package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.TrainerRegistrationRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(trainerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createTrainer_shouldReturnCreated_whenValidRequest() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(
                "Mehmet", "Yılmaz", 2L
        );
        TrainerRegistrationResponse response = new TrainerRegistrationResponse("mehmet.yilmaz", "123456");

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("mehmet.yilmaz"))
                .andExpect(jsonPath("$.password").value("123456"));
    }

    @Test
    void createTrainer_shouldReturn404_whenSpecializationNotFound() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(
                "Mehmet", "Yılmaz", 99L // olmayan id
        );

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class)))
                .thenThrow(new NotFoundException("Specialization (training type) not found. id=99"));

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Specialization (training type) not found. id=99"));
    }

    @Test
    void createTrainer_shouldReturn400_whenUserIsAlsoTrainee() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(
                "Ali", "Veli", 1L
        );

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class)))
                .thenThrow(new BadRequestException("User cannot be both trainer and trainee."));

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User cannot be both trainer and trainee."));
    }

}