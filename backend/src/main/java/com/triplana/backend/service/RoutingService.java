package com.triplana.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    public RouteResponse computeRoute(Double originLat, Double originLng, Double destLat, Double destLng, String travelMode) throws Exception {
        // building thr request body
        Map<String, Object> origin = Map.of("location", Map.of("latLng", Map.of("latitude", originLat, "longitude", originLng)));
        Map<String, Object> destination = Map.of("location", Map.of("latLng", Map.of("latitude", destLat, "longitude", destLng)));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("origin", origin);
        requestBody.put("destination", destination);
        requestBody.put("travelMode", travelMode);

        // building the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline,routes.legs.distanceMeters,routes.legs.duration,routes.legs.localizedValues,routes.legs.steps.distanceMeters,routes.legs.steps.staticDuration,routes.legs.steps.navigationInstruction,routes.legs.steps.travelMode");

        // making the POST request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject("https://routes.googleapis.com/directions/v2:computeRoutes", entity, String.class);

        // parsing the response
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> routes = mapper.convertValue(body.get("routes"), new TypeReference<List<Map<String, Object>>>() {});
        Map<String, Object> route = routes.get(0);

        // parsing polyline
        Map<String, Object> polyline = mapper.convertValue(route.get("polyline"), new TypeReference<Map<String, Object>>() {});

        // parsing legs
        List<Map<String, Object>> legs = mapper.convertValue(route.get("legs"), new TypeReference<List<Map<String, Object>>>() {});
        List<RouteLegResponse> legResponses = new ArrayList<>();

        for (Map<String, Object> leg : legs) {
            RouteLegResponse legResponse = new RouteLegResponse();
            legResponse.setDistanceMeters((Integer) leg.get("distanceMeters"));
            legResponse.setDuration((String) leg.get("duration"));

            Map<String, Object> localizedValues = mapper.convertValue(leg.get("localizedValues"), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> distanceMap = mapper.convertValue(localizedValues.get("distance"), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> durationMap = mapper.convertValue(localizedValues.get("duration"), new TypeReference<Map<String, Object>>() {});
            legResponse.setDistanceText((String) distanceMap.get("text"));
            legResponse.setDurationText((String) durationMap.get("text"));

            // parisng steps
            List<Map<String, Object>> steps = mapper.convertValue(leg.get("steps"), new TypeReference<List<Map<String, Object>>>() {});
            List<RouteStepResponse> stepResponses = new ArrayList<>();

            for (Map<String, Object> step : steps) {
                RouteStepResponse stepResponse = new RouteStepResponse();
                stepResponse.setDistanceMeters((Integer) step.get("distanceMeters"));
                stepResponse.setDuration((String) step.get("staticDuration"));
                stepResponse.setTravelMode((String) step.get("travelMode"));

                Map<String, Object> navInstruction = mapper.convertValue(step.get("navigationInstruction"), new TypeReference<Map<String, Object>>() {});
                if (navInstruction != null) {
                    stepResponse.setInstructions((String) navInstruction.get("instructions"));
                    stepResponse.setManeuver((String) navInstruction.get("maneuver"));
                }

                stepResponses.add(stepResponse);
            }

            legResponse.setSteps(stepResponses);
            legResponses.add(legResponse);
        }

        // building final response
        RouteResponse routeResponse = new RouteResponse();
        routeResponse.setDistanceMeters((Integer) route.get("distanceMeters"));
        routeResponse.setDuration((String) route.get("duration"));
        routeResponse.setEncodedPolyline((String) polyline.get("encodedPolyline"));
        routeResponse.setLegs(legResponses);

        return routeResponse;
    }
}
