package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    User register(User user, MultipartFile multipartFile);

    User save(User user);

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(int id, CurrentUser currentUser);

    Optional<User> findByEmail(String email);

    Page<User> findAllByIsDeleted(boolean deleted, Pageable pageable);

    Page<User> findAllByFilter(Pageable pageRequest, String name, String phone, String email,
                               String registerDateFrom, String registerDateTo,
                               int page, int size);

    User update(User user, MultipartFile multipartFile, CurrentUser currentUser);

    void deleteById(int id, CurrentUser currentUser);

    void deletePicture(int id, CurrentUser currentUser);

    User findByEmailToken(String token);

}
