package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.EventCategory;
import am.itspace.photoshootprojectmanagementcommon.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Image save(Image image, MultipartFile multipartFile, EventCategory eventCategory);

    Page<Image> findAll(Pageable pageable);

    Optional<Image> findById(int id);

    Image update(Image image, MultipartFile multipartFile, EventCategory eventCategory);

    void deleteById(int id);

}
