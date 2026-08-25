package com.triplana.backend.dto.response;

import lombok.Data;

@Data
public class PlaceSuggestionResponse {
    private String placeId;
    private String mainText;
    private String secondaryText;
}
