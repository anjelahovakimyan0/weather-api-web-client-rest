package am.itspace.photoshootprojectmanagementrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = {"am.itspace.photoshootprojectmanagementrest",
        "am.itspace.photoshootprojectmanagementcommon"})
@EntityScan(basePackages = {"am.itspace.photoshootprojectmanagementcommon"})
@EnableJpaRepositories(basePackages = {"am.itspace.photoshootprojectmanagementcommon"})public class PhotoshootProjectManagementRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoshootProjectManagementRestApplication.class, args);
    }

}
