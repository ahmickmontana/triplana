package com.triplana.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.triplana.backend.dto.response.TripResponse;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.repository.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    public List<TripResponse> getTrips(Long userId) {
        List<Trip> trips = tripRepository.findAllByUserId(userId);
        List<TripResponse> tripResponses = new ArrayList<>();

        for (Trip trip : trips) {
            tripResponses.add(TripResponse.from(trip));
        }

        return tripResponses;
    }
}
