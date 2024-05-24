package am.itspace.photoshootprojectmanagementrest.weather;

import am.itspace.photoshootprojectmanagementrest.dto.weatherApi.WeatherInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class WeatherClient {

    private static final String WEATHER_ROOT_API = "/timeline/";

    private static final String WEATHER_API_KEY = "?key=UN9Q6SV34QLBCJXMGWP3QUEBL";

    private final WebClient webClient;

    public Mono<WeatherInfoDto> getWeatherByLocation(String location) {
        return webClient
                .get()
                .uri(WEATHER_ROOT_API + location + WEATHER_API_KEY)
                .retrieve()
                .bodyToMono(WeatherInfoDto.class)
                .timeout(Duration.ofSeconds(3), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class,
                        exception -> Mono.empty())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorResume(Error.class, exception -> Mono.empty());
    }
}
