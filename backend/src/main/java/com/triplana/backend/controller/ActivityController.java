package com.triplana.backend.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triplana.backend.dto.request.CreateActivityRequest;
import com.triplana.backend.dto.response.ActivityResponse;
import com.triplana.backend.service.ActivityService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.*;

@RestController
@RequestMapping("/api/trips/{tripId}/days/{dayId}/activities")
@RequiredArgsConstructor
public class ActivityController {
    
    private final ActivityService activityService;

    
    @PostMapping()
    public ResponseEntity<ActivityResponse> createActivity(@PathVariable Long dayId,
            @Valid @RequestBody CreateActivityRequest request, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ActivityResponse response = activityService.createActivity(dayId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
