package am.itspace.photoshootprojectmanagementrest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "weather")
public record ClientProperties(

        URI visualCrossingUri

) {}
