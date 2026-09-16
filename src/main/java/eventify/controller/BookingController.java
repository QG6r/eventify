package eventify.controller;

import eventify.dto.BookingResponse;
import eventify.dto.CreateBookingRequest;
import eventify.dto.UpdateBookingRequest;
import eventify.security.CurrentUser;
import eventify.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bookings", description = "Операции с бронированиями")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CurrentUser currentUser;

    @Operation(summary = "Получить свои бронирования")
    @GetMapping
    public List<BookingResponse> getMyBookings() {
        return bookingService.getMyBookings(currentUser.getId());
    }

    @Operation(summary = "Создать бронирование")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.create(request, currentUser.getId());
    }

    @Operation(summary = "Получить бронирование по ID")
    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.getById(id, currentUser.getId(), currentUser.isAdmin());
    }

    @Operation(summary = "Обновить бронирование")
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBookingRequest request) {
        return bookingService.update(id, request, currentUser.getId(), currentUser.isAdmin());
    }

    @Operation(summary = "Отменить бронирование")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookingService.delete(id, currentUser.getId(), currentUser.isAdmin());
    }
}