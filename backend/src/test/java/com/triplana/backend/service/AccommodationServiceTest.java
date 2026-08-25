package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.dto.request.CreateAccommodationRequest;
import com.triplana.backend.dto.request.UpdateAccommodationRequest;
import com.triplana.backend.dto.response.AccommodationResponse;
import com.triplana.backend.entity.Accommodation;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.AccommodationRepository;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.validation.AccommodationValidator;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {
    
    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private AccommodationValidator accommodationValidator;

    @InjectMocks
    private AccommodationService accommodationService;

    private User mockUser;
    private Trip mockTrip;
    private Accommodation mockAccommodation;

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
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(14))
            .build();

        mockAccommodation = Accommodation.builder()
            .id(1L)
            .trip(mockTrip)
            .name("Shinjuku Granbell Hotel")
            .checkInDate(LocalDate.now().plusDays(1))
            .checkOutDate(LocalDate.now().plusDays(5))
            .build();
    }

    // createAccommodations()

    @Test
    void createAccommodation_whenValidInputs_returnsAccommodationResponse() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName(mockAccommodation.getName());
        request.setCheckInDate(mockAccommodation.getCheckInDate());
        request.setCheckOutDate(mockAccommodation.getCheckOutDate());
        request.setGooglePlaceId(null);
        request.setLatitude(null);
        request.setLongitude(null);
        request.setLocationName(null);

        AccommodationResponse response = accommodationService.createAccommodation(1L, 1L, request);

        assertEquals(response.getName(), request.getName());
        assertEquals(response.getCheckInDate(), request.getCheckInDate());
        assertEquals(response.getCheckOutDate(), request.getCheckOutDate());
        assertEquals(response.getGooglePlaceId(), request.getGooglePlaceId());
        assertEquals(response.getLatitude(), request.getLatitude());
        assertEquals(response.getLongitude(), request.getLongitude());
        assertEquals(response.getLocationName(), request.getLocationName());
    }

    @Test
    void createAccommodation_whenTripNotFound_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName(mockAccommodation.getName());
        request.setCheckInDate(mockAccommodation.getCheckInDate());
        request.setCheckOutDate(mockAccommodation.getCheckOutDate());
        request.setGooglePlaceId(null);
        request.setLatitude(null);
        request.setLongitude(null);
        request.setLocationName(null);

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.createAccommodation(1L, 1L, request));

        assertEquals("Trip not found.", exception.getMessage());
    }

    @Test
    void createAccommodation_whenNotOwner_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName(mockAccommodation.getName());
        request.setCheckInDate(mockAccommodation.getCheckInDate());
        request.setCheckOutDate(mockAccommodation.getCheckOutDate());
        request.setGooglePlaceId(null);
        request.setLatitude(null);
        request.setLongitude(null);
        request.setLocationName(null);

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.createAccommodation(1L, 999L, request));

        assertEquals("You do not have permission to view this trip.", exception.getMessage());
    }

    // getAccommodations()

    @Test
    void getAccommodations_whenValidTripAndOwner_returnsAccommodationList() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));
        when(accommodationRepository.findAllByTripIdOrderByCheckInDateAsc(any(Long.class))).thenReturn(List.of(mockAccommodation));

        List<AccommodationResponse> responseList = accommodationService.getAccommodations(1L, 1L);

        assertEquals(1, responseList.size());
        assertEquals(mockAccommodation.getCheckInDate(), responseList.get(0).getCheckInDate());
        assertEquals(mockAccommodation.getCheckOutDate(), responseList.get(0).getCheckOutDate());
        assertEquals(mockAccommodation.getName(), responseList.get(0).getName());
        assertEquals(mockAccommodation.getGooglePlaceId(), responseList.get(0).getGooglePlaceId());
        assertEquals(mockAccommodation.getLatitude(), responseList.get(0).getLatitude());
        assertEquals(mockAccommodation.getLongitude(), responseList.get(0).getLongitude());
        assertEquals(mockAccommodation.getLocationName(), responseList.get(0).getLocationName());
    }

    @Test
    void getAccommodations_whenTripNotFound_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.getAccommodations(1L, 1L));

        assertEquals("Trip not found.", exception.getMessage());
    }

    @Test
    void getAccommodations_whenNotOwner_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.getAccommodations(1L, 999L));

        assertEquals("You do not have permission to view this trip.", exception.getMessage());
    }

    // updateAccommodation()

    @Test
    void updateAccommodation_whenValidInputs_returnsAccommodationResponse() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));
        when(accommodationRepository.findById(any(Long.class))).thenReturn(Optional.of(mockAccommodation));

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Accommodation");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(10));

        AccommodationResponse response = accommodationService.updateAccommodation(1L, 1L, 1L, request);

        assertEquals(request.getName(), response.getName());
        assertEquals(request.getCheckInDate(), response.getCheckInDate());
        assertEquals(request.getCheckOutDate(), response.getCheckOutDate());
    }

    @Test
    void updateAccommodation_whenTripNotFound_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Accommodation");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(10));

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.updateAccommodation(1L, 1L, 1L, request));

        assertEquals("Trip not found.", exception.getMessage());
    }

    @Test
    void updateAccommodation_whenAccommodationNotFound_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));
        when(accommodationRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Accommodation");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(10));

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.updateAccommodation(1L, 1L, 1L, request));

        assertEquals("Accommodation not found.", exception.getMessage());
    }

    @Test
    void updateAccommodation_whenNotOwner_throwsAuthException() throws Exception {
        when(tripRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTrip));

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Accommodation");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(10));

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.updateAccommodation(1L, 1L, 999L, request));

        assertEquals("You do not have permission to view this trip.", exception.getMessage());
    }

    // deleteAccommodation()

    @Test
    void deleteAccommodation_whenValidAccommodationAndOwner_deletesAccommodation() throws Exception {
        when(accommodationRepository.findById(any(Long.class))).thenReturn(Optional.of(mockAccommodation));

        accommodationService.deleteAccommodation(1L, 1L);

        verify(accommodationRepository).delete(mockAccommodation);
    }

    @Test
    void deleteAccommodation_whenAccommodationNotFound_deletesAccommodation() throws Exception {
        when(accommodationRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.deleteAccommodation(1L, 1L));

        assertEquals("Accommodation not found.", exception.getMessage());
    }

    @Test
    void deleteAccommodation_whenNotOwner_deletesAccommodation() throws Exception {
        when(accommodationRepository.findById(any(Long.class))).thenReturn(Optional.of(mockAccommodation));

        AuthException exception = assertThrows(AuthException.class, () -> 
            accommodationService.deleteAccommodation(1L, 999L));

        assertEquals("You do not have permission to delete this accommodation.", exception.getMessage());
    }
}
