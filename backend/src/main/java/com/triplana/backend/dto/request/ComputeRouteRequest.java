package com.triplana.backend.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class ComputeRouteRequest {
    private Double originLat;
    private Double originLng;
    private Double destLat;
    private Double destLng;
    private List<LatLng> intermediates;
    private String travelMode;
}