package com.triplana.backend.dto.response;

import lombok.Data;

@Data
public class PlaceDetailsResponse {
    private String name;
    private String formattedAddress;
    private Double latitude;
    private Double longitude;
}
