package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.TraineeRegistrationRequest;
import com.epam.gymcrm.api.payload.request.TraineeUpdateRequest;
import com.epam.gymcrm.api.payload.response.TraineeProfileResponse;
import com.epam.gymcrm.api.payload.response.TraineeProfileUpdateResponse;
import com.epam.gymcrm.api.payload.response.TraineeRegistrationResponse;
import com.epam.gymcrm.domain.service.TraineeService;
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

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(traineeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createTrainee_shouldReturnCreatedStatusAndRegisterResponse() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "Ali", "Veli", "1999-01-01", "İstanbul"
        );
        TraineeRegistrationResponse response = new TraineeRegistrationResponse("ali.veli", "12345");

        when(traineeService.createTrainee(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainees") // mapping "/trainee" ise
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ali.veli"))
                .andExpect(jsonPath("$.password").value("12345"));
    }

    @Test
    void getTraineeByUsername_shouldReturnProfileResponse() throws Exception {
        String username = "ali.veli";
        TraineeProfileResponse profile = new TraineeProfileResponse(
                "Ali", "Veli", "1999-01-01", "İstanbul", true, Set.of()
        );

        when(traineeService.findByUsername(username)).thenReturn(profile);

        mockMvc.perform(get("/api/v1/trainees/profile")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.lastName").value("Veli"))
                .andExpect(jsonPath("$.dateOfBirth").value("1999-01-01"))
                .andExpect(jsonPath("$.address").value("İstanbul"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void getTraineeByUsername_shouldReturnNotFound_whenTraineeDoesNotExist() throws Exception {
        String username = "not.found";
        when(traineeService.findByUsername(username)).thenThrow(new NotFoundException("No trainee found with username: " + username));

        mockMvc.perform(get("/api/v1/trainees/profile")
                        .param("username", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("No trainee found with username")));
    }

    @Test
    void updateTrainee_shouldReturn200AndUpdatedProfile() throws Exception {
        TraineeProfileUpdateResponse response = new TraineeProfileUpdateResponse(
                "ali.veli", "Ali", "Veli", "1995-01-01", "Istanbul", true, Set.of()
        );

        when(traineeService.update(any(TraineeUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "ali.veli",
                                    "firstName": "Ali",
                                    "lastName": "Veli",
                                    "dateOfBirth": "1995-01-01",
                                    "address": "Istanbul",
                                    "isActive": true
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ali.veli"))
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.lastName").value("Veli"))
                .andExpect(jsonPath("$.dateOfBirth").value("1995-01-01"))
                .andExpect(jsonPath("$.address").value("Istanbul"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void updateTrainee_shouldReturn404_whenTraineeNotFound() throws Exception {
        when(traineeService.update(any(TraineeUpdateRequest.class)))
                .thenThrow(new NotFoundException("Trainee to update not found with Username: ali.veli"));

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "ali.veli",
                            "firstName": "Ali",
                            "lastName": "Veli",
                            "isActive": true
                        }
                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTrainee_shouldReturn400_whenBadRequest() throws Exception {
        when(traineeService.update(any(TraineeUpdateRequest.class)))
                .thenThrow(new BadRequestException("Invalid dateOfBirth format"));

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "ali.veli",
                            "firstName": "Ali",
                            "lastName": "Veli",
                            "dateOfBirth": "not-a-date",
                            "isActive": true
                        }
                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTrainee_shouldReturn200_whenDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/trainees/{username}", "ali.veli"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTrainee_shouldReturn404_whenTraineeNotFound() throws Exception {
        doThrow(new NotFoundException("not found"))
                .when(traineeService).deleteTraineeByUsername("unknown");

        mockMvc.perform(delete("/api/v1/trainees/{username}", "unknown"))
                .andExpect(status().isNotFound());
    }


}