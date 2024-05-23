package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Agreement;
import am.itspace.photoshootprojectmanagementcommon.entity.Booking;
import am.itspace.photoshootprojectmanagementcommon.entity.Role;
import am.itspace.photoshootprojectmanagementcommon.entity.Status;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.repository.AgreementRepository;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import am.itspace.photoshootprojectmanagementweb.service.AgreementService;
import am.itspace.photoshootprojectmanagementweb.service.BookingService;
import am.itspace.photoshootprojectmanagementweb.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementService {

    private final AgreementRepository agreementRepository;

    private final BookingService bookingService;

    private final EmailService emailService;

    @Override
    public Agreement save(int bookingId,
                          Agreement agreement) {

        Booking booking = bookingService.findById(bookingId).get();

        log.info("Called save() for user with email {}",
                booking.getUser().getEmail());

        agreement.setPrice(booking.getEventCategory().getStartingPrice());
        agreement.setBooking(booking);
        agreement.setUser(booking.getUser());
        agreement.getBooking().setStatus(Status.PRE_APPROVED);

        Agreement savedAgreement = agreementRepository.save(agreement);

        log.info("Agreement with id {} saved successfully!", agreement.getId());

        return savedAgreement;
    }

    @Override
    public Page<Agreement> findAll(Pageable pageable,
                                   CurrentUser currentUser) {

        log.info("Called findAll()");

        // Retrieve agreement based on user role
        if (currentUser.getUser().getRole() == Role.ADMIN) {
            return agreementRepository.findAll(pageable); // Admins see all agreements
        }
        return agreementRepository.findByUserId(pageable, currentUser.getUser().getId()); // Regular users see only their agreements
    }

    @Override
    public Optional<Agreement> findById(int id) {
        Optional<Agreement> agreementOptional = agreementRepository.findById(id);

        log.info("Called findById() for id {}", id);

        if (agreementOptional.isEmpty()) {
            throw new EntityNotFoundException("Agreement does not exists!");
        }

        log.info("findById(): Id {} was found", id);

        return agreementOptional;
    }

    @Override
    public Agreement update(Agreement agreement) {

        log.info("Called update() for agreement with id {}", agreement.getId());

        Agreement agreementOpt = findById(agreement.getId()).get();

        agreement.setBooking(agreementOpt.getBooking());

        agreement.setUser(agreementOpt.getUser());

        Agreement updatedAgreement = agreementRepository.save(agreement);

        log.info("Agreement with id {} updated successfully!", agreement.getId());

        return updatedAgreement;
    }

    @Override
    public void deleteById(int id) {
        findById(id);

        log.info("Called deleteById() for agreement for id {}", id);

        agreementRepository.deleteById(id);

        log.info("Agreement for id {} deleted successfully!", id);
    }

    @Override
    public void sendAgreementByEmail(User user, Agreement agreement) {
        emailService.sendAgreementByEmail(user, agreement);
    }
}
