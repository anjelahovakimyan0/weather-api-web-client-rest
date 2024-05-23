package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Document;
import am.itspace.photoshootprojectmanagementcommon.repository.DocumentRepository;
import am.itspace.photoshootprojectmanagementweb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public List<Document> findAll() {
        log.info("Called findAll()");

        return documentRepository.findAll();
    }

    @Override
    public List<Document> findByUserId(int userId) {
        return documentRepository.findByUserId(userId);
    }

    @Override
    public List<Document> findByDiscountId(int discountId) {
        return documentRepository.findByDiscountId(discountId);
    }

    @Override
    public void deleteByDiscountId(int discountId) {
        findByDiscountId(discountId);
        documentRepository.deleteByDiscountId(discountId);
    }
}
