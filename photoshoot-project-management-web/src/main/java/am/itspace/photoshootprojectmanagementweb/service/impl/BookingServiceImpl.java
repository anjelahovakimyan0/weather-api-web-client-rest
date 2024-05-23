package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementweb.fileComponent.FileComponent;
import am.itspace.photoshootprojectmanagementcommon.entity.Booking;
import am.itspace.photoshootprojectmanagementcommon.entity.Discount;
import am.itspace.photoshootprojectmanagementcommon.entity.Document;
import am.itspace.photoshootprojectmanagementcommon.entity.Status;
import am.itspace.photoshootprojectmanagementcommon.exception.InvalidBookingException;
import am.itspace.photoshootprojectmanagementcommon.repository.BookingRepository;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.BookingService;
import am.itspace.photoshootprojectmanagementweb.service.DiscountService;
import am.itspace.photoshootprojectmanagementweb.service.DocumentService;
import am.itspace.photoshootprojectmanagementweb.service.SpecialtyService;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final DocumentService documentService;

    private final DiscountService discountService;

    private final FileComponent fileComponent;

    private final SpecialtyService specialtyService;

    @Override
    @Transactional
    public Booking save(Booking booking,
                        CurrentUser currentUser,
                        List<MultipartFile> multipartFiles) {

        log.info("Called save booking method in BookingServiceImpl");

        List<Discount> discounts = booking.getDiscounts();
        List<Discount> finalDiscounts = new ArrayList<>();
        List<Document> documents = documentService.findByUserId(currentUser.getUser().getId());

        if (documents.isEmpty()) {
            finalDiscounts.addAll(discounts.stream()
                    .filter(discount -> discount.getId() != 0)
                    .toList());

            booking.setDiscounts(finalDiscounts);
        }

        List<Booking> bookings = bookingRepository.findByUserId(currentUser.getUser().getId());
        List<Discount> discountsByUser = new ArrayList<>();

        for (Booking bookingByUser : bookings) {
            if (bookingByUser.getDiscounts() != null && !bookingByUser.getDiscounts().isEmpty()) {
                discountsByUser.addAll(bookingByUser.getDiscounts());
            }
        }

        if (discounts != null) {
            for (Discount discountByUser : discountsByUser) {
                finalDiscounts.addAll(discounts.stream()
                        .filter(discount -> discount.getId() != 0)
                        .filter(discount -> (discount.getId() == discountByUser.getId()))
                        .toList());
                break;
            }

            booking.setDiscounts(finalDiscounts);
        }

        if (!finalDiscounts.isEmpty() && multipartFiles.size() == 1) {
            booking.setDiscounts(null);
        }

        if (!finalDiscounts.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (multipartFile != null && !multipartFile.isEmpty()) {
                    String filename = multipartFile.getOriginalFilename().split("\\.")[0];

                    for (Discount discount : finalDiscounts) {
                        Discount discountById = discountService.findById(discount.getId()).get();
                        if (filename.toLowerCase().contains(
                                discountById.getName().toLowerCase())) {

                            documentService.save(Document.builder()
                                    .docUrl(fileComponent.uploadPicture(multipartFile))
                                    .user(currentUser.getUser())
                                    .discount(discount)
                                    .build());
                        }
                    }
                }
            }
        }

        if (booking.getSpecialties() == null || booking.getSpecialties().isEmpty()) {
            booking.setSpecialties(specialtyService.findById(1));
        }

        booking.setUser(userService.findByEmail(currentUser.getUser().getEmail()).get());
        booking.setStatus(Status.PENDING);
        booking.setBookingDate(new Date());

        if (isEndTimeAfterStartTime(booking)) {
            log.info("User with email {} saves the booking for {} on {} from {} to {} by address {}. Participants are {}. Booking id is {}",
                    booking.getUser().getEmail(),
                    booking.getEventCategory().getName(),
                    booking.getBookingDate(),
                    booking.getEventStartTime(),
                    booking.getEventEndTime(),
                    booking.getAddress(),
                    booking.getParticipants(),
                    booking.getId());

            return bookingRepository.save(booking);
        }
        throw new InvalidBookingException("Event end time must be after event start time");
    }

    @Override
    public Page<Booking> findAll(Pageable pageable) {
        log.info("Called find all bookings method in BookingServiceImpl");
        return bookingRepository.findAll(pageable);
    }

    @Override
    public Optional<Booking> findById(int id) {

        log.info("Called find bookings by id method in BookingServiceImpl");

        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (bookingOptional.isEmpty()) {
            throw new EntityNotFoundException("Booking with " + id + " id does not exists!");
        }

        log.info("The booking with id {} was found", id);

        return bookingOptional;
    }

    @Override
    public Booking update(Booking booking) {

        log.info("User with email {} wants to update the booking with id {}, set for {} on {} from {} to {} by address {}. Participants are {}.",
                booking.getUser().getEmail(),
                booking.getId(),
                booking.getEventCategory().getName(),
                booking.getBookingDate(),
                booking.getEventStartTime(),
                booking.getEventEndTime(),
                booking.getAddress(),
                booking.getParticipants()
        );

        if (isEndTimeAfterStartTime(booking)) {
            Booking saved = bookingRepository.save(booking);

            log.info("Booking with id '{}' saved in repository", booking.getId());

            return saved;
        }
        throw new InvalidBookingException("Event end time must be after event start time");
    }

    @Override
    public void deleteById(int id) {
        findById(id);

        log.info("called delete booking by id method in Service");

        bookingRepository.deleteById(id);

        log.info("Booking by {} id was deleted", id);
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        log.info("Called find bookings by user id method");

        List<Booking> bookings = bookingRepository.findByUserId(userId);

        if (bookings.isEmpty()) {
            log.info("Booking with user ID {} was not found", userId);
        } else {
            log.info("Found {} bookings for user with ID {}", bookings.size(), userId);
        }

        return bookings;
    }

    private boolean isEndTimeAfterStartTime(Booking booking) {
        return booking.getEventEndTime() == null || booking.getEventStartTime() == null || booking.getEventEndTime().after(booking.getEventStartTime());
    }
}
