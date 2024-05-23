package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Review;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.repository.ReviewRepository;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.ReviewService;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
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

    @Override
    public Review save(Review review, CurrentUser currentUser) {
        log.info("User with email {} called save()",
                currentUser.getUser().getEmail());

        review.setUser(currentUser.getUser());
        review.setLeftDatetime(new Date());

        Review savedReview = reviewRepository.save(review);

        User user = userService.findById(currentUser.getUser().getId(), currentUser).get();
        user.setHasLeftReview(true);
        userService.save(user);

        log.info("Review was left successfully by User with email {}",
                currentUser.getUser().getEmail());

        return savedReview;
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        log.info("Called findAll()");

        return reviewRepository.findAll(pageable);
    }

    @Override
    public Optional<Review> findById(int id) {
        log.info("Called findById()");

        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            throw new EntityNotFoundException("Review does not exists!");
        }

        log.info("findById(): user with id {} was found", id);

        return reviewOptional;
    }

    @Override
    public Optional<Review> findByUserId(int userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Review update(Review review, CurrentUser currentUser) {
        log.info("User with email {} called update() for review id {}",
                review.getId(), currentUser.getUser().getEmail());

        findById(review.getId());

        if (currentUser.getUser().isHasLeftReview()) {
            // TODO overwrite
        }

        Review updatedReview = reviewRepository.save(review);

        log.info("Review updated successfully by User with email {}",
                currentUser.getUser().getEmail());

        return updatedReview;
    }

    @Override
    public void deleteById(int id, CurrentUser currentUser) {
        findById(id);

        log.info("User with email {} called deleteById()",
                currentUser.getUser().getEmail());

        reviewRepository.deleteById(id);

        log.info("Review with id {} deleted successfully by User with email {}",
                id, currentUser.getUser().getEmail());
    }

    @Override
    public void deleteByUserId(int userId) {
        reviewRepository.deleteByUserId(userId);
    }
}
