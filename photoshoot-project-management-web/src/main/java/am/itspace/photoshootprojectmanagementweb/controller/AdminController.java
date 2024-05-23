package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.entity.Role;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.ReviewService;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final UserService userService;

    private final ReviewService reviewService;

    @GetMapping("/home")
    public String adminPage() {
        return "admin/adminPanel";
    }

    @GetMapping("/specifications/update/{userId}")
    @Transactional
    public String updateSpecification(@PathVariable("userId") int userId,
                                      @AuthenticationPrincipal CurrentUser currentUser,
                                      @RequestParam(value = "active", required = false) String activeStr,
                                      @RequestParam(value = "hasLeftReview", required = false) String hasLeftReviewStr,
                                      @RequestParam(value = "deleted", required = false) String deletedStr,
                                      @RequestParam(value = "role", required = false) String roleStr) {

        if (activeStr != null && !activeStr.isEmpty()) {
            if (activeStr.equals("true") || activeStr.equals("false")) {
                User user = userService.findById(userId, currentUser).get();
                user.setActive(Boolean.parseBoolean(activeStr));
                userService.save(user);
            }
        }

        if (hasLeftReviewStr != null && !hasLeftReviewStr.isEmpty()) {
            if (hasLeftReviewStr.equals("true") || hasLeftReviewStr.equals("false")) {
                User user = userService.findById(userId, currentUser).get();
                user.setHasLeftReview(Boolean.parseBoolean(hasLeftReviewStr));

                reviewService.deleteByUserId(userId);
                userService.save(user);
            }
        }

        if (deletedStr != null && !deletedStr.isEmpty()) {
            if (deletedStr.equals("true") || deletedStr.equals("false")) {
                User user = userService.findById(userId, currentUser).get();
                user.setDeleted(Boolean.parseBoolean(deletedStr));
                userService.save(user);
            }
        }

        if (roleStr != null && !roleStr.isEmpty()) {
            Role role = Role.valueOf(roleStr);
            if (role == Role.USER) {
                User user = userService.findById(userId, currentUser).get();
                user.setRole(Role.valueOf(roleStr));
                userService.save(user);
            } else if (role == Role.ADMIN) {
                User user = userService.findById(userId, currentUser).get();
                user.setRole(Role.valueOf(roleStr));

                reviewService.deleteByUserId(userId);
                user.setHasLeftReview(false);

                userService.save(user);
            }
        }

        return "redirect:/users/" + currentUser.getUser().getId();
    }
}
