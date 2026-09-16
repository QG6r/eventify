package eventify.controller;

import eventify.dto.BookingResponse;
import eventify.dto.EventCreateRequest;
import eventify.dto.EventResponse;
import eventify.dto.EventUpdateRequest;
import eventify.dto.PageResponse;
import eventify.service.BookingService;
import eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Административные функции")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final BookingService bookingService;

    @Operation(summary = "Создать мероприятие (Admin)")
    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody EventCreateRequest request) {
        return eventService.create(request);
    }

    @Operation(summary = "Обновить мероприятие (Admin)")
    @PutMapping("/events/{id}")
    public EventResponse updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateRequest request) {
        return eventService.update(id, request);
    }

    @Operation(summary = "Удалить мероприятие (Admin)")
    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

    @Operation(summary = "Получить все бронирования (Admin)")
    @GetMapping("/bookings")
    public PageResponse<BookingResponse> getAllBookings(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Boolean unconfirmedOnly,
            Pageable pageable) {
        return bookingService.getAll(eventId, unconfirmedOnly, pageable);
    }

    @Operation(summary = "Подтвердить бронирование (Admin)")
    @PutMapping("/bookings/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmBooking(@PathVariable Long id) {
        bookingService.confirm(id);
    }

    @Operation(summary = "Удалить бронирование (Admin)")
    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.delete(id, null, true);
    }
}