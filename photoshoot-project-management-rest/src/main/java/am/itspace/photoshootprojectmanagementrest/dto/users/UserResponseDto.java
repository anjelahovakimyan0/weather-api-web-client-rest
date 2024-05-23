package am.itspace.photoshootprojectmanagementrest.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private int id;

    private String name;

    private String surname;

    private String phone;

    private Date registerDate;

    private String avatarUrl;

    private String email;

}
