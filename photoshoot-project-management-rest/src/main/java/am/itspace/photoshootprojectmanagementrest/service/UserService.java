package am.itspace.photoshootprojectmanagementrest.service;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementrest.dto.pagination.PagingResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserSaveDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    UserResponseDto register(UserSaveDto userSaveDto, MultipartFile multipartFile);

    User save(User user);

    PagingResponseDto findAll(Pageable pageable);

    Optional<UserResponseDto> findById(int id);

    Optional<User> findUserById(int id);

    Optional<User> findByEmail(String email);

    PagingResponseDto findByIsDeleted(boolean deleted, Pageable pageable);

    PagingResponseDto findAllByFilter(Pageable pageRequest, String name, String phone, String email,
                               String registerDateFrom, String registerDateTo,
                               int page, int size);

    UserResponseDto update(int id, UserSaveDto userSaveDto, MultipartFile multipartFile);

    void deleteById(int id);

    void deletePicture(int id);

}
