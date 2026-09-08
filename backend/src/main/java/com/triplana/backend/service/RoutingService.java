package com.triplana.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.request.ComputeRouteRequest;
import com.triplana.backend.dto.request.LatLng;
import com.triplana.backend.dto.response.RouteLegResponse;
import com.triplana.backend.dto.response.RouteResponse;
import com.triplana.backend.dto.response.RouteStepResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class RoutingService {
    
    @Value("${google.places.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public RoutingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public RouteResponse computeRoute(ComputeRouteRequest request) throws Exception {
        Map<String, Object> origin = Map.of("location", Map.of("latLng", Map.of("latitude", request.getOriginLat(), "longitude", request.getOriginLng())));
        Map<String, Object> destination = Map.of("location", Map.of("latLng", Map.of("latitude", request.getDestLat(), "longitude", request.getDestLng())));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("origin", origin);
        requestBody.put("destination", destination);
        requestBody.put("travelMode", request.getTravelMode());

        if (request.getIntermediates() != null && !request.getIntermediates().isEmpty()) {
            List<Map<String, Object>> intermediates = new ArrayList<>();
            for (LatLng point : request.getIntermediates()) {
                intermediates.add(Map.of("location", Map.of("latLng", 
                    Map.of("latitude", point.getLatitude(), "longitude", point.getLongitude()))));
            }
            requestBody.put("intermediates", intermediates);
        }

        if ("TRANSIT".equals(request.getTravelMode())) {
            Map<String, Object> transitPreferences = new HashMap<>();
            if (request.getAllowedTravelModes() != null && !request.getAllowedTravelModes().isEmpty()) {
                transitPreferences.put("allowedTravelModes", request.getAllowedTravelModes());
            }
            if (request.getRoutingPreference() != null && !request.getRoutingPreference().isEmpty()) {
                transitPreferences.put("routingPreference", request.getRoutingPreference());
            }
            if (!transitPreferences.isEmpty()) {
                requestBody.put("transitPreferences", transitPreferences);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline,routes.legs.distanceMeters,routes.legs.duration,routes.legs.localizedValues,routes.legs.steps.distanceMeters,routes.legs.steps.staticDuration,routes.legs.steps.navigationInstruction,routes.legs.steps.travelMode,routes.legs.steps.transitDetails");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject("https://routes.googleapis.com/directions/v2:computeRoutes", entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> routes = mapper.convertValue(body.get("routes"), new TypeReference<List<Map<String, Object>>>() {});
        Map<String, Object> route = routes.get(0);

        Map<String, Object> polyline = mapper.convertValue(route.get("polyline"), new TypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> legs = mapper.convertValue(route.get("legs"), new TypeReference<List<Map<String, Object>>>() {});
        List<RouteLegResponse> legResponses = new ArrayList<>();

        for (Map<String, Object> leg : legs) {
            RouteLegResponse legResponse = new RouteLegResponse();
            legResponse.setDistanceMeters(leg.get("distanceMeters") != null ? ((Number) leg.get("distanceMeters")).intValue() : 0);
            legResponse.setDuration((String) leg.get("duration"));

            Map<String, Object> localizedValues = mapper.convertValue(leg.get("localizedValues"), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> distanceMap = mapper.convertValue(localizedValues.get("distance"), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> durationMap = mapper.convertValue(localizedValues.get("duration"), new TypeReference<Map<String, Object>>() {});
            legResponse.setDistanceText((String) distanceMap.get("text"));
            legResponse.setDurationText((String) durationMap.get("text"));

            List<Map<String, Object>> steps = mapper.convertValue(leg.get("steps"), new TypeReference<List<Map<String, Object>>>() {});
            List<RouteStepResponse> stepResponses = new ArrayList<>();

            for (Map<String, Object> step : steps) {
                RouteStepResponse stepResponse = new RouteStepResponse();
                stepResponse.setDistanceMeters(step.get("distanceMeters") != null ? ((Number) step.get("distanceMeters")).intValue() : 0);
                stepResponse.setDuration((String) step.get("staticDuration"));
                stepResponse.setTravelMode((String) step.get("travelMode"));

                Map<String, Object> navInstruction = mapper.convertValue(step.get("navigationInstruction"), new TypeReference<Map<String, Object>>() {});
                if (navInstruction != null) {
                    stepResponse.setInstructions((String) navInstruction.get("instructions"));
                    stepResponse.setManeuver((String) navInstruction.get("maneuver"));
                }

                Map<String, Object> transitDetails = mapper.convertValue(step.get("transitDetails"), new TypeReference<Map<String, Object>>() {});
                if (transitDetails != null) {
                    Map<String, Object> transitLine = mapper.convertValue(transitDetails.get("transitLine"), new TypeReference<Map<String, Object>>() {});
                    Map<String, Object> stopDetails = mapper.convertValue(transitDetails.get("stopDetails"), new TypeReference<Map<String, Object>>() {});

                    if (transitLine != null) {
                        stepResponse.setTransitLine((String) transitLine.get("name"));
                        Map<String, Object> vehicle = mapper.convertValue(transitLine.get("vehicle"), new TypeReference<Map<String, Object>>() {});
                        if (vehicle != null) {
                            stepResponse.setVehicleType((String) vehicle.get("type"));
                        }
                    }

                    if (stopDetails != null) {
                        Map<String, Object> departureStop = mapper.convertValue(stopDetails.get("departureStop"), new TypeReference<Map<String, Object>>() {});
                        Map<String, Object> arrivalStop = mapper.convertValue(stopDetails.get("arrivalStop"), new TypeReference<Map<String, Object>>() {});
                        if (departureStop != null) stepResponse.setDepartureStop((String) departureStop.get("name"));
                        if (arrivalStop != null) stepResponse.setArrivalStop((String) arrivalStop.get("name"));
                    }

                    stepResponse.setNumStops((Integer) transitDetails.get("numStops"));
                }

                stepResponses.add(stepResponse);
            }

            legResponse.setSteps(stepResponses);
            legResponses.add(legResponse);
        }

        RouteResponse routeResponse = new RouteResponse();
        routeResponse.setDistanceMeters(((Number) route.get("distanceMeters")).intValue());
        routeResponse.setDuration((String) route.get("duration"));
        routeResponse.setEncodedPolyline((String) polyline.get("encodedPolyline"));
        routeResponse.setLegs(legResponses);

        return routeResponse;
    }
}
