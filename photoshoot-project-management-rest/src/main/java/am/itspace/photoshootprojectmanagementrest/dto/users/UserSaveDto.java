package am.itspace.photoshootprojectmanagementrest.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSaveDto {

    private String name;

    private String surname;

    private String phone;

    private String avatarUrl;

    private String email;

    private String password;

}
