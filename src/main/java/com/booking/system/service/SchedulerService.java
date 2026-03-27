package com.booking.system.service;

import com.booking.system.entity.Seat;
import com.booking.system.enums.BookingStatus;
import com.booking.system.enums.SeatStatus;
import com.booking.system.repository.BookingRepository;
import com.booking.system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    // Runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredSeatLocks() {
        log.info("Running scheduler to release expired seat locks...");
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        List<Seat> expiredSeats = seatRepository.findExpiredLocks(SeatStatus.LOCKED, fiveMinutesAgo);

        if (expiredSeats.isEmpty()) {
            return;
        }

        for (Seat seat : expiredSeats) {
            log.info("Releasing lock for Seat ID: {} / Number: {}", seat.getId(), seat.getSeatNumber());
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seatRepository.save(seat);

            // Also cancel the pending booking associated with this seat
            bookingRepository.findBySeatIdAndStatusIn(seat.getId(), List.of(BookingStatus.PENDING))
                    .ifPresent(booking -> {
                        booking.setStatus(BookingStatus.CANCELLED);
                        bookingRepository.save(booking);
                    });
        }
        
        log.info("Released {} expired locks.", expiredSeats.size());
    }
}
