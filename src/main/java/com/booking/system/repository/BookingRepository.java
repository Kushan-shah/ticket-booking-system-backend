package com.booking.system.repository;

import com.booking.system.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.system.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @EntityGraph(attributePaths = {"event", "seat"})
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "event", "seat"})
    Optional<Booking> findById(Long id);

    // Find the currently active (PENDING or CONFIRMED) booking for a seat.
    // A seat can have multiple CANCELLED bookings, so we can't just do findBySeatId.
    Optional<Booking> findBySeatIdAndStatusIn(Long seatId, List<BookingStatus> statuses);
}
