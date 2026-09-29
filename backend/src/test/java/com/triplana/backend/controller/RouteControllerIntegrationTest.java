package com.triplana.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.request.ComputeRouteRequest;
import com.triplana.backend.dto.response.RouteResponse;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.service.EmailService;
import com.triplana.backend.service.RoutingService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoutingService routingService;

    @MockitoBean
    private EmailService emailService;

    @Test
    void computeRoute_whenValidInputs_returnsOk() throws Exception {
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setDistanceMeters(1000);
        mockResponse.setDuration("720s");
        mockResponse.setEncodedPolyline("abc123");

        when(routingService.computeRoute(any())).thenReturn(mockResponse);

        ComputeRouteRequest request = new ComputeRouteRequest();
        request.setOriginLat(51.5033);
        request.setOriginLng(-0.1196);
        request.setDestLat(51.5007);
        request.setDestLng(-0.1246);
        request.setStrategy("fastest");
        request.setTravelMode("WALK");

        mockMvc.perform(post("/api/routes/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.distanceMeters").value(1000))
            .andExpect(jsonPath("$.duration").value("720s"));
    }

    @Test
    void computeRoute_whenServiceThrowsException_returnsBadRequest() throws Exception {
        when(routingService.computeRoute(any())).thenThrow(new AuthException("No route available with the selected preferences."));

        ComputeRouteRequest request = new ComputeRouteRequest();
        request.setOriginLat(51.5033);
        request.setOriginLng(-0.1196);
        request.setDestLat(51.5007);
        request.setDestLng(-0.1246);
        request.setStrategy("fastest");
        request.setTravelMode("WALK");

        mockMvc.perform(post("/api/routes/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}