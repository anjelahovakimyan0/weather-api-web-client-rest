package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Booking;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking save(Booking booking, CurrentUser currentUser, List<MultipartFile> multipartFiles);

    Page<Booking> findAll(Pageable pageable);

    Optional<Booking> findById(int id);

    Booking update(Booking booking);

    void deleteById(int id);

    List<Booking> findByUserId(int userId);

}
