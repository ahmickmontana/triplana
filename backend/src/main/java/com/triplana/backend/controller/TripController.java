package com.triplana.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triplana.backend.dto.request.CreateTripRequest;
import com.triplana.backend.dto.response.TripResponse;
import com.triplana.backend.service.TripService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    
    private final TripService tripService;

    @GetMapping("/")
    public ResponseEntity<List<TripResponse>> getTrips(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<TripResponse> tripResponses = tripService.getTrips(userId);

        return ResponseEntity.ok(tripResponses);
    }

    @PostMapping("/")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TripResponse response = tripService.createTrip(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
    }
    


}
