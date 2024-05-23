package am.itspace.photoshootprojectmanagementrest.service.impl;

import am.itspace.photoshootprojectmanagementrest.dto.weatherApi.WeatherInfoDto;
import am.itspace.photoshootprojectmanagementrest.service.WeatherService;
import am.itspace.photoshootprojectmanagementrest.weather.WeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherClient weatherClient;

    @Override
    public Mono<WeatherInfoDto> getWeatherByLocation(String location) {
        return weatherClient.getWeatherByLocation(location);
    }
}
