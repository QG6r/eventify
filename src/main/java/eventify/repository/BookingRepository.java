package eventify.repository;

import eventify.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    Page<Booking> findByEventId(Long eventId, Pageable pageable);

    Page<Booking> findByConfirmedFalse(Pageable pageable);

    Page<Booking> findByEventIdAndConfirmedFalse(Long eventId, Pageable pageable);

    @Query("""
           SELECT COALESCE(SUM(b.ticketCount), 0)
           FROM Booking b
           WHERE b.event.id = :eventId
             AND (b.confirmed = true OR b.expiryTime > :now)
           """)
    int sumOccupiedTickets(@Param("eventId") Long eventId,
                           @Param("now") OffsetDateTime now);
}