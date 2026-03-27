package com.booking.system.controller;

import com.booking.system.dto.SeatResponse;
import com.booking.system.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/seats")
@RequiredArgsConstructor
@Tag(name = "Seats", description = "Endpoints for viewing available seats for an event")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    @Operation(summary = "Get all seats for an event")
    public ResponseEntity<List<SeatResponse>> getSeatsForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(seatService.getSeatsForEvent(eventId));
    }
}
