package com.triplana.backend.dto.response;

import com.triplana.backend.entity.Trip;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TripResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String coverImagePath;
    private LocalDateTime createdAt;

    public static TripResponse from(Trip trip) {
        TripResponse response = new TripResponse();
        response.setId(trip.getId());
        response.setName(trip.getName());
        response.setDescription(trip.getDescription());
        response.setStartDate(trip.getStartDate());
        response.setEndDate(trip.getEndDate());
        response.setCoverImagePath(trip.getCoverImagePath());
        response.setCreatedAt(trip.getCreatedAt());
        return response;
    }
}