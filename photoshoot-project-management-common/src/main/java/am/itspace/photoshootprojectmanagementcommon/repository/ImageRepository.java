package am.itspace.photoshootprojectmanagementcommon.repository;

import am.itspace.photoshootprojectmanagementcommon.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
}
