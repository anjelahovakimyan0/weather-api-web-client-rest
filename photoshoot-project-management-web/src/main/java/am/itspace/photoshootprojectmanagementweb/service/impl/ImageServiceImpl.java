package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.EventCategory;
import am.itspace.photoshootprojectmanagementcommon.entity.Image;
import am.itspace.photoshootprojectmanagementcommon.repository.ImageRepository;
import am.itspace.photoshootprojectmanagementweb.fileComponent.FileComponent;
import am.itspace.photoshootprojectmanagementweb.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final FileComponent fileComponent;

    @Override
    public Image save(Image image, MultipartFile multipartFile, EventCategory eventCategory) {

        log.info("Called save the image method in ImageServiceImpl");

        if (multipartFile != null && !multipartFile.isEmpty()) {

            String picUrl = fileComponent.uploadPicture(multipartFile);

            image.setEventCategory(eventCategory);
            image.setPicUrl(picUrl);
            log.info("image {} is adding to DB", image.getPicUrl());

            return imageRepository.save(image);
        }

        log.warn("Failed to save the image, multipartFile was null");
        return null;
    }

    @Override
    public Page<Image> findAll(Pageable pageable) {
        log.info("Called find all images method in ImageServiceImpl");

        return imageRepository.findAll(pageable);
    }

    @Override
    public Optional<Image> findById(int id) {

        log.info("Called find images by id method in ImageServiceImpl");

        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isEmpty()) {
            throw new EntityNotFoundException("Image with id " + id + "not found");
        }

        return imageOptional;
    }

    @Override
    public Image update(Image updatedImage, MultipartFile multipartFile, EventCategory eventCategory) {

        log.info("called update image method in ImageServiceImpl");
        log.info("Admin is trying to update the image with id {}", updatedImage.getId());

        // Retrieve the existing updatedImage from the database
        Optional<Image> foundImage = findById(updatedImage.getId());
        Image existingImage = foundImage.get();

        // Delete the old file if it exists and a new file is provided
        if (multipartFile != null && !multipartFile.isEmpty() && existingImage.getPicUrl() != null) {
            fileComponent.deletePicture(existingImage.getPicUrl());
        }

        // Update the picUrl if a new file is provided
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String picUrl = fileComponent.uploadPicture(multipartFile);
            existingImage.setPicUrl(picUrl);
        }

        // Update the eventCategory
        existingImage.setEventCategory(eventCategory);

        // Save the updatedImage
        Image saved = imageRepository.save(existingImage);
        log.info("Image saved in repository with id '{}'", updatedImage.getId());
        return saved;
    }

    @Override
    public void deleteById(int id) {

        log.info("Called delete image by {} id method in ImageServiceImpl", id);

        Optional<Image> imageById = findById(id);
        Image image = imageById.get();

        // Delete the file from the local package if it exists
        if (image.getPicUrl() != null) {
            fileComponent.deletePicture(image.getPicUrl());
        }

        imageRepository.deleteById(id);
        log.info("File with id {} was deleted from DB", id);
    }
}
