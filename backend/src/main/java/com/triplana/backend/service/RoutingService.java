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
import com.triplana.backend.exception.AuthException;

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
        if ("fastest".equals(request.getStrategy())) {
            return computeFastestRoute(request);
        } else if ("transitonly".equals(request.getStrategy())) {
            return computeTransitSegments(request);
        }
        return computeSingleRoute(request, request.getTravelMode());
    }

    private RouteResponse computeFastestRoute(ComputeRouteRequest request) throws Exception {
        List<String> modes = new ArrayList<>(List.of("WALK", "TRANSIT"));
        if (request.getAllowedTravelModes() != null && request.getAllowedTravelModes().contains("DRIVE")) {
            modes.add("DRIVE");
        }
        RouteResponse fastest = null;
        int fastestSeconds = Integer.MAX_VALUE;

        for (String mode : modes) {
            try {
                RouteResponse response;
                if ("TRANSIT".equals(mode)) {
                    response = computeTransitSegments(request);
                } else {
                    response = computeSingleRoute(request, mode);
                }

                if (response != null) {
                    int seconds = Integer.parseInt(response.getDuration().replace("s", ""));
                    if (seconds < fastestSeconds) {
                        fastestSeconds = seconds;
                        fastest = response;
                    }
                }
            } catch (Exception e) {
                // skip unavailable modes
            }
        }

        if (fastest == null) {
            throw new AuthException("No route available with the selected preferences.");
        }

        return fastest;
    }

    private RouteResponse computeSingleRoute(ComputeRouteRequest request, String travelMode) throws Exception {
        Map<String, Object> origin = Map.of("location", Map.of("latLng", Map.of("latitude", request.getOriginLat(), "longitude", request.getOriginLng())));
        Map<String, Object> destination = Map.of("location", Map.of("latLng", Map.of("latitude", request.getDestLat(), "longitude", request.getDestLng())));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("origin", origin);
        requestBody.put("destination", destination);
        requestBody.put("travelMode", travelMode);

        if (request.getIntermediates() != null && !request.getIntermediates().isEmpty() && !"TRANSIT".equals(travelMode)) {
            List<Map<String, Object>> intermediates = new ArrayList<>();
            for (LatLng point : request.getIntermediates()) {
                intermediates.add(Map.of("location", Map.of("latLng",
                    Map.of("latitude", point.getLatitude(), "longitude", point.getLongitude()))));
            }
            requestBody.put("intermediates", intermediates);
        }

        if ("TRANSIT".equals(travelMode)) {
            requestBody.put("departureTime", java.time.Instant.now().plusSeconds(60).toString());
            
            Map<String, Object> transitPreferences = new HashMap<>();
            
            if (request.getAllowedTravelModes() != null && !request.getAllowedTravelModes().isEmpty()) {
                List<String> transitModes = request.getAllowedTravelModes().stream()
                    .filter(m -> !m.equals("DRIVE"))
                    .collect(java.util.stream.Collectors.toList());
                if (!transitModes.isEmpty()) {
                    transitPreferences.put("allowedTravelModes", transitModes);
                }
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

        if (routes == null || routes.isEmpty()) {
            return null;
        }

        Map<String, Object> route = routes.get(0);
        Map<String, Object> polyline = mapper.convertValue(route.get("polyline"), new TypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> legs = mapper.convertValue(route.get("legs"), new TypeReference<List<Map<String, Object>>>() {});
        List<RouteLegResponse> legResponses = new ArrayList<>();

        for (Map<String, Object> leg : legs) {
            RouteLegResponse legResponse = new RouteLegResponse();
            legResponse.setDistanceMeters(leg.get("distanceMeters") != null ? ((Number) leg.get("distanceMeters")).intValue() : 0);
            legResponse.setDuration((String) leg.get("duration"));

            Map<String, Object> localizedValues = mapper.convertValue(leg.get("localizedValues"), new TypeReference<Map<String, Object>>() {});
            if (localizedValues != null) {
                Map<String, Object> distanceMap = mapper.convertValue(localizedValues.get("distance"), new TypeReference<Map<String, Object>>() {});
                Map<String, Object> durationMap = mapper.convertValue(localizedValues.get("duration"), new TypeReference<Map<String, Object>>() {});
                if (distanceMap != null) legResponse.setDistanceText((String) distanceMap.get("text"));
                if (durationMap != null) legResponse.setDurationText((String) durationMap.get("text"));
            }

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

    private RouteResponse computeTransitSegments(ComputeRouteRequest request) throws Exception {
        List<LatLng> allPoints = new ArrayList<>();
        allPoints.add(new LatLng(request.getOriginLat(), request.getOriginLng()));
        if (request.getIntermediates() != null) {
            allPoints.addAll(request.getIntermediates());
        }
        allPoints.add(new LatLng(request.getDestLat(), request.getDestLng()));

        List<RouteLegResponse> allLegs = new ArrayList<>();
        int totalDistance = 0;
        int totalSeconds = 0;
        String encodedPolyline = null;

        List<String> polylines = new ArrayList<>();

        for (int i = 0; i < allPoints.size() - 1; i++) {
            ComputeRouteRequest segmentRequest = new ComputeRouteRequest();
            segmentRequest.setOriginLat(allPoints.get(i).getLatitude());
            segmentRequest.setOriginLng(allPoints.get(i).getLongitude());
            segmentRequest.setDestLat(allPoints.get(i + 1).getLatitude());
            segmentRequest.setDestLng(allPoints.get(i + 1).getLongitude());
            segmentRequest.setTravelMode("TRANSIT");
            segmentRequest.setRoutingPreference(request.getRoutingPreference());
            segmentRequest.setAllowedTravelModes(request.getAllowedTravelModes());

            RouteResponse segment = computeSingleRoute(segmentRequest, "TRANSIT");
            if (segment == null) {
                segment = computeSingleRoute(segmentRequest, "WALK");
            }
            if (segment != null) {
                allLegs.addAll(segment.getLegs());
                totalDistance += segment.getDistanceMeters();
                totalSeconds += Integer.parseInt(segment.getDuration().replace("s", ""));
                if (segment.getEncodedPolyline() != null) {
                    polylines.add(segment.getEncodedPolyline());
                }
            }

        }

        RouteResponse combined = new RouteResponse();
        combined.setDistanceMeters(totalDistance);
        combined.setDuration(totalSeconds + "s");
        combined.setEncodedPolyline(encodedPolyline);
        combined.setPolylines(polylines);
        combined.setLegs(allLegs);
        return combined;
    }
}
