package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Agreement;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AgreementService {

    Agreement save(int bookingId, Agreement agreement);

    Page<Agreement> findAll(Pageable pageable, CurrentUser currentUser);

    Optional<Agreement> findById(int id);

    Agreement update(Agreement agreement);

    void deleteById(int id);

    void sendAgreementByEmail (User user, Agreement agreement);

}
