package am.itspace.photoshootprojectmanagementweb.service;

import am.itspace.photoshootprojectmanagementcommon.entity.Discount;

import java.util.List;
import java.util.Optional;

public interface DiscountService {

    List<Discount> findAll();

    Optional<Discount> findById(int id);

    void deleteById(int discountId);

}
