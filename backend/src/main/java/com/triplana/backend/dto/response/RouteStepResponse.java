package com.triplana.backend.dto.response;

import lombok.Data;

@Data
public class RouteStepResponse {
    private String instructions;
    private String maneuver;
    private int distanceMeters;
    private String duration;
    private String travelMode;
}
