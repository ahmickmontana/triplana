package com.triplana.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.triplana.backend.dto.request.CreateAccommodationRequest;
import com.triplana.backend.dto.request.UpdateAccommodationRequest;
import com.triplana.backend.dto.response.AccommodationResponse;
import com.triplana.backend.entity.Accommodation;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.AccommodationRepository;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.validation.AccommodationValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    
    private final AccommodationRepository accommodationRepository;
    private final TripRepository tripRepository;
    private final AccommodationValidator accommodationValidator;


    public AccommodationResponse createAccommodation(Long tripId, Long userId, CreateAccommodationRequest request) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found."));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip.");
        }

        List<Accommodation> accommodations = accommodationRepository.findAllByTripIdOrderByCheckInDateAsc(tripId);

        accommodationValidator.validateDates(request.getCheckInDate(), request.getCheckOutDate(), trip.getStartDate(), trip.getEndDate(), accommodations, null);

        Accommodation accommodation = Accommodation.builder()
            .trip(trip)
            .name(request.getName())
            .locationName(request.getLocationName())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .googlePlaceId(request.getGooglePlaceId())
            .checkInDate(request.getCheckInDate())
            .checkOutDate(request.getCheckOutDate())
            .build();

        accommodationRepository.save(accommodation);

        return AccommodationResponse.from(accommodation);
    }

    public List<AccommodationResponse> getAccommodations(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found."));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip.");
        }

        return accommodationRepository.findAllByTripIdOrderByCheckInDateAsc(tripId)
            .stream()
            .map(AccommodationResponse::from)
            .toList();
    }

    public AccommodationResponse updateAccommodation(Long tripId, Long accommodationId, Long userId, UpdateAccommodationRequest request) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found."));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip.");
        }

        List<Accommodation> accommodations = accommodationRepository.findAllByTripIdOrderByCheckInDateAsc(tripId);

        accommodationValidator.validateDates(request.getCheckInDate(), request.getCheckOutDate(), trip.getStartDate(), trip.getEndDate(), accommodations, accommodationId);

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(() -> new AuthException("Accommodation not found."));

        accommodation.setName(request.getName());
        accommodation.setLocationName(request.getLocationName());
        accommodation.setLatitude(request.getLatitude());
        accommodation.setLongitude(request.getLongitude());
        accommodation.setGooglePlaceId(request.getGooglePlaceId());
        accommodation.setCheckInDate(request.getCheckInDate());
        accommodation.setCheckOutDate(request.getCheckOutDate());

        accommodationRepository.save(accommodation);

        return AccommodationResponse.from(accommodation);
    }

    public void deleteAccommodation(Long accommodationId, Long userId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(() -> new AuthException("Accommodation not found."));

        if (!accommodation.getTrip().getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to delete this accommodation.");
        }

        accommodationRepository.delete(accommodation);
    }
}
