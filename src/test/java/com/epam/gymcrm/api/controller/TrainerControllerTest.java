package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.TrainerRegistrationRequest;
import com.epam.gymcrm.api.payload.request.UpdateTrainerProfileRequest;
import com.epam.gymcrm.api.payload.response.TrainerProfileResponse;
import com.epam.gymcrm.api.payload.response.TrainerRegistrationResponse;
import com.epam.gymcrm.api.payload.response.UpdateTrainerProfileResponse;
import com.epam.gymcrm.domain.service.TrainerService;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.GlobalExceptionHandler;
import com.epam.gymcrm.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(trainerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createTrainer_shouldReturn201AndRegistrationResponse() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Ali", "Veli", 1L);
        TrainerRegistrationResponse response = new TrainerRegistrationResponse("ali.veli", "12345");

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ali.veli"))
                .andExpect(jsonPath("$.password").value("12345"));

        verify(trainerService).createTrainer(any(TrainerRegistrationRequest.class));
    }

    @Test
    void createTrainer_shouldReturn404_whenSpecializationNotFound() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Ali", "Veli", 99L);

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class)))
                .thenThrow(new NotFoundException("Specialization (training type) not found. id=99"));

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Specialization")));
    }

    @Test
    void createTrainer_shouldReturn400_whenUserIsTrainee() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Ali", "Veli", 1L);

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class)))
                .thenThrow(new BadRequestException("User cannot be both trainer and trainee."));

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("User cannot be both trainer and trainee")));
    }

    @Test
    void getTrainerProfile_shouldReturnProfile_whenExists() throws Exception {
        TrainerProfileResponse response = new TrainerProfileResponse(
                "Ali",
                "Veli",
                1L,
                true,
                Collections.emptyList()
        );

        when(trainerService.getTrainerProfile("ali.veli")).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainers/profile")
                        .param("username", "ali.veli"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.lastName").value("Veli"))
                .andExpect(jsonPath("$.specialization").value(1L))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees.length()").value(0));
    }

    @Test
    void getTrainerProfile_shouldReturn404_whenTrainerNotFound() throws Exception {
        when(trainerService.getTrainerProfile("notfound"))
                .thenThrow(new NotFoundException("Trainer not found with username: notfound"));

        mockMvc.perform(get("/api/v1/trainers/profile")
                        .param("username", "notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Trainer not found")));
    }

    @Test
    void updateTrainerProfile_shouldReturn200AndUpdatedProfile() throws Exception {
        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setUsername("ali.veli");
        request.setFirstName("Mehmet");
        request.setLastName("Kaya");
        request.setSpecialization(1L);
        request.setActive(true);

        UpdateTrainerProfileResponse response = new UpdateTrainerProfileResponse(
                "ali.veli", "Mehmet", "Kaya", 1L, true, List.of() // trainees boş, detayını testten çıkar
        );

        when(trainerService.updateTrainerProfile(any(UpdateTrainerProfileRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/trainers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ali.veli"))
                .andExpect(jsonPath("$.firstName").value("Mehmet"))
                .andExpect(jsonPath("$.lastName").value("Kaya"))
                .andExpect(jsonPath("$.specialization").value(1L))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(trainerService).updateTrainerProfile(any(UpdateTrainerProfileRequest.class));
    }

    @Test
    void updateTrainerProfile_shouldReturn404_whenTrainerNotFound() throws Exception {
        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setUsername("notfound");
        request.setFirstName("Mehmet");
        request.setLastName("Kaya");
        request.setSpecialization(1L);
        request.setActive(true);

        when(trainerService.updateTrainerProfile(any(UpdateTrainerProfileRequest.class)))
                .thenThrow(new NotFoundException("Trainer not found with username: notfound"));

        mockMvc.perform(put("/api/v1/trainers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Trainer not found")));

        verify(trainerService).updateTrainerProfile(any(UpdateTrainerProfileRequest.class));
    }

    @Test
    void updateTrainerProfile_shouldReturn409_whenUserIsNull() throws Exception {
        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setUsername("ali.veli");
        request.setFirstName("Mehmet");
        request.setLastName("Kaya");
        request.setSpecialization(1L);
        request.setActive(true);

        when(trainerService.updateTrainerProfile(any(UpdateTrainerProfileRequest.class)))
                .thenThrow(new IllegalStateException("Trainer: User is null. Cannot update profile."));

        mockMvc.perform(put("/api/v1/trainers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User is null")));

        verify(trainerService).updateTrainerProfile(any(UpdateTrainerProfileRequest.class));
    }
}