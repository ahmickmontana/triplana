package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.triplana.backend.dto.response.PlaceDetailsResponse;
import com.triplana.backend.dto.response.PlaceSuggestionResponse;

@ExtendWith(MockitoExtension.class)
public class PlacesServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PlacesService placesService;
    

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(placesService, "apiKey", "test-api-key");
    }


    // getAutocompleteSuggestions()

    @Test
    void getAutocompleteSuggestions_whenValidInput_returnsSuggestions() throws Exception {
        String fakeResponse = "{\"predictions\": [{\"place_id\": \"ChIJ123\", \"structured_formatting\": {\"main_text\": \"Shibuya Crossing\", \"secondary_text\": \"Tokyo, Japan\"}}], \"status\": \"OK\"}";
        
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(fakeResponse);

        List<PlaceSuggestionResponse> suggestions = placesService.getAutocompleteSuggestions("shibuya", "establishment");

        assertEquals(1, suggestions.size());
        assertEquals("Shibuya Crossing", suggestions.get(0).getMainText());
        assertEquals("Tokyo, Japan", suggestions.get(0).getSecondaryText());
        assertEquals("ChIJ123", suggestions.get(0).getPlaceId());
    }

    @Test
    void getAutocompleteSuggestions_whenNoResults_returnsEmptyList() throws Exception {
        String fakeResponse = "{\"predictions\": [], \"status\": \"ZERO_RESULTS\"}";
        
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(fakeResponse);

        List<PlaceSuggestionResponse> suggestions = placesService.getAutocompleteSuggestions("xyzxyzxyz", "establishment");

        assertEquals(0, suggestions.size());
    }


    // getPlaceDetails()

    @Test
    void getPlaceDetails_whenValidPlaceId_returnsDetails() throws Exception {
        String fakeResponse = "{\"result\": {\"name\": \"Shibuya Crossing\", \"formatted_address\": \"21 Udagawa-cho, Shibuya, Tokyo\", \"geometry\": {\"location\": {\"lat\": 35.6595, \"lng\": 139.7004}}}, \"status\": \"OK\"}";
        
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(fakeResponse);

        PlaceDetailsResponse details = placesService.getPlaceDetails("ChIJ123");

        assertEquals("Shibuya Crossing", details.getName());
        assertEquals("21 Udagawa-cho, Shibuya, Tokyo", details.getFormattedAddress());
        assertEquals(35.6595, details.getLatitude());
        assertEquals(139.7004, details.getLongitude());
    }
}
