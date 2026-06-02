package com.triplana.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Generic API response for simple success or error message
 */
@Data
@AllArgsConstructor
public class ApiResponse {
    
    private boolean success;
    private String message;
}
