package com.triplana.backend.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class RouteResponse {
    private int distanceMeters;
    private String duration;
    private String encodedPolyline;
    private List<RouteLegResponse> legs;
}
