package am.itspace.photoshootprojectmanagementweb.service.impl;

import am.itspace.photoshootprojectmanagementcommon.entity.EventCategory;
import am.itspace.photoshootprojectmanagementcommon.repository.EventCategoryRepository;
import am.itspace.photoshootprojectmanagementweb.service.EventCategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCategoryServiceImpl implements EventCategoryService {

    private final EventCategoryRepository eventCategoryRepository;

    @Override
    public EventCategory save(EventCategory eventCategory) {
        log.info("Called save the category method in EventCategoryServiceImpl");

        EventCategory saved = eventCategoryRepository.save(eventCategory);
        log.info("Admin saved the event Category with name {}", eventCategory.getName());

        return saved;
    }

    @Override
    public List<EventCategory> findAll() {
        log.info("Called find all event categories method in EventCategoryServiceImpl");

        return eventCategoryRepository.findAll();
    }

    @Override
    public Optional<EventCategory> findById(int id) {
        log.info("Called find category by id method in EventCategoryServiceImpl");

        Optional<EventCategory> eventCategoryOptional = eventCategoryRepository.findById(id);
        if (eventCategoryOptional.isEmpty()) {
            throw new EntityNotFoundException("EventCategory does not exists!");
        }

        return eventCategoryOptional;
    }

    @Override
    public EventCategory update(EventCategory eventCategory) {
        log.info("Called update category method in EventCategoryServiceImpl");

        EventCategory saved = eventCategoryRepository.save(eventCategory);
        log.info("Event Category with id '{}' saved successfully", eventCategory.getId());

        return saved;
    }

    @Override
    public void deleteById(int id) {
        findById(id);

        log.info("Called delete category by id method in EventCategoryServiceImpl");
        eventCategoryRepository.deleteById(id);
        log.info("Event Category with id '{}' was deleted from repository", id);
    }
}
