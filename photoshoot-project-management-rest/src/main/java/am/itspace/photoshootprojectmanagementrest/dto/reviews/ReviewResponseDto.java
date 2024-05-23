package am.itspace.photoshootprojectmanagementrest.dto.reviews;

import am.itspace.photoshootprojectmanagementcommon.entity.Rate;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

    private int id;

    private String content;

    private UserResponseDto userResponseDto;

    private Date leftDatetime;

    private Rate rate;

}
