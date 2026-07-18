package com.triplana.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.triplana.backend.dto.request.CreateActivityRequest;
import com.triplana.backend.dto.request.UpdateActivityRequest;
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


    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getActivities(
            @PathVariable Long dayId, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(activityService.getActivities(dayId, userId));
    }
    
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

    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> updateActivity(@PathVariable Long dayId, @PathVariable Long activityId,
        @Valid @RequestBody UpdateActivityRequest request, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ActivityResponse response = activityService.updateActivity(dayId, activityId, userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long activityId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        activityService.deleteActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }
}
