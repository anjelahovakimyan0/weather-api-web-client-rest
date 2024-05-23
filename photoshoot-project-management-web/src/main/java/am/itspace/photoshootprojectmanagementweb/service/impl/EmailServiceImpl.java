package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Agreement;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${server.url}")
    private String serverUrl;

    public void send(String to, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);  // Use the property value here
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
    }


    @Override
    public void send(User user, String subject, String message) {

        // Creates a new Context object which will be used to set mail variables that will be used in the mail template.
        final Context ctx = new Context();

        // Set context variables based on provided arguments
        ctx.setVariable("user", user); // Assuming user object is needed in the template
        // You can add other context variables as needed (e.g., additional message content)

        // Uses the templateEngine to process the appropriate template with the defined context
        // Replace "yourTemplate.html" with the actual template name for your generic email
        final String htmlContent = templateEngine.process("/admin/successfullyActivatedEmail.html", ctx);

        // A new MIME message is created using the JavaMailSender Helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            // A helper for populating a 'MIME' message is created
            // This also defines that the mail will allow 'multi-part' messages (attachments, inlines, etc.) and the encoding will be 'UTF-8'
            final MimeMessageHelper emailMessage = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Setting the subject of the email
            emailMessage.setSubject(subject); // Use the provided subject argument
            // Setting the sender's email address
            emailMessage.setFrom(fromEmail); // Assuming 'fromEmail' is a configured sender address
            // Setting the recipient's email address
            emailMessage.setTo(user.getEmail());
            emailMessage.setText(htmlContent, true);

            log.info("Sending email with subject: {}", subject);

            // Mail message is sent
            javaMailSender.send(mimeMessage);

            log.info("Email sent successfully");
        } catch (MessagingException e) {
            // If there's a messaging error, we catch it and throw a new runtime exception.
            log.error("Failed to send email with subject: {} - {}", subject, e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Async
    public void sendWelcomeMail(User user) {
        log.info("called 'send welcome email' method in EmailServiceImpl");

        // Creates a new Context object which will be used to set mail variables that will be used in the mail template.
        final Context ctx = new Context();

        ctx.setVariable("user", user);
        ctx.setVariable("serverUrl", serverUrl);

        // Uses the templateEngine to process the admin/welcomeMail.html template with the defined context
        // Then, the processed html is stored to 'htmlContent' string
        final String htmlContent = templateEngine.process("admin/welcomeMail.html", ctx);

        // A new MIME message is created using the JavaMailSender Helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            // A helper for populating a 'MIME' message is created
            // This also defines that the mail will allow 'multi-part' messages (attachments, inlines, etc.) and the encoding will be 'UTF-8'
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Setting the subject of the email
            message.setSubject("Welcome to ArmPhoto Community");
            // Setting the sender's email address
            message.setFrom(fromEmail);
            // Setting the recipient's email address
            message.setTo(user.getEmail());
            message.setText(htmlContent, true);

            log.info("Sending welcome email to {}", user.getEmail());

            // Mail message is sent
            javaMailSender.send(mimeMessage);

            log.info("Welcome email was sent to {}", user.getEmail());
        } catch (MessagingException e) {
            // If there's a messaging error, we catch it and throw a new runtime exception.
            log.error("Failed to send the email to {}{}", user.getEmail(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendAgreementByEmail(User user, Agreement agreement) {
        final Context ctx = new Context();
        ctx.setVariable("user", user);
        ctx.setVariable("agreement", agreement);
        final String htmlContent = templateEngine.process("admin/agreementEmail.html", ctx);
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject("Agreement");
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            log.info("Agreement email was sent to {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send the agreement email to {}{}", user.getEmail(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
