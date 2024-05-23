package am.itspace.photoshootprojectmanagementrest.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Document;
import am.itspace.photoshootprojectmanagementcommon.entity.User;
import am.itspace.photoshootprojectmanagementcommon.repository.DocumentRepository;
import am.itspace.photoshootprojectmanagementrest.service.DocumentService;
import am.itspace.photoshootprojectmanagementrest.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final UserService userService;

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
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not find documents by user with id. User with id " + userId + " does not exist!");
        }

        return documentRepository.findByUserId(userId);
    }

    @Override
    public List<Document> findByDiscountId(int discountId) {
        return documentRepository.findByDiscountId(discountId);
    }

    @Override
    public void deleteByDiscountId(int discountId) {
        List<Document> documents = findByDiscountId(discountId);
        if (documents.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete document by discount with id. Discount with id " + discountId + " does not exist!");
        }

        documentRepository.deleteByDiscountId(discountId);
    }
}
