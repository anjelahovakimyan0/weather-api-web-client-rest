package am.itspace.photoshootprojectmanagementweb.service.impl;//package am.itspace.photoshootprojectmanagementcommon.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import am.itspace.photoshootprojectmanagementweb.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final UserService userService;

    @Override
    public boolean verifyUser(String token) {
        User userByToken = userService.findByEmailToken(token);
        if (userByToken == null) {
            log.info("User with token {} was not found", token);
            return false;
        }
        if (userByToken.isActive()) {
            log.info("user {} is already active! ", userByToken.getEmail());
            return false;
        }
        log.info("User {} was verified successfully", userByToken.getEmail());
//        userService.activateUser(userByToken); TODO...

        return true;
    }
}
