package am.itspace.photoshootprojectmanagementrest.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Document;

import java.util.List;

public interface DocumentService {

    Document save(Document document);

    List<Document> findAll();

    List<Document> findByUserId(int userId);

    List<Document> findByDiscountId(int id);

    void deleteByDiscountId(int discountId);

}
