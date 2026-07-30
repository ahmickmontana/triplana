package com.triplana.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.response.PlaceDetailsResponse;
import com.triplana.backend.dto.response.PlaceSuggestionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlacesService {

    @Value("${google.places.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    

    public List<PlaceSuggestionResponse> getAutocompleteSuggestions(String input, String types) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input + "&key=" + apiKey + "&language=en&types=" + types;

        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> body = mapper.readValue(response, new TypeReference<Map<String, Object>>() {});

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) body.get("predictions");

        List<PlaceSuggestionResponse> suggestions = new ArrayList<>();

        for (Map<String, Object> prediction : predictions) {
            @SuppressWarnings("unchecked")
            Map<String, Object> structuredFormatting = (Map<String, Object>) prediction.get("structured_formatting");

            PlaceSuggestionResponse suggestion = new PlaceSuggestionResponse();

            suggestion.setPlaceId((String) prediction.get("place_id"));
            suggestion.setMainText((String) structuredFormatting.get("main_text"));
            suggestion.setSecondaryText((String) structuredFormatting.get("secondary_text"));
            suggestions.add(suggestion);
        }

        return suggestions.stream().limit(5).toList();
    }

    
    public PlaceDetailsResponse getPlaceDetails(String placeId) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" 
            + placeId + "&fields=name,geometry,formatted_address&key=" + apiKey;

        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> body = mapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> result = mapper.convertValue(body.get("result"), new TypeReference<Map<String, Object>>() {});
        Map<String, Object> geometry = mapper.convertValue(result.get("geometry"), new TypeReference<Map<String, Object>>() {});
        Map<String, Object> location = mapper.convertValue(geometry.get("location"), new TypeReference<Map<String, Object>>() {});

        PlaceDetailsResponse details = new PlaceDetailsResponse();

        details.setName((String) result.get("name"));
        details.setFormattedAddress((String) result.get("formatted_address"));
        details.setLatitude(((Number) location.get("lat")).doubleValue());
        details.setLongitude(((Number) location.get("lng")).doubleValue());

        return details;
    }
}
