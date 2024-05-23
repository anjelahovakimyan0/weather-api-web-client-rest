package am.itspace.photoshootprojectmanagementrest.dto.reviews;

import am.itspace.photoshootprojectmanagementcommon.entity.Rate;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSaveDto {

    private String content;

    private User user;

    private Rate rate;

}
