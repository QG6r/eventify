package eventify.service;

import eventify.dto.BookingResponse;
import eventify.dto.CreateBookingRequest;
import eventify.dto.EventResponse;
import eventify.entity.Booking;
import eventify.entity.Event;
import eventify.entity.Role;
import eventify.entity.User;
import eventify.exception.AccessDeniedException;
import eventify.exception.ConflictException;
import eventify.exception.NotFoundException;
import eventify.mapper.BookingMapper;
import eventify.repository.BookingRepository;
import eventify.repository.EventRepository;
import eventify.repository.UserRepository;
import eventify.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "expiryMinutes", 30L);
        user = User.builder().id(1L).email("user@test.com").role(Role.USER).build();
        event = Event.builder()
                .id(10L).title("Concert").totalTickets(100)
                .dateTime(OffsetDateTime.now().plusDays(1)).build();
    }

    @Test
    void create_success_whenEnoughSeats() {
        CreateBookingRequest request = new CreateBookingRequest(10L, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(event));
        when(bookingRepository.sumOccupiedTickets(eq(10L), any(OffsetDateTime.class))).thenReturn(50);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(100L);
            return b;
        });
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(
                new BookingResponse(100L,
                        new EventResponse(10L, "Concert", null, event.getDateTime(), 100, null, null),
                        "user@test.com", 2, OffsetDateTime.now(), OffsetDateTime.now(), false, null));

        BookingResponse response = bookingService.create(request, 1L);

        assertThat(response.id()).isEqualTo(100L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_notEnoughSeats_throwsConflict() {
        CreateBookingRequest request = new CreateBookingRequest(10L, 60);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(event));
        when(bookingRepository.sumOccupiedTickets(eq(10L), any(OffsetDateTime.class))).thenReturn(50);

        assertThatThrownBy(() -> bookingService.create(request, 1L))
                .isInstanceOf(ConflictException.class);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_eventNotFound_throws() {
        CreateBookingRequest request = new CreateBookingRequest(999L, 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(request, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_otherUsersBooking_throwsAccessDenied() {
        User owner = User.builder().id(2L).email("owner@test.com").role(Role.USER).build();
        Booking booking = Booking.builder()
                .id(100L).user(owner).event(event).ticketCount(1)
                .expiryTime(OffsetDateTime.now().plusMinutes(30)).confirmed(false).build();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getById(100L, 1L, false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getById_adminCanAccessAnyBooking() {
        User owner = User.builder().id(2L).email("owner@test.com").role(Role.USER).build();
        Booking booking = Booking.builder()
                .id(100L).user(owner).event(event).ticketCount(1)
                .expiryTime(OffsetDateTime.now().plusMinutes(30)).confirmed(false).build();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.sumOccupiedTickets(eq(10L), any(OffsetDateTime.class))).thenReturn(0);
        when(bookingMapper.toResponse(booking)).thenReturn(
                new BookingResponse(100L,
                        new EventResponse(10L, "Concert", null, event.getDateTime(), 100, null, null),
                        "owner@test.com", 1, OffsetDateTime.now(), OffsetDateTime.now(), false, null));

        BookingResponse response = bookingService.getById(100L, 1L, true);

        assertThat(response.id()).isEqualTo(100L);
    }
}