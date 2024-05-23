package am.itspace.photoshootprojectmanagementrest.mapper;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserSaveDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto map(User user);

    User map(UserSaveDto userSaveDto);

    User map(UserResponseDto userResponseDto);

    List<UserResponseDto> map(List<User> users);

}
