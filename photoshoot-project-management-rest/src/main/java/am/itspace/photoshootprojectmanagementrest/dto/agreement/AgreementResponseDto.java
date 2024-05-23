package am.itspace.photoshootprojectmanagementrest.dto.agreement;

import am.itspace.photoshootprojectmanagementcommon.entity.Booking;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgreementResponseDto {

    private int id;

    private String name;

    private double price;

    private Booking booking;

    private User user;

}
