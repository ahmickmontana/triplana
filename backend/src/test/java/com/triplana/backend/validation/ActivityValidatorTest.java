package com.triplana.backend.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.entity.Activity;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.TripDay;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;

@ExtendWith(MockitoExtension.class)
public class ActivityValidatorTest {

    private ActivityValidator activityValidator = new ActivityValidator();
    private Activity mockActivity;

    @BeforeEach
    void setUp() {
        User mockUser = User.builder()
            .id(1L)
            .username("Mock User")
            .email("user@email.com")
            .build();

        Trip mockTrip = Trip.builder()
            .id(1L)
            .user(mockUser)
            .name("Mock Trip")
            .startDate(LocalDate.now().plusDays(1))
            .build();

        TripDay mockTripDay = TripDay.builder()
            .id(1L)
            .trip(mockTrip)
            .date(LocalDate.now().plusDays(1))
            .dayNumber(1)
            .build();

        mockActivity = Activity.builder()
            .id(1L)
            .tripDay(mockTripDay)
            .title("Mock Activity")
            .manualOrder(1)
            .build();
    }

    @Test
    void validateTimes_whenEndTimeWithoutStartTime_throwsAuthException() {
        LocalTime startTime = null;
        LocalTime endTime = LocalTime.of(12, 0);

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityValidator.validateTimes(startTime, endTime, null, null));

        assertEquals("End time requires a start time.", exception.getMessage());
    }

    @Test
    void validateTimes_whenEndTimeBeforeStartTime_throwsAuthException() {
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityValidator.validateTimes(startTime, endTime, null, null));

        assertEquals("End time must occur after the start time.", exception.getMessage());
    }

    @Test
    void validateTimes_whenEndTimeEqualsStartTime_throwsAuthException() {
        LocalTime startTime = LocalTime.of(12, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityValidator.validateTimes(startTime, endTime, null, null));

        assertEquals("End time must occur after the start time.", exception.getMessage());
    }

    @Test
    void validateTimes_whenStartTimeEqualsExistingStartTime_throwsAuthException() {
        LocalTime startTime = LocalTime.of(12, 0);

        mockActivity.setStartTime(LocalTime.of(12, 0));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityValidator.validateTimes(startTime, null, List.of(mockActivity), null));

        assertEquals("An activity is already planned at that time.", exception.getMessage());
    }

    @Test
    void validateTimes_whenStartTimeBetweenExisting_throwsAuthException() {
        LocalTime startTime = LocalTime.of(13, 0);

        mockActivity.setStartTime(LocalTime.of(12, 0));
        mockActivity.setEndTime(LocalTime.of(14, 0));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityValidator.validateTimes(startTime, null, List.of(mockActivity), null));

        assertEquals("An activity is already planned at that time.", exception.getMessage());
    }

    @Test
    void validateTimes_whenEndTimeBetweenExisting_throwsAuthException() {
        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(13, 0);

        mockActivity.setStartTime(LocalTime.of(12, 0));
        mockActivity.setEndTime(LocalTime.of(14, 0));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityValidator.validateTimes(startTime, endTime, List.of(mockActivity), null));

        assertEquals("This time conflicts with another activity.", exception.getMessage());
    }

    @Test
    void validateTimes_whenValidTimes_doesNotThrow() {
        LocalTime startTime = LocalTime.of(9, 30);
        LocalTime endTime = LocalTime.of(11, 30);

        mockActivity.setStartTime(LocalTime.of(12, 0));
        mockActivity.setEndTime(LocalTime.of(14, 0));

        assertDoesNotThrow(() -> 
            activityValidator.validateTimes(startTime, endTime, List.of(mockActivity), null));
    }

    @Test
    void validateTimes_whenBothTimesNull_doesNotThrow() {
        mockActivity.setStartTime(LocalTime.of(12, 0));
        mockActivity.setEndTime(LocalTime.of(14, 0));

        assertDoesNotThrow(() -> 
            activityValidator.validateTimes(null, null, List.of(mockActivity), null));
    }
}
