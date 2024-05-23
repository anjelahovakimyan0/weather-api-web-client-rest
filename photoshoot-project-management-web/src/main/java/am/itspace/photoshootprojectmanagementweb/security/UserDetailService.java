package am.itspace.photoshootprojectmanagementweb.security;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserService userService;

    /**
     * Loads a user's details by their username.
     *
     * @param username The username of the user whose details need to be loaded.
     * @return A UserDetails object containing the user's details.
     * @throws UsernameNotFoundException If the user with the given username does not exist.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userResponseDtoOpt = userService.findByEmail(username);

        if (userResponseDtoOpt.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + username + " does not exists!");
        }

        return new CurrentUser(userResponseDtoOpt.get());
    }
}
