package com.triplana.backend.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class RouteLegResponse {
    private int distanceMeters;
    private String duration;
    private String distanceText;
    private String durationText;
    List<RouteStepResponse> steps;
}
