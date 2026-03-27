package com.booking.system.service;

import com.booking.system.dto.BookingResponse;
import com.booking.system.dto.LockSeatRequest;
import com.booking.system.entity.Booking;
import com.booking.system.entity.Event;
import com.booking.system.entity.Seat;
import com.booking.system.entity.User;
import com.booking.system.enums.BookingStatus;
import com.booking.system.enums.SeatStatus;
import com.booking.system.exception.BookingFailedException;
import com.booking.system.exception.ResourceNotFoundException;
import com.booking.system.exception.SeatAlreadyBookedException;
import com.booking.system.exception.SeatLockExpiredException;
import com.booking.system.repository.BookingRepository;
import com.booking.system.repository.SeatRepository;
import com.booking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final EventService eventService;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse lockSeat(LockSeatRequest request, String userEmail) {
        log.info("User {} attempting to lock seat {} for event {}", userEmail, request.getSeatNumber(), request.getEventId());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventService.getEventEntity(request.getEventId());

        // Prevent booking for past events
        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new BookingFailedException("Cannot book seats for an event that has already occurred.");
        }

        Seat seat = seatRepository.findByEventIdAndSeatNumber(event.getId(), request.getSeatNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new SeatAlreadyBookedException("Seat is already permanently booked.");
        }

        // If locked, check if the lock has expired (e.g. 5 minutes)
        if (seat.getStatus() == SeatStatus.LOCKED) {
            if (seat.getLockedAt() != null && seat.getLockedAt().plusMinutes(5).isAfter(LocalDateTime.now())) {
                throw new SeatAlreadyBookedException("Seat is currently locked by another user. Try again in a few minutes.");
            }
        }

        // Apply Lock
        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedAt(LocalDateTime.now());
        seatRepository.save(seat); // Optimistic locking checked here on flush.

        // If seat lock was previously expired, cancel the old pending booking if one exists linking to this seat
        bookingRepository.findBySeatIdAndStatusIn(seat.getId(), List.of(BookingStatus.PENDING))
                .ifPresent(oldBooking -> {
                    oldBooking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(oldBooking);
                });

        // Create new Pending Booking
        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .seat(seat)
                .status(BookingStatus.PENDING)
                .build();

        bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse confirmBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BookingFailedException("You are not authorized to confirm this booking.");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return mapToResponse(booking); // Idempotent
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingFailedException("Cannot confirm a cancelled booking.");
        }

        Seat seat = booking.getSeat();

        if (seat.getLockedAt() != null && seat.getLockedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seatRepository.save(seat);
            
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new SeatLockExpiredException("Booking lock expired. Please select the seat and lock it again.");
        }

        // Confirm booking
        seat.setStatus(SeatStatus.BOOKED);
        seat.setLockedAt(null);
        seatRepository.save(seat);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        log.info("Booking {} confirmed for user {}", bookingId, userEmail);
        return mapToResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BookingFailedException("You are not authorized to cancel this booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return; // Idempotent
        }

        Seat seat = booking.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setLockedAt(null);
        seatRepository.save(seat);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("Booking {} cancelled by user {}. Seat {} released.", bookingId, userEmail, seat.getSeatNumber());
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getUserBookings(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BookingFailedException("You are not authorized to view this booking.");
        }
        return mapToResponse(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventName(booking.getEvent().getName())
                .seatNumber(booking.getSeat().getSeatNumber())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
