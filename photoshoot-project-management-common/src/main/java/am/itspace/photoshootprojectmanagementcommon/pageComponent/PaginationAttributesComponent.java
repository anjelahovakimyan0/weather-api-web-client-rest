package am.itspace.photoshootprojectmanagementcommon.pageComponent;

import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class PaginationAttributesComponent {

    public void addPaginationAttributes(ModelMap modelMap, int page, int size,
                                        String orderBy, String order, int totalPages) {

        modelMap.addAttribute("currentPage", page);
        modelMap.addAttribute("size", size);
        modelMap.addAttribute("orderBy", orderBy);
        modelMap.addAttribute("order", order);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();

            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
    }

    public void paginationAttributesWithoutSort(ModelMap modelMap, int page, int size, int totalPages) {

        modelMap.addAttribute("currentPage", page);
        modelMap.addAttribute("size", size);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();

            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
    }
}
