package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalTime;

@Data
public class CreateActivityRequest {

    @NotBlank(message = "Activity name cannot be empty")
    private String title;

    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;
}