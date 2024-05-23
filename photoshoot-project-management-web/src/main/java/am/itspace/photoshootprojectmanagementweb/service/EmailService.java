package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Agreement;
import am.itspace.photoshootprojectmanagementcommon.entity.User;

public interface EmailService {

    void send(User user, String subject, String message);

    void sendWelcomeMail(User user);

    void sendAgreementByEmail(User user, Agreement agreement);
}
