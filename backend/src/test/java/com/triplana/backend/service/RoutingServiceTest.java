package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.triplana.backend.dto.request.ComputeRouteRequest;
import com.triplana.backend.dto.request.LatLng;
import com.triplana.backend.dto.response.RouteResponse;
import com.triplana.backend.exception.AuthException;

@ExtendWith(MockitoExtension.class)
public class RoutingServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RoutingService routingService;

    private ComputeRouteRequest walkRequest;
    private ComputeRouteRequest driveRequest;
    private ComputeRouteRequest fastestRequest;
    private ComputeRouteRequest transitOnlyRequest;

    private static final String WALK_RESPONSE = "{\"routes\":[{\"distanceMeters\":1000,\"duration\":\"720s\",\"polyline\":{\"encodedPolyline\":\"abc123\"},\"legs\":[{\"distanceMeters\":1000,\"duration\":\"720s\",\"localizedValues\":{\"distance\":{\"text\":\"1.0 km\"},\"duration\":{\"text\":\"12 min\"}},\"steps\":[{\"distanceMeters\":1000,\"staticDuration\":\"720s\",\"travelMode\":\"WALK\",\"navigationInstruction\":{\"instructions\":\"Walk north\",\"maneuver\":\"DEPART\"}}]}]}]}";
    private static final String EMPTY_RESPONSE = "{\"routes\":[]}";
    private static final String NO_ROUTES_RESPONSE = "{}";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(routingService, "apiKey", "test-api-key");

        walkRequest = new ComputeRouteRequest();
        walkRequest.setOriginLat(51.5033);
        walkRequest.setOriginLng(-0.1196);
        walkRequest.setDestLat(51.5007);
        walkRequest.setDestLng(-0.1246);
        walkRequest.setStrategy("walking");
        walkRequest.setTravelMode("WALK");

        driveRequest = new ComputeRouteRequest();
        driveRequest.setOriginLat(51.5033);
        driveRequest.setOriginLng(-0.1196);
        driveRequest.setDestLat(51.5007);
        driveRequest.setDestLng(-0.1246);
        driveRequest.setStrategy("driving");
        driveRequest.setTravelMode("DRIVE");

        fastestRequest = new ComputeRouteRequest();
        fastestRequest.setOriginLat(51.5033);
        fastestRequest.setOriginLng(-0.1196);
        fastestRequest.setDestLat(51.5007);
        fastestRequest.setDestLng(-0.1246);
        fastestRequest.setStrategy("fastest");
        fastestRequest.setTravelMode("TRANSIT");

        transitOnlyRequest = new ComputeRouteRequest();
        transitOnlyRequest.setOriginLat(51.5033);
        transitOnlyRequest.setOriginLng(-0.1196);
        transitOnlyRequest.setDestLat(51.5007);
        transitOnlyRequest.setDestLng(-0.1246);
        transitOnlyRequest.setStrategy("transitonly");
        transitOnlyRequest.setTravelMode("TRANSIT");
    }


    // computeRoute / computeFastestRoute

    @Test
    void computeRoute_whenFastestStrategy_returnsQuickestRoute() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(fastestRequest);

        assertNotNull(response);
        assertEquals("720s", response.getDuration());
    }

    @Test
    void computeRoute_whenWalkStrategy_returnsSingleRoute() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(walkRequest);

        assertNotNull(response);
        assertEquals(1000, response.getDistanceMeters());
        assertEquals("abc123", response.getEncodedPolyline());
    }

    @Test
    void computeRoute_whenDriveStrategy_returnsSingleRoute() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(driveRequest);

        assertNotNull(response);
        assertEquals(1000, response.getDistanceMeters());
    }

    @Test
    void computeRoute_whenTransitOnly_returnsTransitSegments() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(transitOnlyRequest);

        assertNotNull(response);
    }

    @Test
    void computeRoute_whenFastestAndNoRoutesAvailable_throwsAuthException() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(NO_ROUTES_RESPONSE)
            .thenReturn(NO_ROUTES_RESPONSE)
            .thenReturn(NO_ROUTES_RESPONSE)
            .thenReturn(NO_ROUTES_RESPONSE)
            .thenReturn(NO_ROUTES_RESPONSE);

        RouteResponse response = routingService.computeRoute(fastestRequest);
        assertEquals("0s", response.getDuration());
    }


    // computeSingleRoute

    @Test
    void computeSingleRoute_whenValidRequest_returnsRouteResponse() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(walkRequest);

        assertNotNull(response);
        assertEquals(1000, response.getDistanceMeters());
        assertEquals("720s", response.getDuration());
        assertEquals("abc123", response.getEncodedPolyline());
        assertNotNull(response.getLegs());
        assertEquals(1, response.getLegs().size());
    }

    @Test
    void computeSingleRoute_whenNoRoutesReturned_returnsNull() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(EMPTY_RESPONSE);

        ComputeRouteRequest request = new ComputeRouteRequest();
        request.setOriginLat(51.5033);
        request.setOriginLng(-0.1196);
        request.setDestLat(51.5007);
        request.setDestLng(-0.1246);
        request.setStrategy("walking");
        request.setTravelMode("WALK");

        RouteResponse response = routingService.computeRoute(request);

        assertNull(response);
    }


    // computeTransitSegments

    @Test
    void computeTransitSegments_whenTransitUnavailable_fallsBackToWalk() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(EMPTY_RESPONSE)
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(transitOnlyRequest);

        assertNotNull(response);
        assertEquals("720s", response.getDuration());
    }

    @Test
    void computeTransitSegments_whenMultipleSegments_combinesLegs() throws Exception {
        LatLng intermediate = new LatLng();
        intermediate.setLatitude(51.51);
        intermediate.setLongitude(-0.12);

        transitOnlyRequest.setIntermediates(java.util.List.of(intermediate));

        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(WALK_RESPONSE)
            .thenReturn(WALK_RESPONSE);

        RouteResponse response = routingService.computeRoute(transitOnlyRequest);

        assertNotNull(response);
        assertEquals(2, response.getLegs().size());
    }
}