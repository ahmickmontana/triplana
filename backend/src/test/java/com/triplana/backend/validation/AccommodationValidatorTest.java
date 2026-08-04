package com.triplana.backend.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.entity.Accommodation;
import com.triplana.backend.exception.AuthException;

@ExtendWith(MockitoExtension.class)
public class AccommodationValidatorTest {
    
    private final AccommodationValidator accommodationValidator = new AccommodationValidator();
    private Accommodation mockAccommodation;

    @BeforeEach
    void setUp() {
        mockAccommodation = Accommodation.builder()
            .id(1L)
            .checkInDate(LocalDate.of(2027, 3, 15))
            .checkOutDate(LocalDate.of(2027, 3, 20))
            .build();
    }


    @Test
    void validateDates_whenCheckOutBeforeCheckIn_throwsAuthException() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 15);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 10);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, null, null));

        assertEquals("Check-in must be before check-out.", exception.getMessage());
    }

    @Test
    void validateDates_whenCheckOutEqualsCheckIn_throwsAuthException() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 15);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 15);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, null, null));

        assertEquals("Check-in must be before check-out.", exception.getMessage());
    }

    @Test
    void validateDates_whenCheckInBeforeTripStart_throwsAuthException() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 9);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 15);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, null, null));

        assertEquals("Accommodation must be within trip dates.", exception.getMessage());
    }

    @Test
    void validateDates_whenCheckOutAfterTripEnd_throwsAuthException() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 10);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 21);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, null, null));

        assertEquals("Accommodation must be within trip dates.", exception.getMessage());
    }

    @Test
    void validateDates_whenDatesOverlapWithExisting_throwsAuthException() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 13);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 18);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, List.of(mockAccommodation), null));

        assertEquals("Accommodation dates overlap with another stay.", exception.getMessage());
    }

    @Test
    void validateDates_whenCheckInEqualsExistingCheckIn_throwsAuthException() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 15);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 18);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, List.of(mockAccommodation), null));

        assertEquals("Accommodation dates overlap with another stay.", exception.getMessage());
    }

    @Test
    void validateDates_whenValidDates_doesNotThrow() {
        LocalDate checkInDate = LocalDate.of(2027, 3, 10);
        LocalDate checkOutDate = LocalDate.of(2027, 3, 15);

        LocalDate tripStartDate = LocalDate.of(2027, 3, 10);
        LocalDate tripEndDate = LocalDate.of(2027, 3, 20);

        assertDoesNotThrow(() -> 
            accommodationValidator.validateDates(checkInDate, checkOutDate, tripStartDate, tripEndDate, List.of(mockAccommodation), null));
    }
}
