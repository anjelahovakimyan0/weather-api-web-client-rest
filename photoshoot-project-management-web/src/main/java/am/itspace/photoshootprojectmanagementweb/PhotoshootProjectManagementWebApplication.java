package am.itspace.photoshootprojectmanagementweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"am.itspace.photoshootprojectmanagementweb.*",
        "am.itspace.photoshootprojectmanagementcommon.*"})
@EntityScan(basePackages = {"am.itspace.photoshootprojectmanagementcommon.*"})
@EnableJpaRepositories(basePackages = {"am.itspace.photoshootprojectmanagementcommon.*"})
@EnableAsync
public class PhotoshootProjectManagementWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoshootProjectManagementWebApplication.class, args);
    }
}
