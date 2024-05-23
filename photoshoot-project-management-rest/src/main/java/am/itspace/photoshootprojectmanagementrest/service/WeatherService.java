package am.itspace.photoshootprojectmanagementrest.service;

import am.itspace.photoshootprojectmanagementrest.dto.weatherApi.WeatherInfoDto;
import reactor.core.publisher.Mono;

public interface WeatherService {

    Mono<WeatherInfoDto> getWeatherByLocation(String location);

}
