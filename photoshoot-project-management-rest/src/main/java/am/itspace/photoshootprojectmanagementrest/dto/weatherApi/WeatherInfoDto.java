package am.itspace.photoshootprojectmanagementrest.dto.weatherApi;

import lombok.Data;
import java.util.List;

@Data
public class WeatherInfoDto {

    private String resolvedAddress;

    private String address;

    private String timezone;

    private String description;

    private List<DayDto> days;

}

@Data
class DayDto {

    private String datetime;

    private double feelslikemax;

    private double feelslikemin;

    private double feelslike;

    private double snow;

    private double snowdepth;

    private double windgust;

    private double windspeed;

    private double winddir;

    private double pressure;

    private double cloudcover;

    private double visibility;

    private double solarradiation;

    private double solarenergy;

    private String sunrise;

    private String sunset;

    private double moonphase;

    private String conditions;

    private String description;

    private String icon;

}

@Data
class HourDto {

    private String datetime;

    private double feelslike;

    private double snow;

    private double snowdepth;

    private double windgust;

    private double windspeed;

    private double winddir;

    private double pressure;

    private double visibility;

    private double cloudcover;

    private double solarradiation;

    private double solarenergy;

    private String conditions;

    private String icon;

}

