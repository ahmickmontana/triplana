package com.triplana.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triplana.backend.dto.request.CreateAccommodationRequest;
import com.triplana.backend.dto.request.UpdateAccommodationRequest;
import com.triplana.backend.dto.response.AccommodationResponse;
import com.triplana.backend.service.AccommodationService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trips/{tripId}/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    
    private final AccommodationService accommodationService;

    @PostMapping
    public ResponseEntity<AccommodationResponse> createAccommodation(@PathVariable Long tripId,
        @Valid @RequestBody CreateAccommodationRequest request, HttpSession session) {
            
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AccommodationResponse response = accommodationService.createAccommodation(tripId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccommodationResponse>> getAccommodations(@PathVariable Long tripId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(accommodationService.getAccommodations(tripId, userId));
    }

    @PutMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponse> updateAccommodation(@PathVariable Long tripId, @PathVariable Long accommodationId,
        @Valid @RequestBody UpdateAccommodationRequest request, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AccommodationResponse response = accommodationService.updateAccommodation(tripId, accommodationId, userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long tripId, @PathVariable Long accommodationId, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        accommodationService.deleteAccommodation(accommodationId, userId);

        return ResponseEntity.noContent().build();
    }
}
