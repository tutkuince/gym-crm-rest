package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.TrainerRegistrationRequest;
import com.epam.gymcrm.api.payload.response.TrainerProfileResponse;
import com.epam.gymcrm.api.payload.response.TrainerRegistrationResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Specialization")));
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
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User cannot be both trainer and trainee")));
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
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Trainer not found")));
    }

}