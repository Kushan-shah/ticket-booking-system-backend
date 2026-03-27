package com.booking.system.controller;

import com.booking.system.dto.BookingResponse;
import com.booking.system.dto.LockSeatRequest;
import com.booking.system.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Endpoints for booking tickets securely")
@SecurityRequirement(name = "bearerAuth") // Enables Swagger Bearer token UI
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock-seat")
    @Operation(summary = "Lock a seat for 5 minutes (Idempotent)")
    public ResponseEntity<BookingResponse> lockSeat(@Valid @RequestBody LockSeatRequest request, Authentication authentication) {
        String email = authentication.getName();
        return new ResponseEntity<>(bookingService.lockSeat(request, email), HttpStatus.CREATED);
    }

    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm a pending booking")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long bookingId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId, email));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking and release the seat")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        String email = authentication.getName();
        bookingService.cancelBooking(bookingId, email);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/user")
    @Operation(summary = "Get all bookings for the authenticated user")
    public ResponseEntity<Page<BookingResponse>> getUserBookings(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.getUserBookings(email, pageable));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get a specific booking by ID")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long bookingId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, email));
    }
}
