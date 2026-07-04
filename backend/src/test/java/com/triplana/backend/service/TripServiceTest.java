package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.dto.request.CreateTripRequest;
import com.triplana.backend.dto.request.UpdateTripRequest;
import com.triplana.backend.dto.response.TripResponse;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.validation.TripValidator;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {
    
    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripValidator tripValidator;

    @InjectMocks
    private TripService tripService;

    private User mockUser;
    private Trip mockTrip;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
            .id(1L)
            .username("Ahmick")
            .email("ahmick@email.com")
            .build();

        mockTrip = Trip.builder()
            .id(1L)
            .user(mockUser)
            .name("Japan Trip")
            .description("1 week trip to japan")
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(7))
            .build();
    }


    // createTrip()

    @Test
    void createTrip_whenValidInputs_returnsTripResponse() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        when(tripRepository.save(any())).thenReturn(mockTrip);

        CreateTripRequest request = new CreateTripRequest();
        request.setName("Japan Trip");
        request.setDescription("1 week trip to japan");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(7));

        TripResponse response = tripService.createTrip(1L, request);

        assertNotNull(response);
        assertEquals("Japan Trip", response.getName());
    }

    @Test
    void createTrip_whenEndBeforeStart_throwsAuthException() {
        doThrow(new AuthException("endDate", "End date cannot be before start date."))
            .when(tripValidator).validateTripDates(any(), any());

        CreateTripRequest request = new CreateTripRequest();
        request.setName("Japan Trip");
        request.setDescription("1 week trip to japan");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now());

        assertThrows(AuthException.class, () -> tripService.createTrip(1L, request));
    }

    @Test
    void createTrip_whenUserNotFound_throwsAuthException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        CreateTripRequest request = new CreateTripRequest();
        request.setName("Japan Trip");
        request.setDescription("1 week trip to japan");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now());

        assertThrows(AuthException.class, () -> tripService.createTrip(1L, request));
    }

    
    // getTrip()
    
    @Test
    void getTrip_whenValidTripAndOwner_returnsTripResponse() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        TripResponse response = tripService.getTrip(1L, 1L);

        assertNotNull(response);
        assertEquals(mockTrip.getName(), response.getName());
    }

    @Test
    void getTrip_whenTripNotFound_throwsAuthException() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> tripService.getTrip(1L, 1L));
    }

    @Test
    void getTrip_whenNotOwner_throwsAuthException() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> tripService.getTrip(1L, 999L));
    }


    // updateTrip()

    @Test
    void updateTrip_whenValidInputs_returnsTripResponse() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));
        when(tripRepository.save(any())).thenReturn(mockTrip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("Japan trip 2026");
        request.setDescription("Trip to osaka, kyoto and tokyo");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).plusDays(14));

        TripResponse response = tripService.updateTrip(1L, 1L, request);

        assertNotNull(response);
        assertEquals(mockTrip.getName(), request.getName());
        assertEquals(mockTrip.getDescription(), request.getDescription());
        assertEquals(mockTrip.getStartDate(), request.getStartDate());
        assertEquals(mockTrip.getEndDate(), request.getEndDate());
    }

    @Test
    void updateTrip_whenTripNotFound_throwsAuthException() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("Japan trip 2026");
        request.setDescription("Trip to osaka, kyoto and tokyo");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).plusDays(14));

        assertThrows(AuthException.class, () -> tripService.updateTrip(1L, 1L, request));
    }

    @Test
    void updateTrip_whenNotOwner_throwsAuthException() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("Japan trip 2026");
        request.setDescription("Trip to osaka, kyoto and tokyo");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).plusDays(14));

        assertThrows(AuthException.class, () -> tripService.updateTrip(1L, 999L, request));
    }

    @Test
    void updateTrip_whenRemoveCoverImage_setsCoverImagePathNull() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));
        when(tripRepository.save(any())).thenReturn(mockTrip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName(mockTrip.getName());
        request.setDescription(mockTrip.getDescription());
        request.setStartDate(mockTrip.getStartDate());
        request.setEndDate(mockTrip.getEndDate());
        request.setRemoveCoverImage(true);

        TripResponse response = tripService.updateTrip(1L, 1L, request);

        assertNotNull(response);
        assertNull(response.getCoverImagePath());
    }

    // deleteTrip()

    @Test
    void deleteTrip_whenValidTripAndOwner_deletesTrip() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        tripService.deleteTrip(1L, 1L);

        verify(tripRepository).delete(mockTrip);
    }

    @Test
    void deleteTrip_whenTripNotFound_throwsAuthException() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> tripService.deleteTrip(1L, 1L));
    }

    @Test
    void deleteTrip_whenNotOwner_throwsAuthException() {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        assertThrows(AuthException.class, () -> tripService.deleteTrip(1L, 999L));
    }
}
