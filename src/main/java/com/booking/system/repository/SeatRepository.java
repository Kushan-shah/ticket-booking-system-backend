package com.booking.system.repository;

import com.booking.system.entity.Seat;
import com.booking.system.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findByEventId(Long eventId);
    
    Optional<Seat> findByEventIdAndSeatNumber(Long eventId, String seatNumber);

    // Custom query for the scheduler to find seats that are locked and the lock has expired
    @Query("SELECT s FROM Seat s WHERE s.status = :status AND s.lockedAt < :expiredTime")
    List<Seat> findExpiredLocks(@Param("status") SeatStatus status, @Param("expiredTime") LocalDateTime expiredTime);
}
