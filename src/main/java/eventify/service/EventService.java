package eventify.service;

import eventify.dto.EventCreateRequest;
import eventify.dto.EventResponse;
import eventify.dto.EventUpdateRequest;
import eventify.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface EventService {

    PageResponse<EventResponse> list(OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    EventResponse getById(Long id);

    EventResponse create(EventCreateRequest request);

    EventResponse update(Long id, EventUpdateRequest request);

    void delete(Long id);
}