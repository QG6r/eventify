package eventify.service.impl;

import eventify.dto.EventCreateRequest;
import eventify.dto.EventResponse;
import eventify.dto.EventUpdateRequest;
import eventify.dto.PageResponse;
import eventify.entity.Event;
import eventify.exception.NotFoundException;
import eventify.mapper.EventMapper;
import eventify.repository.BookingRepository;
import eventify.repository.EventRepository;
import eventify.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> list(OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        Page<Event> page;
        if (from != null && to != null) {
            page = eventRepository.findByDateTimeBetween(from, to, pageable);
        } else if (from != null) {
            page = eventRepository.findByDateTimeGreaterThanEqual(from, pageable);
        } else if (to != null) {
            page = eventRepository.findByDateTimeLessThanEqual(to, pageable);
        } else {
            page = eventRepository.findAll(pageable);
        }
        return PageResponse.from(page.map(this::toResponseWithAvailability));
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено: id=" + id));
        return toResponseWithAvailability(event);
    }

    @Override
    @Transactional
    public EventResponse create(EventCreateRequest request) {
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .dateTime(request.dateTime())
                .totalTickets(request.totalTickets())
                .coverUrl(request.coverUrl())
                .build();
        Event saved = eventRepository.save(event);
        log.info("Создано мероприятие id={}", saved.getId());
        return toResponseWithAvailability(saved);
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено: id=" + id));

        if (request.title() != null) event.setTitle(request.title());
        if (request.description() != null) event.setDescription(request.description());
        if (request.dateTime() != null) event.setDateTime(request.dateTime());
        if (request.totalTickets() != null) event.setTotalTickets(request.totalTickets());
        if (request.coverUrl() != null) event.setCoverUrl(request.coverUrl());

        log.info("Обновлено мероприятие id={}", id);
        return toResponseWithAvailability(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Мероприятие не найдено: id=" + id);
        }
        eventRepository.deleteById(id);
        log.info("Удалено мероприятие id={}", id);
    }

    private EventResponse toResponseWithAvailability(Event event) {
        int occupied = bookingRepository.sumOccupiedTickets(event.getId(), OffsetDateTime.now());
        int available = Math.max(0, event.getTotalTickets() - occupied);
        EventResponse base = eventMapper.toResponse(event);
        return new EventResponse(
                base.id(), base.title(), base.description(), base.dateTime(),
                base.totalTickets(), available, base.coverUrl()
        );
    }
}