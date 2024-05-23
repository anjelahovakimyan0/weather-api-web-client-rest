package am.itspace.photoshootprojectmanagementcommon.repository;

import am.itspace.photoshootprojectmanagementcommon.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    List<Specialty> findById(int id);

}
