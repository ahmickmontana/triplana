package com.triplana.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.triplana.backend.dto.request.CreateTripRequest;
import com.triplana.backend.dto.response.TripResponse;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TripRepository;
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

    private final TripRepository tripRepository;

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

    @PostMapping("/{id}/cover-image")
    public ResponseEntity<TripResponse> uploadCoverImage(
        @PathVariable Long id,
        @RequestParam("image") MultipartFile image,
        HttpSession session) throws IOException {

            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            TripResponse response = tripService.uploadCoverImage(id, userId, image);
            return ResponseEntity.ok(response);
        }
    
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        
        TripResponse response = tripService.getTrip(id, userId);
        return ResponseEntity.ok(response);
    }
}
