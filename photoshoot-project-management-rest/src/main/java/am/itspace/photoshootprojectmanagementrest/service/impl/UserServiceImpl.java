package am.itspace.photoshootprojectmanagementrest.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.QUser;
import am.itspace.photoshootprojectmanagementrest.dto.pagination.PagingResponseDto;
import am.itspace.photoshootprojectmanagementrest.fileComponent.FileComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.Role;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.repository.UserRepository;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserSaveDto;
import am.itspace.photoshootprojectmanagementrest.mapper.UserMapper;
import am.itspace.photoshootprojectmanagementrest.service.UserService;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import am.itspace.photoshootprojectmanagementcommon.exception.UserExistException;

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

    private final UserMapper userMapper;

    private final FileComponent fileComponent;

    private final EntityManager entityManager;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public UserResponseDto register(UserSaveDto userSaveDto,
                                    MultipartFile multipartFile) {

        Optional<User> userOptional = findByEmail(userMapper.map(userSaveDto).getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isDeleted()) {
                throw new UserExistException(
                        "User with email " + userSaveDto.getEmail() + " already exists!");
            } else {
                user.setDeleted(false);
                userRepository.save(user);

                return userMapper.map(user);
            }
        }

        User user = userMapper.map(userSaveDto);

        user.setDeleted(false);
        user.setActive(false);
        user.setRegisterDate(new Date());
        user.setHasLeftReview(false);
        user.setRole(Role.USER);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            user.setAvatarUrl(fileComponent.uploadPicture(multipartFile));
        }

        String activationToken = UUID.randomUUID().toString();
        user.setEmailToken(activationToken);

        User savedUser = userRepository.save(user);

        return userMapper.map(savedUser);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public PagingResponseDto findAll(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponseDto> userResponseDtos = new ArrayList<>();

        for (User user : usersPage.getContent()) {
            userResponseDtos.add(userMapper.map(user));
        }

        return  PagingResponseDto.builder()
                .data(userResponseDtos)
                .size(pageable.getPageSize())
                .page(usersPage.getPageable().getPageNumber())
                .totalElements(usersPage.getTotalElements())
                .build();
    }

    @Override
    public PagingResponseDto findAllByFilter(Pageable pageRequest, String name, String phone, String email,
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


        return PagingResponseDto.builder()
                .data(usersPage)
                .size(size)
                .page(page)
                .totalElements(users.size())
                .build();
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
    public Optional<UserResponseDto> findById(int id) {
        Optional<User> user = userRepository.findById(id);

//        if ((user.isPresent() && user.get().isDeleted()) || user.isEmpty()
//                || (currentUser.getUser().getRole() == Role.USER && currentUser.getUser().getId() != id)) {
//
//            throw new EntityNotFoundException("User with id " + id + " does not exist!");
//        }

        return Optional.of(userMapper.map(user.get()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PagingResponseDto findByIsDeleted(boolean deleted, Pageable pageable) {
        log.info("Called findByIsDeleted()");

        Page<User> usersByIsDeleted = userRepository.findByIsDeleted(deleted, pageable);

        return PagingResponseDto.builder()
                .data(usersByIsDeleted)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(usersByIsDeleted.getTotalElements())
                .build();
    }

    @Override
    public UserResponseDto update(int id, UserSaveDto userSaveDto, MultipartFile multipartFile) {
//        if (currentUser.getUser().getRole() == Role.USER
//                && currentUser.getUser().getId() != user.getId()) {
//            throw new EntityNotFoundException("User does not exists!");
//        }

        Optional<User> userOptional = findUserById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not update user: User with id " + id + " does not exist!");
        }

        User user = userMapper.map(userSaveDto);
        User userById = userOptional.get();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            user.setAvatarUrl(fileComponent.uploadPicture(multipartFile));
        } else {
            user.setAvatarUrl(userSaveDto.getAvatarUrl());
        }

        user.setId(id);
        user.setRegisterDate(userById.getRegisterDate());
        user.setActive(userById.isActive());
        user.setHasLeftReview(userById.isHasLeftReview());
        user.setDeleted(userById.isDeleted());
        user.setRole(userById.getRole());

        User updatedUser = userRepository.save(user);

        log.info("User updated by user with email {}", user.getEmail());

        return userMapper.map(updatedUser);
    }

    @Override
    public void deleteById(int id) {
        Optional<User> userOptional = findUserById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete user: User with id " + id + " does not exist!");
        }

        User user = userOptional.get();

        user.setDeleted(true);
        user.setActive(false);

        userRepository.save(user);
    }

    @Override
    public void deletePicture(int id) {
        Optional<User> userOptional = findUserById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete user: User with id " + id + " does not exist!");
        }

        User user = userOptional.get();

        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(null);
            userRepository.save(user);

            fileComponent.deletePicture(avatarUrl);
        }
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }
}
