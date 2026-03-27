package com.booking.system.service;

import com.booking.system.dto.SeatResponse;
import com.booking.system.entity.Seat;
import com.booking.system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatResponse> getSeatsForEvent(Long eventId) {
        return seatRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToSeatResponse)
                .collect(Collectors.toList());
    }

    private SeatResponse mapToSeatResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus())
                .build();
    }
}
