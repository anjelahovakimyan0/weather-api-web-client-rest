package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Review;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {

    Review save(Review review, CurrentUser currentUser);

    Page<Review> findAll(Pageable pageable);

    Optional<Review> findById(int id);

    Optional<Review> findByUserId(int userId);

    Review update(Review review, CurrentUser currentUser);

    void deleteById(int id, CurrentUser currentUser);

    void deleteByUserId(int userId);

}
