package com.booking.system.dto;

import com.booking.system.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String eventName;
    private String seatNumber;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
