package eventify.service;

import eventify.dto.BookingResponse;
import eventify.dto.CreateBookingRequest;
import eventify.dto.PageResponse;
import eventify.dto.UpdateBookingRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    List<BookingResponse> getMyBookings(Long currentUserId);

    BookingResponse getById(Long id, Long currentUserId, boolean isAdmin);

    BookingResponse create(CreateBookingRequest request, Long currentUserId);

    BookingResponse update(Long id, UpdateBookingRequest request, Long currentUserId, boolean isAdmin);

    void delete(Long id, Long currentUserId, boolean isAdmin);

    PageResponse<BookingResponse> getAll(Long eventId, Boolean unconfirmedOnly, Pageable pageable);

    void confirm(Long id);
}