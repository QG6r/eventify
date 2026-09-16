package eventify.service;

import eventify.dto.EventResponse;
import eventify.entity.Event;
import eventify.exception.NotFoundException;
import eventify.mapper.EventMapper;
import eventify.repository.BookingRepository;
import eventify.repository.EventRepository;
import eventify.service.impl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void getById_computesAvailableTickets() {
        Event event = Event.builder()
                .id(1L).title("Concert").totalTickets(100)
                .dateTime(OffsetDateTime.now()).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.sumOccupiedTickets(eq(1L), any(OffsetDateTime.class))).thenReturn(30);
        when(eventMapper.toResponse(event)).thenReturn(
                new EventResponse(1L, "Concert", null, event.getDateTime(), 100, null, null));

        EventResponse response = eventService.getById(1L);

        assertThat(response.totalTickets()).isEqualTo(100);
        assertThat(response.availableTickets()).isEqualTo(70);
    }

    @Test
    void getById_notFound_throws() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_availableNeverNegative() {
        Event event = Event.builder()
                .id(1L).title("Small").totalTickets(10)
                .dateTime(OffsetDateTime.now()).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.sumOccupiedTickets(eq(1L), any(OffsetDateTime.class))).thenReturn(15);
        when(eventMapper.toResponse(event)).thenReturn(
                new EventResponse(1L, "Small", null, event.getDateTime(), 10, null, null));

        EventResponse response = eventService.getById(1L);

        assertThat(response.availableTickets()).isZero();
    }
}