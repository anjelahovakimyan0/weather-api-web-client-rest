package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.pageComponent.PaginationAttributesComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.Review;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.ReviewService;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    private final UserService userService;

    private final PaginationAttributesComponent attributesComponent;

    @GetMapping
    public String reviewsPage(ModelMap modelMap,
                              @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                              @RequestParam(value = "size", required = false, defaultValue = "3") int size,
                              @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                              @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Review> reviewsPage = reviewService.findAll(pageable);
        modelMap.addAttribute("reviews", reviewsPage);

        attributesComponent.addPaginationAttributes(modelMap, page, size, orderBy, order, reviewsPage.getTotalPages());

        return "";
    }

    @GetMapping("/create")
    public String createReviewPage(ModelMap modelMap, @RequestParam(value = "msg", required = false) String msg) {
        if (!msg.isEmpty()) {
            modelMap.addAttribute("msg", msg);
        }

        return "";
    }

    @PostMapping("/create")
    public String createReview(@ModelAttribute Review review,
                               @AuthenticationPrincipal CurrentUser currentUser) {

        if (currentUser.getUser().isHasLeftReview()) {
            return "redirect:/create?msg=You already had one left review";
        }

        reviewService.save(review, currentUser);

        return "";
    }

    @GetMapping("/update/{id}")
    public String updatePage(ModelMap modelMap,
                             @PathVariable("id") int id,
                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             @RequestParam(value = "size", required = false, defaultValue = "3") int size,
                             @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                             @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        modelMap.addAttribute("review", reviewService.findById(id).get());

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<User> usersPage = userService.findAllByIsDeleted(false, pageable);
        modelMap.addAttribute("users", usersPage);

        int totalPages = usersPage.getTotalPages();
        attributesComponent.addPaginationAttributes(modelMap, page, size, orderBy, order, totalPages);

        return "";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Review review,
                         @AuthenticationPrincipal CurrentUser currentUser) {
        reviewService.update(review, currentUser);

        return "";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") int id,
                             @AuthenticationPrincipal CurrentUser currentUser) {
        reviewService.deleteById(id, currentUser);

        return "";
    }
}
