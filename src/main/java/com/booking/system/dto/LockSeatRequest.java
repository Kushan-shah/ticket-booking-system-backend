package com.booking.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockSeatRequest {
    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    @NotBlank(message = "Seat number cannot be blank")
    private String seatNumber;
}
