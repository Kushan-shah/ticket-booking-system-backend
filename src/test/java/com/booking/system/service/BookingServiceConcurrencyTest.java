package com.booking.system.service;

import com.booking.system.dto.LockSeatRequest;
import com.booking.system.entity.Event;
import com.booking.system.entity.Seat;
import com.booking.system.entity.User;
import com.booking.system.enums.Role;
import com.booking.system.enums.SeatStatus;
import com.booking.system.repository.BookingRepository;
import com.booking.system.repository.EventRepository;
import com.booking.system.repository.SeatRepository;
import com.booking.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class BookingServiceConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private Event event;
    private Seat seat;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder().email("user1@test.com").password("pass").role(Role.USER).build());
        user2 = userRepository.save(User.builder().email("user2@test.com").password("pass").role(Role.USER).build());

        event = eventRepository.save(Event.builder().name("Concert").date(LocalDateTime.now().plusDays(1)).location("Stadium").build());

        seat = seatRepository.save(Seat.builder().event(event).seatNumber("A1").status(SeatStatus.AVAILABLE).version(0L).build());
    }

    @Test
    void testOptimisticLockingWhenTwoUsersBookSameSeat() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // Both threads try to lock the exact same seat.
        // Due to @Transactional + @Version optimistic locking:
        //   - If threads run truly concurrently: one gets OptimisticLockingFailureException
        //   - If threads are serialized: the second sees LOCKED status and gets SeatAlreadyBookedException
        // Either way, only ONE can succeed — proving no double booking.

        for (int i = 0; i < numberOfThreads; i++) {
            final String email = (i == 0) ? user1.getEmail() : user2.getEmail();
            executorService.submit(() -> {
                try {
                    LockSeatRequest req = new LockSeatRequest(event.getId(), "A1");
                    bookingService.lockSeat(req, email);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Any exception (OptimisticLocking, SeatAlreadyBooked, etc.) = failed to book
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // The critical assertion: exactly 1 user succeeded, exactly 1 failed
        assertEquals(1, successCount.get(), "Exactly one user should successfully lock the seat");
        assertEquals(1, failureCount.get(), "Exactly one user should fail to lock the seat");

        // Seat should be LOCKED with version incremented
        Seat finalSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.LOCKED, finalSeat.getStatus(), "Seat should be in LOCKED state");
        assertTrue(finalSeat.getVersion() >= 1L, "Version should be incremented");
    }
}
