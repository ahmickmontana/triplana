package com.triplana.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.request.CreateAccommodationRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.UpdateAccommodationRequest;
import com.triplana.backend.entity.Accommodation;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.User;
import com.triplana.backend.repository.AccommodationRepository;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.service.EmailService;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AccommodationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

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

    private Trip createTrip(User user) {
        Trip trip = Trip.builder()
            .user(user)
            .name("Test Trip")
            .startDate(LocalDate.of(2027, 3, 1))
            .endDate(LocalDate.of(2027, 3, 31))
            .build();
        return tripRepository.save(trip);
    }

    private Accommodation createAccommodation(Trip trip) {
        Accommodation accommodation = Accommodation.builder()
            .trip(trip)
            .name("Test Hotel")
            .checkInDate(LocalDate.of(2027, 3, 1))
            .checkOutDate(LocalDate.of(2027, 3, 7))
            .build();
        return accommodationRepository.save(accommodation);
    }

    // POST /api/trips/{tripId}/accommodations

    @Test
    void createAccommodation_whenValidInputs_returnsCreated() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Cookie cookie = loginAndGetCookie("user@email.com");

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName("New Hotel");
        request.setCheckInDate(LocalDate.of(2027, 3, 10));
        request.setCheckOutDate(LocalDate.of(2027, 3, 15));

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/accommodations")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        assertEquals(1, accommodationRepository.findAllByTripIdOrderByCheckInDateAsc(trip.getId()).size());
    }

    @Test
    void createAccommodation_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName("New Hotel");
        request.setCheckInDate(LocalDate.of(2027, 3, 10));
        request.setCheckOutDate(LocalDate.of(2027, 3, 15));

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/accommodations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createAccommodation_whenNameEmpty_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Cookie cookie = loginAndGetCookie("user@email.com");

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName("");
        request.setCheckInDate(LocalDate.of(2027, 3, 10));
        request.setCheckOutDate(LocalDate.of(2027, 3, 15));

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/accommodations")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createAccommodation_whenDatesOutsideTripRange_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Cookie cookie = loginAndGetCookie("user@email.com");

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName("New Hotel");
        request.setCheckInDate(LocalDate.of(2027, 4, 1));
        request.setCheckOutDate(LocalDate.of(2027, 4, 5));

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/accommodations")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createAccommodation_whenDatesOverlap_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        createAccommodation(trip);
        Cookie cookie = loginAndGetCookie("user@email.com");

        CreateAccommodationRequest request = new CreateAccommodationRequest();
        request.setName("New Hotel");
        request.setCheckInDate(LocalDate.of(2027, 3, 3));
        request.setCheckOutDate(LocalDate.of(2027, 3, 10));

        mockMvc.perform(post("/api/trips/" + trip.getId() + "/accommodations")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // GET /api/trips/{tripId}/accommodations

    @Test
    void getAccommodations_whenValidTripAndOwner_returnsAccommodations() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        createAccommodation(trip);
        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(get("/api/trips/" + trip.getId() + "/accommodations")
                .cookie(cookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Test Hotel"));
    }

    @Test
    void getAccommodations_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);

        mockMvc.perform(get("/api/trips/" + trip.getId() + "/accommodations"))
            .andExpect(status().isUnauthorized());
    }

    // PUT /api/trips/{tripId}/accommodations/{accommodationId}

    @Test
    void updateAccommodation_whenValidInputs_accommodationIsUpdated() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Accommodation accommodation = createAccommodation(trip);
        Cookie cookie = loginAndGetCookie("user@email.com");

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Hotel");
        request.setCheckInDate(LocalDate.of(2027, 3, 1));
        request.setCheckOutDate(LocalDate.of(2027, 3, 7));

        mockMvc.perform(put("/api/trips/" + trip.getId() + "/accommodations/" + accommodation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        Accommodation updated = accommodationRepository.findById(accommodation.getId()).orElseThrow();
        assertEquals("Updated Hotel", updated.getName());
    }

    @Test
    void updateAccommodation_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Accommodation accommodation = createAccommodation(trip);

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Hotel");
        request.setCheckInDate(LocalDate.of(2027, 3, 1));
        request.setCheckOutDate(LocalDate.of(2027, 3, 7));

        mockMvc.perform(put("/api/trips/" + trip.getId() + "/accommodations/" + accommodation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAccommodation_whenNotOwner_returnsBadRequest() throws Exception {
        User user1 = createUser("testUser1", "user1@email.com");
        User user2 = createUser("testUser2", "user2@email.com");
        Trip trip = createTrip(user1);
        Accommodation accommodation = createAccommodation(trip);
        Cookie cookie = loginAndGetCookie("user2@email.com");

        UpdateAccommodationRequest request = new UpdateAccommodationRequest();
        request.setName("Updated Hotel");
        request.setCheckInDate(LocalDate.of(2027, 3, 1));
        request.setCheckOutDate(LocalDate.of(2027, 3, 7));

        mockMvc.perform(put("/api/trips/" + trip.getId() + "/accommodations/" + accommodation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // DELETE /api/trips/{tripId}/accommodations/{accommodationId}

    @Test
    void deleteAccommodation_whenValidAccommodationAndOwner_accommodationIsDeleted() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Accommodation accommodation = createAccommodation(trip);
        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(delete("/api/trips/" + trip.getId() + "/accommodations/" + accommodation.getId())
                .cookie(cookie))
            .andExpect(status().isNoContent());

        assertFalse(accommodationRepository.findById(accommodation.getId()).isPresent());
    }

    @Test
    void deleteAccommodation_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");
        Trip trip = createTrip(user);
        Accommodation accommodation = createAccommodation(trip);

        mockMvc.perform(delete("/api/trips/" + trip.getId() + "/accommodations/" + accommodation.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAccommodation_whenNotOwner_returnsBadRequest() throws Exception {
        User user1 = createUser("testUser1", "user1@email.com");
        User user2 = createUser("testUser2", "user2@email.com");
        Trip trip = createTrip(user1);
        Accommodation accommodation = createAccommodation(trip);
        Cookie cookie = loginAndGetCookie("user2@email.com");

        mockMvc.perform(delete("/api/trips/" + trip.getId() + "/accommodations/" + accommodation.getId())
                .cookie(cookie))
            .andExpect(status().isBadRequest());
    }
}