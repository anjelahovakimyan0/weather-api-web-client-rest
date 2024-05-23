package am.itspace.photoshootprojectmanagementcommon.repository;

import am.itspace.photoshootprojectmanagementcommon.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<Document> findByUserId(int userId);

    List<Document> findByDiscountId(int discountId);

    void deleteByDiscountId(int discountId);

}
