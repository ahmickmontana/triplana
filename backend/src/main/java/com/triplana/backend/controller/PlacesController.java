package com.triplana.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.triplana.backend.dto.response.PlaceDetailsResponse;
import com.triplana.backend.dto.response.PlaceSuggestionResponse;
import com.triplana.backend.service.PlacesService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlacesController {
    
    private final PlacesService placesService;


    @GetMapping("/autocomplete")
    public ResponseEntity<List<PlaceSuggestionResponse>> getAutocompleteSuggestions(
        @RequestParam String input, 
        @RequestParam(defaultValue = "establishment") String types) throws Exception {
        return ResponseEntity.ok(placesService.getAutocompleteSuggestions(input, types));
    }

    @GetMapping("/details")
    public ResponseEntity<PlaceDetailsResponse> getPlaceDetails(@RequestParam String placeId) throws Exception {
        return ResponseEntity.ok(placesService.getPlaceDetails(placeId));
    }
}
