package am.itspace.photoshootprojectmanagementcommon.repository;

import am.itspace.photoshootprojectmanagementcommon.entity.Agreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementRepository extends JpaRepository<Agreement, Integer> {

    Page<Agreement> findByUserId(Pageable pageable, int id);

}
