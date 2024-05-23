package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.Specialty;
import am.itspace.photoshootprojectmanagementcommon.repository.SpecialtyRepository;
import am.itspace.photoshootprojectmanagementweb.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Override
    public List<Specialty> findAll() {
        log.info("Called findAll()");

        return specialtyRepository.findAll();
    }

    @Override
    public List<Specialty> findById(int i) {
        return specialtyRepository.findById(i);
    }
}