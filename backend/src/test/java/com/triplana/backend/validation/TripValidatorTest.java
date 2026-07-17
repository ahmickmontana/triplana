package com.triplana.backend.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.exception.AuthException;

@ExtendWith(MockitoExtension.class)
public class TripValidatorTest {

    private TripValidator tripValidator = new TripValidator();

    private LocalDate startDate;
    private LocalDate endDate;


    @Test
    void validateTripDates_whenEndBeforeStart_throwsAuthException() {
        startDate = LocalDate.now();
        endDate = LocalDate.now().minusDays(1);

        AuthException exception = assertThrows(AuthException.class, () ->
            tripValidator.validateTripDates(startDate, endDate));

        assertEquals("End date cannot be before start date.", exception.getMessage());
    }

    @Test
    void validateTripDates_whenStartAndEndSame_doesNotThrow() {
        startDate = LocalDate.now();
        endDate = LocalDate.now();

        assertDoesNotThrow(() -> tripValidator.validateTripDates(startDate, endDate));
    }

    @Test
    void validateTripDates_whenEndAfterStart_doesNotThrow() {
        startDate = LocalDate.now();
        endDate = LocalDate.now().plusDays(1);

        assertDoesNotThrow(() -> tripValidator.validateTripDates(startDate, endDate));
    }

    @Test
    void validateTripDates_whenStartDateNull_doesNotThrow() {
        startDate = null;
        endDate = LocalDate.now();

        assertDoesNotThrow(() -> tripValidator.validateTripDates(startDate, endDate));
    }

    @Test
    void validateTripDates_whenEndDateNull_doesNotThrow() {
        startDate = LocalDate.now();
        endDate = null;

        assertDoesNotThrow(() -> tripValidator.validateTripDates(startDate, endDate));
    }

    @Test
    void validateTripDates_whenBothNull_doesNotThrow() {
        startDate = null;
        endDate = null;

        assertDoesNotThrow(() -> tripValidator.validateTripDates(startDate, endDate));
    }
}