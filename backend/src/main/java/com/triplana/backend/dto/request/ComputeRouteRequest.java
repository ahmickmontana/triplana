package com.triplana.backend.dto.request;

import lombok.Data;

@Data
public class ComputeRouteRequest {
    private Double originLat;
    private Double originLng;
    private Double destLat;
    private Double destLng;
    private String travelMode;
}