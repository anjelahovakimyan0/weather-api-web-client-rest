package am.itspace.photoshootprojectmanagementrest.mapper;

import am.itspace.photoshootprojectmanagementcommon.entity.Review;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewSaveDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponseDto map(Review review);

    Review map(ReviewSaveDto reviewSaveDto);

    Review map(ReviewResponseDto reviewResponseDto);

    List<ReviewResponseDto> map(List<Review> reviews);

}
