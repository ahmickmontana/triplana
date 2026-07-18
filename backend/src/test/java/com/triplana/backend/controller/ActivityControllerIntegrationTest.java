package com.triplana.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.request.CreateActivityRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.UpdateActivityRequest;
import com.triplana.backend.repository.ActivityRepository;
import com.triplana.backend.repository.TripDayRepository;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.service.EmailService;

import jakarta.transaction.Transactional;

import com.triplana.backend.entity.Activity;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.TripDay;
import com.triplana.backend.entity.User;

import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ActivityControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripDayRepository tripDayRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;


    // Helper method to create user and get a SESSION cookie since most endpoints require auth.
    private Cookie loginAndGetCookie(String email) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("Password1!");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        return result.getResponse().getCookie("SESSION");
    }

    private User createUser(String username, String email) {
        User user = User.builder()
            .username(username)
            .email(email)
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        return userRepository.save(user);
    }


    // POST /api/trips/{tripId}/days/{dayId}/activities

    @Test
    void createActivity_whenValidInputs_returnsCreated() throws Exception {
        createUser("testUser", "user@email.com");
        Cookie sessionCookie = loginAndGetCookie("user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        CreateActivityRequest request = new CreateActivityRequest();
        request.setTitle("Activity title");
        request.setDescription("Activity description");
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(13, 0));
        request.setLocationName("Activity location");

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertEquals(1, activityRepository.findAllByTripDayIdOrderByManualOrderAsc(tripDay.getId()).size());
    }

    @Test
    void createActivity_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        CreateActivityRequest request = new CreateActivityRequest();
        request.setTitle("Activity title");
        request.setDescription("Activity description");
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(13, 0));
        request.setLocationName("Activity location");

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertEquals(0, activityRepository.findAllByTripDayIdOrderByManualOrderAsc(tripDay.getId()).size());
    }

    @Test
    void createActivity_whenNameEmpty_returnsBadRequest() throws Exception {
        createUser("testUser", "user@email.com");
        Cookie sessionCookie = loginAndGetCookie("user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        CreateActivityRequest request = new CreateActivityRequest();
        request.setTitle("");
        request.setDescription("Activity description");
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(13, 0));
        request.setLocationName("Activity location");

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, activityRepository.findAllByTripDayIdOrderByManualOrderAsc(tripDay.getId()).size());
    }


    // GET /api/trips/{tripId}/days/{dayId}/activities

    @Test
    void getActivities_whenValidDayAndOwner_returnsActivities() throws Exception {
        createUser("testUser", "user@email.com");
        Cookie sessionCookie = loginAndGetCookie("user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        mockMvc.perform(get("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value(activity.getTitle()));
    }

    @Test
    void getActivities_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        mockMvc.perform(get("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    // PUT /api/trips/{tripId}/days/{dayId}/activities/{activityId}

    @Test
    void updateActivity_whenValidInputs_activityIsUpdated() throws Exception {
        createUser("testUser", "user@email.com");
        Cookie sessionCookie = loginAndGetCookie("user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setTitle("New activity title");
        request.setDescription("New activity description");
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setLocationName("New activity location");

        mockMvc.perform(put("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        assertEquals(request.getTitle(), updatedActivity.getTitle());
        assertEquals(request.getDescription(), updatedActivity.getDescription());
        assertEquals(request.getStartTime(), updatedActivity.getStartTime());
        assertEquals(request.getEndTime(), updatedActivity.getEndTime());
        assertEquals(request.getLocationName(), updatedActivity.getLocationName());
    }

    @Test
    void updateActivity_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setTitle("New activity title");
        request.setDescription("New activity description");
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setLocationName("New activity location");

        mockMvc.perform(put("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        assertNotEquals(request.getTitle(), updatedActivity.getTitle());
        assertNotEquals(request.getDescription(), updatedActivity.getDescription());
        assertNotEquals(request.getStartTime(), updatedActivity.getStartTime());
        assertNotEquals(request.getEndTime(), updatedActivity.getEndTime());
        assertNotEquals(request.getLocationName(), updatedActivity.getLocationName());
    }

    @Test
    void updateActivity_whenNotOwner_returnsBadRequest() throws Exception {
        createUser("testUser", "user@email.com");
        createUser("otherUser", "otheruser@email.com");
        Cookie sessionCookie = loginAndGetCookie("otheruser@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        UpdateActivityRequest request = new UpdateActivityRequest();
        request.setTitle("New activity title");
        request.setDescription("New activity description");
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setLocationName("New activity location");

        mockMvc.perform(put("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        assertNotEquals(request.getTitle(), updatedActivity.getTitle());
        assertNotEquals(request.getDescription(), updatedActivity.getDescription());
        assertNotEquals(request.getStartTime(), updatedActivity.getStartTime());
        assertNotEquals(request.getEndTime(), updatedActivity.getEndTime());
        assertNotEquals(request.getLocationName(), updatedActivity.getLocationName());
    }

    
    // DELETE /api/trips/{tripId}/days/{dayId}/activities/{activityId}

    @Test
    void deleteActivity_whenValidActivityAndOwner_activityIsDeleted() throws Exception {
        createUser("testUser", "user@email.com");
        Cookie sessionCookie = loginAndGetCookie("user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        mockMvc.perform(delete("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie))
                .andExpect(status().isNoContent());

        assertTrue(activityRepository.findAllByTripDayIdOrderByManualOrderAsc(tripDay.getId()).isEmpty());
    }

    @Test
    void deleteActivity_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        mockMvc.perform(delete("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertFalse(activityRepository.findAllByTripDayIdOrderByManualOrderAsc(tripDay.getId()).isEmpty());
    }

    @Test
    void deleteActivity_whenNotOwner_returnsBadRequest() throws Exception {
        createUser("testUser", "user@email.com");
        createUser("otherUser", "otheruser@email.com");
        Cookie sessionCookie = loginAndGetCookie("otheruser@email.com");

        Trip trip = Trip.builder()
            .user(userRepository.findByEmailIgnoreCase("user@email.com").get())
            .name("Test Trip")  
            .description("Test Description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();
        
        tripRepository.save(trip);

        TripDay tripDay = TripDay.builder()
            .trip(trip)
            .date(LocalDate.now())
            .dayNumber(1)
            .build();

        tripDayRepository.save(tripDay);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title("Activity title")
            .description("Activity description")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .locationName("Activity location")
            .build();

        activityRepository.save(activity);

        mockMvc.perform(delete("/api/trips/" + trip.getId() + "/days/" + tripDay.getId() + "/activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(sessionCookie))
                .andExpect(status().isBadRequest());

        assertFalse(activityRepository.findAllByTripDayIdOrderByManualOrderAsc(tripDay.getId()).isEmpty());
    }
}
