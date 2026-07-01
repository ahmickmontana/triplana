package com.triplana.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.triplana.backend.dto.request.CreateTripRequest;
import com.triplana.backend.dto.request.UpdateTripRequest;
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

    private final UserRepository userRepository;

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
            .build();

        // Save trip to repository
        tripRepository.save(trip);

        return TripResponse.from(trip);
    }

    public TripResponse uploadCoverImage(Long tripId, Long userId, MultipartFile image) throws IOException {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found"));
        
        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to edit this trip");
        }

        String uploadDir = "uploads/covers/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = "trip-" + tripId + getExtension(image.getOriginalFilename());
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, image.getBytes());

        trip.setCoverImagePath("/" + uploadDir + fileName);
        tripRepository.save(trip);

        return TripResponse.from(trip);
    }

    public TripResponse getTrip(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found."));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip.");
        }

        return TripResponse.from(trip);
    }

    public TripResponse updateTrip(Long tripId, Long userId, UpdateTripRequest request) {
        tripValidator.validateTripDates(request.getStartDate(), request.getEndDate());

        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found."));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip.");
        }

        trip.setName(request.getName());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());

        tripRepository.save(trip);

        return TripResponse.from(trip);
    }

    public void deleteTrip(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new AuthException("Trip not found."));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip.");
        }

        tripRepository.delete(trip);
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}
