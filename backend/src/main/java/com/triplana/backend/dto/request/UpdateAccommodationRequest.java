package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateAccommodationRequest {

    @NotBlank(message = "Accommodation name cannot be empty")
    private String name;

    private String locationName;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;

    @NotNull(message = "Check-in date cannot be empty")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be empty")
    private LocalDate checkOutDate;
}