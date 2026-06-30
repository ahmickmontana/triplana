package com.triplana.backend.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.triplana.backend.exception.AuthException;



@Component
public class TripValidator {
    
    public void validateTripDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new AuthException("endDate", "End date cannot be before start date.");
        }
    }
}
