package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.pageComponent.PaginationAttributesComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.Booking;
import am.itspace.photoshootprojectmanagementcommon.entity.Document;
import am.itspace.photoshootprojectmanagementcommon.entity.Role;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.AgreementService;
import am.itspace.photoshootprojectmanagementweb.service.BookingService;
import am.itspace.photoshootprojectmanagementweb.service.DiscountService;
import am.itspace.photoshootprojectmanagementweb.service.DocumentService;
import am.itspace.photoshootprojectmanagementweb.service.EventCategoryService;
import am.itspace.photoshootprojectmanagementweb.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    private final EventCategoryService eventCategoryService;

    private final DiscountService discountService;

    private final DocumentService documentService;

    private final SpecialtyService specialtyService;


    private final PaginationAttributesComponent attributesComponent;

    private final AgreementService agreementService;

    @GetMapping
    public String bookingsPage(ModelMap modelMap,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "size", required = false, defaultValue = "3") int size,
                               @RequestParam(value = "orderBy", required = false, defaultValue = "bookingDate") String orderBy,
                               @RequestParam(value = "order", required = false, defaultValue = "DESC") String order,
                               @AuthenticationPrincipal CurrentUser currentUser) {

        // Create a Pageable object based on pagination parameters
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.fromString(order), orderBy);

        // Retrieve bookings based on user role
        Page<Booking> bookingsPage = null;
        List<Booking> bookingsList = null;

        if (currentUser.getUser().getRole() == Role.ADMIN) {
            bookingsPage = bookingService.findAll(pageable); // Admins see all bookings
        } else {
            bookingsList = bookingService.findByUserId(currentUser.getUser().getId());  // Regular users see only their bookings
        }

        Pageable pageableAgreements = PageRequest.of(page - 1, size); // Default pagination without specific sorting

        if (bookingsPage != null && !bookingsPage.isEmpty()) {
            // Generate page numbers for pagination links if multiple pages exist
            attributesComponent.addPaginationAttributes(modelMap, page, size,
                    orderBy, order, bookingsPage.getTotalPages());
            modelMap.addAttribute("bookings", bookingsPage);
        } else if (bookingsList != null && !bookingsList.isEmpty()) {
            // Generate page numbers for pagination links if multiple pages exist
            attributesComponent.addPaginationAttributes(modelMap, page, size,
                    orderBy, order, bookingsList.size());
            modelMap.addAttribute("bookings", bookingsList);
        }


        modelMap.addAttribute("agreements", agreementService.findAll(pageableAgreements.first(), currentUser));

        return "users/bookingsPage";
    }

    @GetMapping("/create")
    public String createBookingPage(ModelMap modelMap,
                                    @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());
        modelMap.addAttribute("discounts", discountService.findAll());
        modelMap.addAttribute("specialties", specialtyService.findAll());

        List<Document> documents = documentService.findByUserId(currentUser.getUser().getId());
        modelMap.addAttribute("documents", documents);

        return "users/createBooking";
    }

    @PostMapping("/create")
    public String createBooking(@Valid @ModelAttribute Booking booking,
                                @RequestParam("documents") List<MultipartFile> multipartFiles,
                                @AuthenticationPrincipal CurrentUser currentUser) {

        bookingService.save(booking, currentUser, multipartFiles);

        return "redirect:/bookings";
    }

    @GetMapping("/update/{id}")
    public String updatePage(ModelMap modelMap,
                             @PathVariable("id") int id) {

        modelMap.addAttribute("booking", bookingService.findById(id).get());
        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());
        modelMap.addAttribute("specialties", specialtyService.findAll());
        modelMap.addAttribute("discounts", discountService.findAll());

        return "users/updateBooking";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Booking booking,
                         @AuthenticationPrincipal CurrentUser currentUser) {

        booking.setUser(bookingService.findById(booking.getId()).get().getUser());
        bookingService.update(booking);

        return "redirect:/bookings";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") int id) {

        bookingService.deleteById(id);

        return "redirect:/bookings";
    }
}
