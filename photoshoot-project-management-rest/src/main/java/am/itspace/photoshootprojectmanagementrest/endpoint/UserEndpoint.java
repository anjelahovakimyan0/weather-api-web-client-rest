package am.itspace.photoshootprojectmanagementrest.endpoint;

import am.itspace.photoshootprojectmanagementrest.dto.pagination.PagingResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.users.UserSaveDto;
import am.itspace.photoshootprojectmanagementrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/users")
public class UserEndpoint {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserSaveDto userSaveDto,
                                                    @RequestParam(value = "picture", required = false) MultipartFile multipartFile) {

        return ResponseEntity.ok(userService.register(userSaveDto, multipartFile));
    }

    @GetMapping
    public ResponseEntity<PagingResponseDto> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
            @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagingResponseDto> findAllByFilter(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "registerDateFrom", required = false) String registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) String registerDateTo,

            @RequestParam(value = "page", required = false, defaultValue = "2") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        PagingResponseDto usersByFilter = userService.findAllByFilter(pageRequest, name, phone, email,
                registerDateFrom, registerDateTo, page, size);


        return ResponseEntity.ok(usersByFilter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findById(id).get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable int id,
                                                  @RequestBody UserSaveDto userSaveDto,
                                                  @RequestParam(value = "picture", required = false) MultipartFile multipartFile) {

        return ResponseEntity.ok(userService.update(id, userSaveDto, multipartFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteById(@PathVariable int id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deletePicture/{id}")
    public String deletePicture(@PathVariable("id") int id) {
        userService.deletePicture(id);

        return "redirect:/users/" + id;
    }
}
