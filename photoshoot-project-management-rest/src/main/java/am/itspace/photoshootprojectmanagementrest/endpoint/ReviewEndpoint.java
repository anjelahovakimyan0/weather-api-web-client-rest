package am.itspace.photoshootprojectmanagementrest.endpoint;

import am.itspace.photoshootprojectmanagementrest.dto.pagination.PagingResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewResponseDto;
import am.itspace.photoshootprojectmanagementrest.dto.reviews.ReviewSaveDto;
import am.itspace.photoshootprojectmanagementrest.dto.weatherApi.WeatherInfoDto;
import am.itspace.photoshootprojectmanagementrest.service.ReviewService;
import am.itspace.photoshootprojectmanagementrest.service.WeatherService;
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
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/reviews")
public class ReviewEndpoint {

    private final ReviewService reviewService;

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<Mono<WeatherInfoDto>> getWeather(@RequestParam("location") String location) {
        return ResponseEntity.ok(weatherService.getWeatherByLocation(location));
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDto> save(@RequestBody ReviewSaveDto reviewSaveDto) {
        return ResponseEntity.ok(reviewService.save(reviewSaveDto));
    }

    @GetMapping
    public ResponseEntity<PagingResponseDto> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
            @RequestParam(value = "order", required = false, defaultValue = "DESC") String order) {

        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(reviewService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> findById(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.findById(id).get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> update(@PathVariable int id,
                                                    @RequestBody ReviewSaveDto reviewSaveDto) {
        return ResponseEntity.ok(reviewService.update(id, reviewSaveDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> deleteById(@PathVariable int id) {

        Optional<ReviewResponseDto> reviewResponseDto = reviewService.findById(id);
        if (reviewResponseDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
