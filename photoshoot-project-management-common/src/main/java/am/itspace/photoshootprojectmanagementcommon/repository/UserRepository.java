package am.itspace.photoshootprojectmanagementcommon.repository;

import am.itspace.photoshootprojectmanagementcommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Page<User> findByIsDeleted(boolean deleted, Pageable pageable);

    Optional<User> findByEmailToken(String emailToken);

}
