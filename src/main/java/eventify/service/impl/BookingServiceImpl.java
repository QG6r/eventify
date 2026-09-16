package eventify.service.impl;

import eventify.dto.BookingResponse;
import eventify.dto.CreateBookingRequest;
import eventify.dto.EventResponse;
import eventify.dto.PageResponse;
import eventify.dto.UpdateBookingRequest;
import eventify.entity.Booking;
import eventify.entity.Event;
import eventify.entity.User;
import eventify.exception.AccessDeniedException;
import eventify.exception.ConflictException;
import eventify.exception.NotFoundException;
import eventify.mapper.BookingMapper;
import eventify.repository.BookingRepository;
import eventify.repository.EventRepository;
import eventify.repository.UserRepository;
import eventify.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Value("${app.booking.expiry-minutes:30}")
    private long expiryMinutes;

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long currentUserId) {
        return bookingRepository.findByUserId(currentUserId).stream()
                .map(this::toResponseWithAvailability)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(Long id, Long currentUserId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: id=" + id));
        ensureOwnerOrAdmin(booking, currentUserId, isAdmin);
        return toResponseWithAvailability(booking);
    }

    @Override
    @Transactional
    public BookingResponse create(CreateBookingRequest request, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + currentUserId));

        Event event = eventRepository.findByIdForUpdate(request.eventId())
                .orElseThrow(() -> new NotFoundException("Мероприятие не найдено: id=" + request.eventId()));

        OffsetDateTime now = OffsetDateTime.now();
        int occupied = bookingRepository.sumOccupiedTickets(event.getId(), now);
        int available = event.getTotalTickets() - occupied;

        if (request.ticketCount() > available) {
            throw new ConflictException(
                    "Недостаточно мест: запрошено " + request.ticketCount() + ", доступно " + available);
        }

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .ticketCount(request.ticketCount())
                .expiryTime(now.plusMinutes(expiryMinutes))
                .confirmed(false)
                .build();
        Booking saved = bookingRepository.save(booking);
        log.info("Создано бронирование id={} на событие id={} пользователем id={}",
                saved.getId(), event.getId(), currentUserId);
        return toResponseWithAvailability(saved);
    }

    @Override
    @Transactional
    public BookingResponse update(Long id, UpdateBookingRequest request, Long currentUserId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: id=" + id));
        ensureOwnerOrAdmin(booking, currentUserId, isAdmin);

        if (request.ticketCount() != null) {
            Event event = eventRepository.findByIdForUpdate(booking.getEvent().getId())
                    .orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));
            OffsetDateTime now = OffsetDateTime.now();
            int occupied = bookingRepository.sumOccupiedTickets(event.getId(), now);
            int occupiedByOthers = occupied - currentlyCounted(booking, now);
            int available = event.getTotalTickets() - occupiedByOthers;
            if (request.ticketCount() > available) {
                throw new ConflictException(
                        "Недостаточно мест: запрошено " + request.ticketCount() + ", доступно " + available);
            }
            booking.setTicketCount(request.ticketCount());
        }
        log.info("Обновлено бронирование id={}", id);
        return toResponseWithAvailability(booking);
    }

    @Override
    @Transactional
    public void delete(Long id, Long currentUserId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: id=" + id));
        ensureOwnerOrAdmin(booking, currentUserId, isAdmin);
        bookingRepository.delete(booking);
        log.info("Удалено бронирование id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getAll(Long eventId, Boolean unconfirmedOnly, Pageable pageable) {
        boolean onlyUnconfirmed = Boolean.TRUE.equals(unconfirmedOnly);
        Page<Booking> page;
        if (eventId != null && onlyUnconfirmed) {
            page = bookingRepository.findByEventIdAndConfirmedFalse(eventId, pageable);
        } else if (eventId != null) {
            page = bookingRepository.findByEventId(eventId, pageable);
        } else if (onlyUnconfirmed) {
            page = bookingRepository.findByConfirmedFalse(pageable);
        } else {
            page = bookingRepository.findAll(pageable);
        }
        return PageResponse.from(page.map(this::toResponseWithAvailability));
    }

    @Override
    @Transactional
    public void confirm(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: id=" + id));
        booking.setConfirmed(true);
        log.info("Подтверждено бронирование id={}", id);
    }


    private void ensureOwnerOrAdmin(Booking booking, Long currentUserId, boolean isAdmin) {
        if (!isAdmin && !booking.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Нет доступа к чужому бронированию");
        }
    }

    private int currentlyCounted(Booking booking, OffsetDateTime now) {
        boolean counted = booking.isConfirmed() || booking.getExpiryTime().isAfter(now);
        return counted ? booking.getTicketCount() : 0;
    }

    private BookingResponse toResponseWithAvailability(Booking booking) {
        BookingResponse base = bookingMapper.toResponse(booking);
        Event event = booking.getEvent();
        int occupied = bookingRepository.sumOccupiedTickets(event.getId(), OffsetDateTime.now());
        int available = Math.max(0, event.getTotalTickets() - occupied);
        EventResponse ev = base.event();
        EventResponse eventWithAvail = new EventResponse(
                ev.id(), ev.title(), ev.description(), ev.dateTime(),
                ev.totalTickets(), available, ev.coverUrl()
        );
        return new BookingResponse(
                base.id(), eventWithAvail, base.customerEmail(), base.ticketCount(),
                base.createdAt(), base.expiryTime(), base.confirmed(), base.timezone()
        );
    }
}