package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateTripRequest {

    @NotBlank(message = "Trip name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Start date cannot be empty")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be empty")
    private LocalDate endDate;
}