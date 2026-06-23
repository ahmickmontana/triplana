package com.triplana.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.triplana.backend.dto.request.CreateTripRequest;
import com.triplana.backend.dto.response.TripResponse;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.validation.TripValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    private UserRepository userRepository;

    private final TripValidator tripValidator;

    public List<TripResponse> getTrips(Long userId) {
        List<Trip> trips = tripRepository.findAllByUserId(userId);
        List<TripResponse> tripResponses = new ArrayList<>();

        for (Trip trip : trips) {
            tripResponses.add(TripResponse.from(trip));
        }

        return tripResponses;
    }

    public TripResponse createTrip(Long userId, CreateTripRequest request) {
        // Validation
        tripValidator.validateTripDates(request.getStartDate(), request.getEndDate());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found."));

        // Create trip
        Trip trip = Trip.builder()
            .user(user)
            .name(request.getName())
            .description(request.getDescription())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .coverImagePath(request.getCoverImagePath())
            .build();

        // Save trip to repository
        tripRepository.save(trip);

        return TripResponse.from(trip);
    }
}
