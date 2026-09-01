package com.triplana.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triplana.backend.dto.request.ComputeRouteRequest;
import com.triplana.backend.dto.response.RouteResponse;
import com.triplana.backend.service.RoutingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {
    
    private final RoutingService routingService;

    @PostMapping("/compute")
    public ResponseEntity<RouteResponse> computeRoute(@RequestBody ComputeRouteRequest request) throws Exception {
        RouteResponse response = routingService.computeRoute(
            request.getOriginLat(), request.getOriginLng(), request.getDestLat(), request.getDestLng(), request.getTravelMode());

        return ResponseEntity.ok(response);
    }
}
