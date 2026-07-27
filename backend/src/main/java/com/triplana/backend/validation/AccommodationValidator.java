package com.triplana.backend.validation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.triplana.backend.entity.Accommodation;
import com.triplana.backend.exception.AuthException;

@Component
public class AccommodationValidator {
    
    public void validateDates(LocalDate checkInDate, LocalDate checkOutDate, LocalDate tripStart, LocalDate tripEnd, List<Accommodation> existingAccommodations, Long excludeId) {
        if (checkOutDate != null && checkInDate != null && !checkOutDate.isAfter(checkInDate)) {
            throw new AuthException("checkOutDate", "Check-in must be before check-out.");
        }

        if (checkInDate.isBefore(tripStart) || (tripEnd != null && checkOutDate.isAfter(tripEnd))) {
            throw new AuthException("checkOutDate", "Accommodation must be within trip dates.");
        }

        if (checkInDate != null) {
            for (Accommodation existing : existingAccommodations) {
                if (excludeId != null && existing.getId().equals(excludeId)) continue;

                if (existing.getCheckInDate() != null && existing.getCheckInDate().equals(checkInDate)) {
                    throw new AuthException("checkInDate", "Accommodation dates overlap with another stay.");
                }

                if (existing.getCheckInDate() != null && existing.getCheckOutDate() != null) {
                    if (checkInDate.isAfter(existing.getCheckInDate()) && checkInDate.isBefore(existing.getCheckOutDate())) {
                        throw new AuthException("checkInDate", "Accommodation dates overlap with another stay.");
                    }

                    if (checkOutDate != null && checkInDate.isBefore(existing.getCheckOutDate()) && checkOutDate.isAfter(existing.getCheckInDate())) {
                        throw new AuthException("checkInDate", "Accommodation dates overlap with another stay.");
                    }
                }
            }
        }
    }
}
