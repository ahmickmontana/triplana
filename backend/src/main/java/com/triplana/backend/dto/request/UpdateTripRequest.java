package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateTripRequest {

    @NotBlank(message = "Trip name cannot be empty")
    @Size(max = 100)
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}