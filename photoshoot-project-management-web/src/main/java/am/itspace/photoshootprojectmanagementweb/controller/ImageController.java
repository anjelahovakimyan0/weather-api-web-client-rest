package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.pageComponent.PaginationAttributesComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.EventCategory;
import am.itspace.photoshootprojectmanagementcommon.entity.Image;
import am.itspace.photoshootprojectmanagementweb.service.EventCategoryService;
import am.itspace.photoshootprojectmanagementweb.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    private final PaginationAttributesComponent paginationAttributesComponent;

    private final EventCategoryService eventCategoryService;

    @GetMapping
    public String imagesPage(ModelMap modelMap,
                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             @RequestParam(value = "size", required = false, defaultValue = "3") int size,
                             @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                             @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Image> imagesPage = imageService.findAll(pageable);

        modelMap.addAttribute("images", imagesPage);
        modelMap.addAttribute("events", eventCategoryService.findAll());

        int totalPages = imagesPage.getTotalPages();
        paginationAttributesComponent.addPaginationAttributes(modelMap, page, size, orderBy, order, totalPages);

        return "users/images";
    }

    @GetMapping("/create")
    public String createImagePage(ModelMap modelMap) {
        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());

        return "admin/createImage";
    }

    @PostMapping("/create")
    public String image(@Valid @ModelAttribute Image image,
                        @RequestParam("file") MultipartFile multipartFile,
                        @RequestParam("eventCategory") EventCategory eventCategory) {

        imageService.save(image, multipartFile, eventCategory);

        return "redirect:/images";
    }

    @GetMapping("/update/{id}")
    public String updatePage(ModelMap modelMap, @PathVariable("id") int id) {

        modelMap.addAttribute("image", imageService.findById(id).get());
        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());

        return "admin/updateImage";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Image image,
                         @RequestParam("file") MultipartFile multipartFile,
                         @RequestParam("eventCategory") EventCategory eventCategory) {
        imageService.update(image, multipartFile, eventCategory);

        return "redirect:/images";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") int id) {
        imageService.deleteById(id);

        return "redirect:/images";
    }
}
