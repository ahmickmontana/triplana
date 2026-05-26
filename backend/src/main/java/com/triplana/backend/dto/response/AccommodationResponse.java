package com.triplana.backend.dto.response;

import com.triplana.backend.entity.Accommodation;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccommodationResponse {
    private Long id;
    private String name;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;

    public static AccommodationResponse from(Accommodation accommodation) {
        AccommodationResponse response = new AccommodationResponse();
        response.setId(accommodation.getId());
        response.setName(accommodation.getName());
        response.setLocationName(accommodation.getLocationName());
        response.setLatitude(accommodation.getLatitude());
        response.setLongitude(accommodation.getLongitude());
        response.setGooglePlaceId(accommodation.getGooglePlaceId());
        response.setCheckInDate(accommodation.getCheckInDate());
        response.setCheckOutDate(accommodation.getCheckOutDate());
        response.setCreatedAt(accommodation.getCreatedAt());
        return response;
    }
}