package am.itspace.photoshootprojectmanagementrest.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Review;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.repository.ReviewRepository;
import am.itspace.photoshootprojectmanagementrest.dto.pagination.PagingResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewSaveDto;
import am.itspace.photoshootprojectmanagementrest.mapper.ReviewMapper;
import am.itspace.photoshootprojectmanagementrest.service.ReviewService;
import am.itspace.photoshootprojectmanagementrest.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponseDto save(ReviewSaveDto reviewSaveDto) {
//        Optional<User> userOptional = userService.findUserById(currentUser.getId());

        Optional<User> userOptional = userService.findUserById(18);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not find current user: User with id " +
                            reviewSaveDto.getUser().getId() + " does not exist!");
        }

        User user = userOptional.get();

        if (user.isHasLeftReview()) {
            // TODO overwrite
        }

        Review review = reviewMapper.map(reviewSaveDto);

        review.setUser(user);
        review.setLeftDatetime(new Date());

        Review savedReview = reviewRepository.save(review);

        user.setHasLeftReview(true);
        userService.save(user);

        return reviewMapper.map(savedReview);
    }

    @Override
    public PagingResponseDto findAll(Pageable pageable) {
        Page<Review> reviewsPage = reviewRepository.findAll(pageable);

        return PagingResponseDto.builder()
                .data(reviewsPage)
                .page(pageable.getPageNumber())
                .size(reviewsPage.getSize())
                .totalElements(reviewsPage.getTotalElements())
                .build();
    }

    @Override
    public Optional<ReviewResponseDto> findById(int id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Review with id " + id + " does not exist!");
        }

        return Optional.of(reviewMapper.map(reviewOptional.get()));
    }

    public Optional<Review> findReviewById(int id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Optional<ReviewResponseDto> findByUserId(int userId) {
        Optional<Review> reviewOptional = reviewRepository.findByUserId(userId);
        if (reviewOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Review with user id " + userId + " does not exist!");
        }

        return Optional.of(
                reviewMapper.map(reviewOptional.get()));
    }

    @Override
    public ReviewResponseDto update(int id, ReviewSaveDto reviewSaveDto) {
        Optional<Review> reviewOptional = findReviewById(id);
        if (reviewOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not update review. Review with id " + id + " does not exist!");
        }

        Review review = reviewMapper.map(reviewSaveDto);
        review.setId(id);

        return reviewMapper.map(
                reviewRepository.save(review));
    }

    @Override
    public void deleteById(int id) {
        Optional<Review> reviewOptional = findReviewById(id);
        if (reviewOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete review. Review with id " + id + " does not exist!");
        }

        reviewRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(int userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete review by user id. User with id " + userId + " does not exist!");
        }

        // TODO user only can delete it's review

        reviewRepository.deleteByUserId(userId);
    }
}
