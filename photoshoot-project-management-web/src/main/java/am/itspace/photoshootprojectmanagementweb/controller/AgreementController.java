package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.pageComponent.PaginationAttributesComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.Agreement;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.AgreementService;
import am.itspace.photoshootprojectmanagementweb.service.BookingService;
import am.itspace.photoshootprojectmanagementweb.service.EventCategoryService;
import am.itspace.photoshootprojectmanagementweb.service.SpecialtyService;
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
@RequestMapping("/agreements")
public class AgreementController {

    private final AgreementService agreementService;

    private final PaginationAttributesComponent paginationAttributesComponent;

    private final BookingService bookingService;

    private final SpecialtyService specialtyService;

    private final EventCategoryService eventCategoryService;

    @GetMapping
    public String agreementsPage(ModelMap modelMap,
                                 @AuthenticationPrincipal CurrentUser currentUser,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                 @RequestParam(value = "size", required = false, defaultValue = "3") int size,
                                 @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                                 @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Agreement> agreementsPage = agreementService.findAll(pageable, currentUser);
        modelMap.addAttribute("agreements", agreementsPage);

        paginationAttributesComponent.addPaginationAttributes(
                modelMap, page, size, orderBy, order, agreementsPage.getTotalPages());

        return "admin/agreementsPage";
    }

    @GetMapping("/create")
    public String createAgreementPage(ModelMap modelMap,
                                      @RequestParam("bookingId") int bookingId) {
        modelMap.addAttribute("booking", bookingService.findById(bookingId).get());
        modelMap.addAttribute("specialties", specialtyService.findAll());
        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());
        return "admin/createAgreement";
    }

    @PostMapping("/create")
    public String createAgreement(@RequestParam("bookingId") int bookingId,
                                  @ModelAttribute Agreement agreement) {
        agreementService.save(bookingId, agreement);
        return "redirect:/agreements";
    }

    @GetMapping("/update/{id}")
    public String updatePage(ModelMap modelMap,
                             @PathVariable("id") int id) {

        modelMap.addAttribute("agreement", agreementService.findById(id).get());
        modelMap.addAttribute("specialties", specialtyService.findAll());
        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());
        return "admin/updateAgreement";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Agreement agreement) {
        agreementService.update(agreement);
        return "redirect:/agreements";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") int id) {
        agreementService.deleteById(id);
        return "redirect:/agreements";
    }

    @GetMapping("/send/{id}")
    public String sendAgreementByEmail(@PathVariable("id") int id) {
        Agreement agreement = agreementService.findById(id).get();
        User user = agreement.getBooking().getUser();
//        agreementService.sendAgreementByEmail(user, agreement);
        return "redirect:/agreements";
    }
}
