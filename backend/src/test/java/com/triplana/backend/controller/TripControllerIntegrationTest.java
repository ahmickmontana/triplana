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
import com.triplana.backend.dto.request.CreateTripRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.UpdateTripRequest;
import com.triplana.backend.entity.Trip;
import com.triplana.backend.entity.User;
import com.triplana.backend.repository.TripRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.service.EmailService;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TripControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

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


    // GET /api/trips

    @Test
    void getTrips_whenLoggedIn_returnsTrips() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(get("/api/trips/")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Trip name"));
    }

    @Test
    void getTrips_whenNotLoggedIn_returnsTrips() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);


        mockMvc.perform(get("/api/trips/"))
                .andExpect(status().isUnauthorized());
    }

    // POST /api/trips

    @Test
    void createTrip_whenValidInputs_tripIsCreated() throws Exception {
        User user = createUser("testUser", "user@email.com");

        CreateTripRequest request = new CreateTripRequest();
        request.setName("Trip name");
        request.setDescription("Trip description");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).plusDays(1));

        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(post("/api/trips/")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertEquals(1, tripRepository.findAllByUserId(user.getId()).size());
    }

    @Test
    void createTrip_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");

        CreateTripRequest request = new CreateTripRequest();
        request.setName("Trip name");
        request.setDescription("Trip description");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).plusDays(1));

        mockMvc.perform(post("/api/trips/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertEquals(0, tripRepository.findAllByUserId(user.getId()).size());
    }

    @Test
    void createTrip_whenNameEmpty_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");

        CreateTripRequest request = new CreateTripRequest();
        request.setName("");
        request.setDescription("Trip description");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).plusDays(1));

        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(post("/api/trips/")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, tripRepository.findAllByUserId(user.getId()).size());
    }

    @Test
    void createTrip_whenEndBeforeStart_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");

        CreateTripRequest request = new CreateTripRequest();
        request.setName("Trip name");
        request.setDescription("Trip description");
        request.setStartDate(LocalDate.now().plusMonths(1));
        request.setEndDate(LocalDate.now().plusMonths(1).minusDays(1));

        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(post("/api/trips/")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0, tripRepository.findAllByUserId(user.getId()).size());
    }

    // GET /api/trips/{id}

    @Test
    void getTrip_whenValidTripAndOwner_returnsTrip() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(get("/api/trips/" + trip.getId())
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trip name"));
    }

    @Test
    void getTrip_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        mockMvc.perform(get("/api/trips/" + trip.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTrip_whenTripNotFound_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(get("/api/trips/999")
                        .cookie(cookie))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTrip_whenNotOwner_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        createUser("testUser2", "user2@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        Cookie cookie = loginAndGetCookie("user2@email.com");

        mockMvc.perform(get("/api/trips/" + trip.getId())
                            .cookie(cookie))
                .andExpect(status().isBadRequest());
    }

    // PUT /api/trips/{id}

    @Test
    void updateTrip_whenValidInputs_tripIsUpdated() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(put("/api/trips/" + trip.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();

        assertEquals(request.getName(), updatedTrip.getName());
    }

    @Test
    void updateTrip_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        mockMvc.perform(put("/api/trips/" + trip.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();

        assertNotEquals(request.getName(), updatedTrip.getName());
    }

    @Test
    void updateTrip_whenNotOwner_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        createUser("otherUser", "user2@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        Cookie cookie = loginAndGetCookie("user2@email.com");

        mockMvc.perform(put("/api/trips/" + trip.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();

        assertNotEquals(request.getName(), updatedTrip.getName());
    }

    // DELETE /api/trips/{id}

    @Test
    void deleteTrip_whenValidTripAndOwner_tripIsDeleted() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        Cookie cookie = loginAndGetCookie("user@email.com");

        mockMvc.perform(delete("/api/trips/" + trip.getId())
                        .cookie(cookie))
                .andExpect(status().isNoContent());

        List<Trip> trips = tripRepository.findAllByUserId(user.getId());

        assertTrue(trips.isEmpty());
    }

    @Test
    void deleteTrip_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        mockMvc.perform(delete("/api/trips/" + trip.getId()))
                .andExpect(status().isUnauthorized());

        List<Trip> trips = tripRepository.findAllByUserId(user.getId());

        assertFalse(trips.isEmpty());
    }

    @Test
    void deleteTrip_whenNotOwner_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        createUser("testUser2", "user2@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        Cookie cookie = loginAndGetCookie("user2@email.com");

        mockMvc.perform(delete("/api/trips/" + trip.getId())
                        .cookie(cookie))
                .andExpect(status().isBadRequest());

        List<Trip> trips = tripRepository.findAllByUserId(user.getId());

        assertFalse(trips.isEmpty());
    }

    // POST /{id}/cover-image
    @Test
    void uploadCoverImage_whenValidImage_coverImagePathUpdated() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        Cookie cookie = loginAndGetCookie("user@email.com");

        MockMultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            new byte[100]
        );

        mockMvc.perform(multipart("/api/trips/" + trip.getId() + "/cover-image")
            .file(image)
            .cookie(cookie))
        .andExpect(status().isOk());

        assertNotNull(tripRepository.findById(trip.getId()).get().getCoverImagePath());
    }

    @Test
    void uploadCoverImage_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        User user = createUser("testUser", "user@email.com");

        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            new byte[100]
        );

        mockMvc.perform(multipart("/api/trips/" + trip.getId() + "/cover-image")
            .file(image))
        .andExpect(status().isUnauthorized());

        assertNull(tripRepository.findById(trip.getId()).get().getCoverImagePath());
    }

    @Test
    void uploadCoverImage_whenNotOwner_returnsBadRequest() throws Exception {
        User user = createUser("testUser", "user@email.com");
        createUser("otherUser", "user2@email.com");


        Trip trip = Trip.builder()
            .user(user)
            .name("Trip name")
            .description("Trip description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .build();

        tripRepository.save(trip);

        UpdateTripRequest request = new UpdateTripRequest();
        request.setName("New trip name");
        request.setDescription(trip.getDescription());
        request.setStartDate(trip.getStartDate());
        request.setEndDate(trip.getEndDate());
        
        Cookie cookie = loginAndGetCookie("user2@email.com");

        MockMultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            new byte[100]
        );

        mockMvc.perform(multipart("/api/trips/" + trip.getId() + "/cover-image")
            .file(image)
            .cookie(cookie))
        .andExpect(status().isBadRequest());

        assertNull(tripRepository.findById(trip.getId()).get().getCoverImagePath());
    }
}
