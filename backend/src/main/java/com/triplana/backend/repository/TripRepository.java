package com.triplana.backend.repository;

import com.triplana.backend.entity.Trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findAllByUserId(Long userId);

}