package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.pageComponent.PaginationAttributesComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.Booking;
import am.itspace.photoshootprojectmanagementcommon.entity.Discount;
import am.itspace.photoshootprojectmanagementcommon.entity.Document;
import am.itspace.photoshootprojectmanagementcommon.entity.Role;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.BookingService;
import am.itspace.photoshootprojectmanagementweb.service.DiscountService;
import am.itspace.photoshootprojectmanagementweb.service.DocumentService;
import am.itspace.photoshootprojectmanagementweb.service.ReviewService;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import am.itspace.photoshootprojectmanagementweb.service.VerificationService;
import jakarta.transaction.Transactional;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final BookingService bookingService;

    private final DocumentService documentService;

    private final DiscountService discountService;

    private final ReviewService reviewService;

    private final PaginationAttributesComponent attributesComponent;

    private final VerificationService verificationService;

    @GetMapping
    public String usersPage(ModelMap modelMap,
                            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "2") int size,
                            @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                            @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<User> usersPage = userService.findAllByIsDeleted(false, pageable);
        modelMap.addAttribute("users", usersPage);

        attributesComponent.addPaginationAttributes(modelMap, page, size, orderBy, order, usersPage.getTotalPages());

        return "admin/usersPage";
    }

    @GetMapping("/filter")
    public String findAllByFilter(ModelMap modelMap,
                                  @RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "phone", required = false) String phone,
                                  @RequestParam(value = "email", required = false) String email,
                                  @RequestParam(value = "registerDateFrom", required = false) String registerDateFrom,
                                  @RequestParam(value = "registerDateTo", required = false) String registerDateTo,

                                  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "size", required = false, defaultValue = "3") int size) {

        Pageable pageRequest = PageRequest.of(page - 1, size);

        Page<User> usersPage = userService.findAllByFilter(pageRequest, name, phone, email,
                registerDateFrom, registerDateTo, page, size);

        modelMap.addAttribute("name", name);
        modelMap.addAttribute("phone", phone);
        modelMap.addAttribute("email", email);
        modelMap.addAttribute("registerDateFrom", registerDateFrom);
        modelMap.addAttribute("registerDateTo", registerDateTo);
        modelMap.addAttribute("users", usersPage);

        attributesComponent.paginationAttributesWithoutSort(
                modelMap, page, size, usersPage.getTotalPages());

        return "admin/usersPageFilter";
    }

    @GetMapping("/register")
    public String registerPage(ModelMap modelMap,
                               @RequestParam(value = "msg", required = false) String msg) {
        if (msg != null && !msg.isEmpty()) {
            modelMap.addAttribute("msg", msg);
        }

        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam("picture") MultipartFile multipartFile) {
        userService.register(user, multipartFile);

        return "users/register";
    }

    @GetMapping("/loginPage")
    public String loginPage(@AuthenticationPrincipal CurrentUser currentUser) {

        if (currentUser == null) {
            return "loginPage";
        }

        return "redirect:/users/loginSuccess";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal CurrentUser currentUser) {
        return (currentUser.getUser().getRole() == Role.ADMIN)
                ? "redirect:/admin/home"
                : "redirect:/";
    }

    @GetMapping("/admin/home")
    public String adminPage() {
        return "admin/home";
    }

    @GetMapping("/update/{id}")
    public String updatePage(ModelMap modelMap,
                             @PathVariable("id") int id,
                             @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findById(id, currentUser).get());
        return "";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute User user,
                         @RequestParam("picture") MultipartFile multipartFile,
                         @AuthenticationPrincipal CurrentUser currentUser) {
        userService.update(user, multipartFile, currentUser);
        return "";
    }

    @GetMapping("/{id}")
    public String findById(ModelMap modelMap,
                           @PathVariable("id") int id,
                           @AuthenticationPrincipal CurrentUser currentUser) {
        userService.findById(id, currentUser);

        User user = userService.findById(id, currentUser).get();

        List<Document> documentList = documentService.findByUserId(user.getId());

        Set<Discount> discountsByUser = new HashSet<>();
        for (Document document : documentList) {
            discountsByUser.add(document.getDiscount());
        }

        modelMap.addAttribute("user", user);
        modelMap.addAttribute("documents", documentList);
        modelMap.addAttribute("discounts", discountsByUser);
        modelMap.addAttribute("discountsList", discountService.findAll());

        if (user.isHasLeftReview()) {
            modelMap.addAttribute("review", reviewService.findByUserId(user.getId()).get());
        }

        return "users/singleUserPage";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") int id,
                             @AuthenticationPrincipal CurrentUser currentUser) {
        userService.deleteById(id, currentUser);

        return "redirect:/users";
    }

    @GetMapping("/discounts/delete")
    @Transactional
    public String deleteUserDiscount(@RequestParam("discountId") int discountId,
                                     @AuthenticationPrincipal CurrentUser currentUser) {

        documentService.deleteByDiscountId(discountId);

        List<Booking> bookings = bookingService.findByUserId(currentUser.getUser().getId());
        for (Booking booking : bookings) {
            List<Discount> discounts = booking.getDiscounts();
            for (Discount discount : discounts) {
                if (discount.getId() == discountId) {
                    discounts.remove(discount);
                    break;
                }
            }
        }

        return "redirect:/users/" + currentUser.getUser().getId();
    }

    @GetMapping("/deletePicture/{id}")
    public String deletePicture(@PathVariable("id") int id,
                                @AuthenticationPrincipal CurrentUser currentUser) {
        userService.deletePicture(id, currentUser);

        return "redirect:/users/" + id;
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token") String token) {

        if (verificationService.verifyUser(token)) {
            return "redirect:/users/loginSuccess";
        }

        return "redirect:/";
    }
}
