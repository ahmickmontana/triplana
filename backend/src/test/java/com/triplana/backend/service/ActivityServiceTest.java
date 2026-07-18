package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.dto.request.CreateActivityRequest;
import com.triplana.backend.dto.request.UpdateActivityRequest;
import com.triplana.backend.dto.response.ActivityResponse;
import com.triplana.backend.entity.Activity;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.TripDay;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.ActivityRepository;
import com.triplana.backend.repository.TripDayRepository;
import com.triplana.backend.validation.ActivityValidator;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {
    
    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TripDayRepository tripDayRepository;

    @Mock
    private ActivityValidator activityValidator;

    @InjectMocks
    private ActivityService activityService;

    private User mockUser;
    private Trip mockTrip;
    private TripDay mockTripDay;
    private Activity mockActivity;
    private Activity mockActivity2;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
            .id(1L)
            .username("Mock User")
            .email("user@email.com")
            .build();

        mockTrip = Trip.builder()
            .id(1L)
            .user(mockUser)
            .name("Mock Trip")
            .startDate(LocalDate.now().plusDays(1))
            .build();

        mockTripDay = TripDay.builder()
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
        
        mockActivity2 = Activity.builder()
            .id(2L)
            .tripDay(mockTripDay)
            .title("Mock Activity 2")
            .manualOrder(1)
            .build();
    }

    // createActivity()

    @Test
    void createActivity_whenValidInputs_returnsActivityResponse() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTripDay));

        CreateActivityRequest request = new CreateActivityRequest();
        request.setDescription("mock activity request");
        request.setTitle("mock activity title");
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(13, 0));

        ActivityResponse response = activityService.createActivity(1L, request, 1L);

        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getStartTime(), response.getStartTime());
        assertEquals(request.getEndTime(), response.getEndTime());
    }

    @Test
    void createActivity_whenTripDayNotFound_throwsAuthException() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        CreateActivityRequest request = new CreateActivityRequest();
        request.setDescription("mock activity request");
        request.setTitle("mock activity title");
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(13, 0));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityService.createActivity(1L, request, 1L));

        assertEquals("Trip day not found.", exception.getMessage());

    }

    @Test
    void createActivity_whenNotOwner_throwsAuthException() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTripDay));

        CreateActivityRequest request = new CreateActivityRequest();
        request.setDescription("mock activity request");
        request.setTitle("mock activity title");
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(13, 0));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityService.createActivity(1L, request, 999L));

        assertEquals("You do not have permission to view this trip day.", exception.getMessage());
    }

    // getActivities()

    @Test
    void getActivities_whenValidDayAndOwner_returnsActivityList() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTripDay));
        when(activityRepository.findAllByTripDayIdOrderByManualOrderAsc(any(Long.class))).thenReturn(List.of(mockActivity, mockActivity2));

        List<ActivityResponse> responseList = activityService.getActivities(1L, 1L);

        assertFalse(responseList.isEmpty());
        assertEquals(2, responseList.size());
        assertEquals(mockActivity.getTitle(), responseList.get(0).getTitle());
        assertEquals(mockActivity2.getTitle(), responseList.get(1).getTitle());
    }

    @Test
    void getActivities_whenNotOwner_throwsAuthException() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTripDay));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityService.getActivities(1L, 999L));

        assertEquals("You do not have permission to view this trip day.", exception.getMessage());
    }

    // updateActivity()

    @Test
    void updateActivity_whenValidInputs_returnsActivityResponse() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTripDay));
        when(activityRepository.findAllByTripDayIdOrderByManualOrderAsc(any(Long.class))).thenReturn(List.of(mockActivity));
        when(activityRepository.findById(any(Long.class))).thenReturn(Optional.of(mockActivity));
        when(activityRepository.save(any(Activity.class))).thenReturn(mockActivity);

        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setTitle("new activity title");
        request.setDescription("new activity description");
        request.setStartTime(LocalTime.of(15, 0));
        request.setEndTime(LocalTime.of(18, 0));
        request.setLocationName("new location name");

        ActivityResponse response = activityService.updateActivity(1L, 1L, 1L, request);

        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getStartTime(), response.getStartTime());
        assertEquals(request.getEndTime(), response.getEndTime());
        assertEquals(request.getLocationName(), response.getLocationName());
    }

    @Test
    void updateActivity_whenTripDayNotFound_throwsAuthException() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setTitle("new activity title");
        request.setDescription("new activity description");
        request.setStartTime(LocalTime.of(15, 0));
        request.setEndTime(LocalTime.of(18, 0));
        request.setLocationName("new location name");

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityService.updateActivity(1L, 1L, 1L, request));

        assertEquals("Trip day not found.", exception.getMessage());
    }

    @Test
    void updateActivity_whenNotOwner_throwsAuthException() {
        when(tripDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTripDay));

        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setTitle("new activity title");
        request.setDescription("new activity description");
        request.setStartTime(LocalTime.of(15, 0));
        request.setEndTime(LocalTime.of(18, 0));
        request.setLocationName("new location name");

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityService.updateActivity(1L, 1L, 999L, request));

        assertEquals("You do not have permission to access this trip day.", exception.getMessage());
    }

    // deleteActivity()

    @Test
    void deleteActivity_whenValidActivityAndOwner_deletesActivity() {
        when(activityRepository.findById(any(Long.class))).thenReturn(Optional.of(mockActivity));

        activityService.deleteActivity(1L, 1L);

        verify(activityRepository).delete(mockActivity);
    }

    @Test
    void deleteActivity_whenNotOwner_throwsAuthException() {
        when(activityRepository.findById(any(Long.class))).thenReturn(Optional.of(mockActivity));

        AuthException exception = assertThrows(AuthException.class, () -> 
            activityService.deleteActivity(1L, 999L));

        assertEquals("You do not have permission to delete this activity.", exception.getMessage());
    }
}
