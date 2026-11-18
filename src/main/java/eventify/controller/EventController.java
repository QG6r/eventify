package eventify.controller;

import eventify.dto.EventResponse;
import eventify.dto.PageResponse;
import eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Tag(name = "Events", description = "Event management")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "List events")
    @GetMapping
    public PageResponse<EventResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            Pageable pageable) {
        return eventService.list(from, to, pageable);
    }

    @Operation(summary = "Get event by ID")
    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable Long id) {
        return eventService.getById(id);
    }
}