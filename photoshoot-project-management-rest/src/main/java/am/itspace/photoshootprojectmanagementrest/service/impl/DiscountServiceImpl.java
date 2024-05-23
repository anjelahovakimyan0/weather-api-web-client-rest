package am.itspace.photoshootprojectmanagementrest.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Discount;
import am.itspace.photoshootprojectmanagementcommon.repository.DiscountRepository;
import am.itspace.photoshootprojectmanagementrest.service.DiscountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public List<Discount> findAll() {
        log.info("Called findAll()");

        return discountRepository.findAll();
    }

    @Override
    public Optional<Discount> findById(int id) {
        return discountRepository.findById(id);
    }

    @Override
    public void deleteById(int discountId) {
        Optional<Discount> discountOptional = findById(discountId);
        if (discountOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can not delete discount with id. Discount with id " + discountId + " does not exist!");
        }

        discountRepository.deleteById(discountId);
    }
}
