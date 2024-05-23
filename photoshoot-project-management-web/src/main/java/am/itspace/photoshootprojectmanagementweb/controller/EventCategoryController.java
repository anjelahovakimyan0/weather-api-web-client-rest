package am.itspace.photoshootprojectmanagementweb.controller;

import am.itspace.photoshootprojectmanagementcommon.entity.EventCategory;
import am.itspace.photoshootprojectmanagementweb.service.EventCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/eventCategories")
@Slf4j
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;

    @GetMapping
    public String eventCategoryPage(ModelMap modelMap) {

        modelMap.addAttribute("eventCategories", eventCategoryService.findAll());

        return "users/events";
    }

    @GetMapping("/create")
    public String createCategoryPage() {
        return "admin/createEvent";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute EventCategory eventCategory) {

        eventCategoryService.save(eventCategory);

        return "redirect:/eventCategories";
    }

    @GetMapping("/update/{id}")
    public String updatePage(ModelMap modelMap,
                             @PathVariable("id") int id) {

        modelMap.addAttribute("eventCategory", eventCategoryService.findById(id).get());

        return "admin/updateEvent";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute EventCategory eventCategory) {

        eventCategoryService.update(eventCategory);

        return "redirect:/eventCategories";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") int id) {

        eventCategoryService.deleteById(id);
        return "redirect:/eventCategories";
    }
}
