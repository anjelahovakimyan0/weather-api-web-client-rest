package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.QUser;
import am.itspace.photoshootprojectmanagementcommon.entity.Role;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.exception.UserExistException;
import am.itspace.photoshootprojectmanagementcommon.repository.UserRepository;
import am.itspace.photoshootprojectmanagementweb.fileComponent.FileComponent;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.EmailService;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final FileComponent fileComponent;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final EntityManager entityManager;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public User register(User user, MultipartFile multipartFile) {
        log.info("Called save() for user email {}", user.getEmail());

        Optional<User> userOptional = findByEmail(user.getEmail());

        if (userOptional.isPresent()) {
            User userOpt = userOptional.get();
            if (!userOpt.isDeleted()) {
                throw new UserExistException(
                        "User with email " + user.getEmail() + " already exists!");
            } else {
                userOpt.setDeleted(false);
                userRepository.save(userOpt);

                return userOpt;
            }
        }

        user.setDeleted(false);
        user.setActive(false);
        user.setRegisterDate(new Date());
        user.setHasLeftReview(false);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (multipartFile != null && !multipartFile.isEmpty()) {
            user.setAvatarUrl(fileComponent.uploadPicture(multipartFile));
        }

        String activationToken = UUID.randomUUID().toString();
        user.setEmailToken(activationToken);
        emailService.sendWelcomeMail(user);

        User savedUser = userRepository.save(user);

        log.info("User saved with email {}", savedUser.getEmail());

        return savedUser;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        log.info("Called findAll()");

        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> findAllByFilter(Pageable pageRequest, String name, String phone, String email,
                                      String registerDateFrom, String registerDateTo,
                                      int page, int size) {

        JPAQuery query = new JPAQuery<>(entityManager);

        QUser qUser = QUser.user;
        JPAQueryBase from = query.from(qUser);

        splitSearch(name, registerDateFrom, registerDateTo, from, qUser);

        List<User> users = getPageableUsers(page, size, from, query);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), users.size());

        List<User> pageContent = users.subList(start, end);
        Page<User> usersPage = new PageImpl<>(pageContent, pageRequest, users.size());

        return usersPage;
    }

    private void splitSearch(String name, String registerDateFrom, String registerDateTo,
                             JPAQueryBase from, QUser qUser) {

        if (!StringUtils.isNotBlank(registerDateFrom)
                && !StringUtils.isNotBlank(registerDateTo)
                && StringUtils.isNotBlank(name)) {

            if (name.contains(",")) {
                String[] searchByInputArr = name.split(",");

                List<String> trimmedInputArr = new ArrayList<>();

                for (String input : searchByInputArr) {
                    String trimmedInput = input.trim();
                    trimmedInputArr.add(trimmedInput);
                }

                if (searchByInputArr.length == 3) {
                    searchBy(trimmedInputArr.get(0), trimmedInputArr.get(1), trimmedInputArr.get(2), from, qUser);
                } else {
                    searchBy(trimmedInputArr.get(0), trimmedInputArr.get(1), null, from, qUser);
                }

            } else {
                searchBy(name, null, null, from, qUser);
            }

        } else {
            try {
                List<String> result = getDatesResult(registerDateFrom, registerDateTo);
                filter(sdf.parse(result.get(0)),
                        sdf.parse(result.get(1)), from, qUser);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<String> getDatesResult(String registerDateFrom,
                                        String registerDateTo) {

        if (!StringUtils.isNotBlank(registerDateFrom)
                && !StringUtils.isNotBlank(registerDateTo)) {
            registerDateFrom = sdf.format(new Date());
            registerDateTo = sdf.format(new Date());
        } else if (!StringUtils.isNotBlank(registerDateFrom)) {
            try {
                // based on registerDateTo get yesterday's date as registerDateFrom
                registerDateFrom = sdf.format(sdf.parse(registerDateTo).getTime() - 24 * 60 * 60 * 1000);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (!StringUtils.isNotBlank(registerDateTo)) {
            registerDateTo = sdf.format(new Date());
        }

        return new ArrayList<>(Arrays.asList(registerDateFrom, registerDateTo));
    }

    private void filter(Date registerDateFrom, Date registerDateTo,
                        JPAQueryBase from, QUser qUser) {

        if (registerDateFrom != null && registerDateTo != null) {
            from.where(qUser.registerDate.between(registerDateFrom, registerDateTo));
        } else if (registerDateFrom != null) {
            from.where(qUser.registerDate.after(registerDateFrom));
        } else if (registerDateTo != null) {
            from.where(qUser.registerDate.before(registerDateTo));
        }
    }

    private void searchBy(String name, String phone, String email,
                          JPAQueryBase from, QUser qUser) {

        if (StringUtils.isNotBlank(name)) {
            from.where(qUser.name.contains(name));
        }

        if (StringUtils.isNotBlank(phone)) {
            from.where(qUser.phone.contains(phone));
        }

        if (StringUtils.isNotBlank(email)) {
            from.where(qUser.email.contains(email));
        }
    }

    private List<User> getPageableUsers(int page, int size,
                                        JPAQueryBase from, JPAQuery query) {
        List<User> users = query.fetch();

        if (((page) * users.size()) > 0) {
            from.offset((long) page * users.size());
        }
        from.limit(users.size() > 0 ? users.size() : size);

        return users;
    }

    @Override
    public Optional<User> findById(int id,
                                   CurrentUser currentUser) {

        log.info("Called findById() for id {} by user with email {}", id, currentUser);

        Optional<User> user = userRepository.findById(id);
        if ((user.isPresent() && user.get().isDeleted()) || user.isEmpty()
                || (currentUser.getUser().getRole() == Role.USER && currentUser.getUser().getId() != id)) {

            throw new EntityNotFoundException("User with id " + id + " does not exist!");
        }

        log.info("Id {} was found", id);

        return user;
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Called findByEmail() for email {}", email);

        return userRepository.findByEmail(email);
    }

    @Override
    public Page<User> findAllByIsDeleted(boolean deleted, Pageable pageable) {
        log.info("Called findByIsDeleted()");

        return userRepository.findByIsDeleted(deleted, pageable);
    }

    @Override
    public User update(User user, MultipartFile multipartFile, CurrentUser currentUser) {
        log.info("Called update() by user with email {}", currentUser.getUser().getEmail());

        if (currentUser.getUser().getRole() == Role.USER
                && currentUser.getUser().getId() != user.getId()) {
            throw new EntityNotFoundException("User does not exists!");
        }

        Optional<User> userOptional = findUserById(user.getId());
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not update user: User with id " + user.getId() + " does not exist!");
        }

        User userById = userOptional.get();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            user.setAvatarUrl(fileComponent.uploadPicture(multipartFile));
        } else {
            user.setAvatarUrl(userById.getAvatarUrl());
        }

        user.setRegisterDate(userById.getRegisterDate());
        user.setActive(userById.isActive());
        user.setHasLeftReview(userById.isHasLeftReview());
        user.setDeleted(userById.isDeleted());
        user.setRole(userById.getRole());

        User updatedUser = userRepository.save(user);

        log.info("User updated by user with email {}", user.getEmail());

        return updatedUser;
    }

    @Override
    public void deleteById(int id, CurrentUser currentUser) {
        log.info("Called deleteById() for id {} by user with email {}", id, currentUser.getUser().getEmail());

        Optional<User> userOptional = findUserById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete user: User with id " + id + " does not exist!");
        }

        User user = userOptional.get();

        user.setDeleted(true);
        user.setActive(false);

        userRepository.save(user);

        log.info("User with id {} deleted successfully by user with email {}",
                id, currentUser.getUser().getEmail());
    }

    @Override
    public User findByEmailToken(String token) {
        log.info("Called findByEmailToken() for token {} in UserServiceImpl", token);

        return userRepository.findByEmailToken(token).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    }

    @Override
    public void deletePicture(int id, CurrentUser currentUser) {
        log.info("Called deletePicture() for user id {} by user with email {}", id, currentUser.getUser().getEmail());

        Optional<User> userOptional = findUserById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("Can not delete user: User with id " + id + " does not exist!");
        }

        User user = userOptional.get();

        String avatarUrl = user.getAvatarUrl();

        if (avatarUrl != null && !user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(null);
            userRepository.save(user);

            fileComponent.deletePicture(avatarUrl);
        }

        log.info("deletePicture(): user picture with id {} deleted successfullty by user with email {}",
                id, currentUser.getUser().getEmail());
    }
}
