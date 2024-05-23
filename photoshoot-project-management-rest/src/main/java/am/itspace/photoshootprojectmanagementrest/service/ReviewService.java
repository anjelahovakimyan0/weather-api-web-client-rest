package am.itspace.photoshootprojectmanagementrest.service;

import am.itspace.photoshootprojectmanagementrest.dto.pagination.PagingResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewSaveDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {

    ReviewResponseDto save(ReviewSaveDto reviewSaveDto);

    PagingResponseDto findAll(Pageable pageable);

    Optional<ReviewResponseDto> findById(int id);

    Optional<ReviewResponseDto> findByUserId(int userId);

    ReviewResponseDto update(int id, ReviewSaveDto reviewSaveDto);

    void deleteById(int id);

    void deleteByUserId(int userId);

}
