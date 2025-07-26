package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.TraineeDto;
import com.epam.gymcrm.dto.UpdateTraineeTrainersRequest;
import com.epam.gymcrm.exception.GlobalExceptionHandler;
import com.epam.gymcrm.exception.InvalidCredentialsException;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.service.TraineeService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    // Helper for adding headers
    private static final String USERNAME = "John.Doe";
    private static final String PASSWORD = "password123";

    private static final String INVALID_USERNAME = "invalid.user";
    private static final String INVALID_PASSWORD = "invalid.pass";

    // CREATE (no auth header required)
    @Test
    void shouldCreateTrainee() throws Exception {
        TraineeDto request = new TraineeDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth("1990-01-01");
        request.setAddress("Some Address");

        TraineeDto response = new TraineeDto();
        response.setId(1L);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setUsername(USERNAME);
        response.setDateOfBirth("1990-01-01");
        response.setAddress("Some Address");

        when(traineeService.createTrainee(any(TraineeDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    // GET BY ID
    @Test
    void shouldGetTraineeById() throws Exception {
        TraineeDto response = new TraineeDto();
        response.setId(2L);
        response.setFirstName("Jane");
        response.setLastName("Smith");
        response.setUsername("Jane.Smith");

        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.findById(2L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainees/2")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("Jane.Smith"));
    }

    // GET BY ID - Invalid Credentials
    @Test
    void shouldReturnUnauthorizedWhenCredentialsInvalid() throws Exception {
        when(traineeService.isTraineeCredentialsValid(INVALID_USERNAME, INVALID_PASSWORD))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(get("/api/v1/trainees/2")
                        .header("X-Username", INVALID_USERNAME)
                        .header("X-Password", INVALID_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid Credentials"));
    }

    // GET ALL
    @Test
    void shouldGetAllTrainees() throws Exception {
        TraineeDto t1 = new TraineeDto();
        t1.setId(1L);
        t1.setUsername("John.Doe");
        TraineeDto t2 = new TraineeDto();
        t2.setId(2L);
        t2.setUsername("Jane.Smith");

        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.findAll()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/v1/trainees")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // UPDATE
    @Test
    void shouldUpdateTrainee() throws Exception {
        TraineeDto request = new TraineeDto();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setDateOfBirth("1990-01-01");
        request.setAddress("Some Address");

        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(traineeService).update(any(TraineeDto.class));

        mockMvc.perform(put("/api/v1/trainees/5")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    // BAD REQUEST (Validation error, still create endpoint so no header)
    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        TraineeDto invalidRequest = new TraineeDto();
        invalidRequest.setFirstName("John"); // Missing required fields

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldGetTraineeByUsername() throws Exception {
        TraineeDto response = new TraineeDto();
        response.setId(11L);
        response.setUsername("jane.smith");
        response.setFirstName("Jane");
        response.setLastName("Smith");

        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.findByUsername("jane.smith")).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainees/search")
                        .param("username", "jane.smith")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.username").value("jane.smith"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void shouldReturnNotFoundWhenTraineeByUsernameNotExists() throws Exception {
        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.findByUsername("nouser"))
                .thenThrow(new NotFoundException("Trainee not found with username: nouser"));

        mockMvc.perform(get("/api/v1/trainees/search")
                        .param("username", "nouser")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Trainee not found with username: nouser"));
    }

    @Test
    void shouldChangePasswordWhenValidRequestWithBody() throws Exception {
        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(traineeService)
                .changeTraineePassword(USERNAME, PASSWORD, "newPass");

        // Only newPassword in body, username & oldPassword in headers
        String body = """
                {
                    "newPassword": "newPass"
                }
                """;

        mockMvc.perform(patch("/api/v1/trainees/password")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).changeTraineePassword(USERNAME, PASSWORD, "newPass");
    }

    @Test
    void shouldReturn401WhenOldPasswordIsWrong() throws Exception {
        doThrow(new InvalidCredentialsException("Old password is incorrect"))
                .when(traineeService).isTraineeCredentialsValid("test.user", "wrongOldPass");

        String body = """
                {
                    "newPassword": "newPass"
                }
                """;

        mockMvc.perform(patch("/api/v1/trainees/password")
                        .header("X-Username", "test.user")
                        .header("X-Password", "wrongOldPass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Old password is incorrect"));

        verify(traineeService).isTraineeCredentialsValid("test.user", "wrongOldPass");
        verify(traineeService, never()).changeTraineePassword(any(), any(), any());
    }

    @Test
    void shouldActivateTrainee() throws Exception {
        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(traineeService).activateTrainee(5L);

        mockMvc.perform(patch("/api/v1/trainees/5/activate")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isNoContent());

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).activateTrainee(5L);
    }

    @Test
    void shouldReturnConflictWhenAlreadyActive() throws Exception {
        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doThrow(new IllegalStateException("Trainee is already active."))
                .when(traineeService).activateTrainee(5L);

        mockMvc.perform(patch("/api/v1/trainees/5/activate")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Trainee is already active."));

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).activateTrainee(5L);
    }

    @Test
    void shouldDeactivateTrainee() throws Exception {
        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(traineeService).deactivateTrainee(10L);

        mockMvc.perform(patch("/api/v1/trainees/10/deactivate")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isNoContent());

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).deactivateTrainee(10L);
    }

    @Test
    void shouldReturnConflictWhenAlreadyInactive() throws Exception {
        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doThrow(new IllegalStateException("Trainee is already inactive."))
                .when(traineeService).deactivateTrainee(10L);

        mockMvc.perform(patch("/api/v1/trainees/10/deactivate")
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Trainee is already inactive."));

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).deactivateTrainee(10L);
    }

    @Test
    void shouldDeleteTraineeByUsername() throws Exception {
        String username = "ali.veli";

        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(traineeService).deleteTraineeByUsername(username);

        mockMvc.perform(delete("/api/v1/trainees/delete")
                        .param("username", username)
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isNoContent());

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).deleteTraineeByUsername(username);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTrainee() throws Exception {
        String username = "nouser";

        when(traineeService.isTraineeCredentialsValid(USERNAME, PASSWORD)).thenReturn(true);
        doThrow(new NotFoundException("Trainee not found with username: " + username))
                .when(traineeService).deleteTraineeByUsername(username);

        mockMvc.perform(delete("/api/v1/trainees/delete")
                        .param("username", username)
                        .header("X-Username", USERNAME)
                        .header("X-Password", PASSWORD))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found with username: " + username));

        verify(traineeService).isTraineeCredentialsValid(USERNAME, PASSWORD);
        verify(traineeService).deleteTraineeByUsername(username);
    }

    @Test
    void shouldUpdateTraineeTrainersSuccessfully() throws Exception {
        Long traineeId = 1L;
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainerIds(List.of(10L, 20L));

        when(traineeService.isTraineeCredentialsValid("test.user", "testpass")).thenReturn(true);
        doNothing().when(traineeService).updateTraineeTrainers(eq(traineeId), any(UpdateTraineeTrainersRequest.class));

        mockMvc.perform(patch("/api/v1/trainees/{id}/trainers", traineeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Username", "test.user")
                        .header("X-Password", "testpass"))
                .andExpect(status().isNoContent());

        verify(traineeService).isTraineeCredentialsValid("test.user", "testpass");
        verify(traineeService).updateTraineeTrainers(eq(traineeId), any(UpdateTraineeTrainersRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenTraineeDoesNotExist() throws Exception {
        Long traineeId = 42L;
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainerIds(List.of(999L));

        when(traineeService.isTraineeCredentialsValid("test.user", "testpass")).thenReturn(true);
        doThrow(new NotFoundException("Trainee not found")).when(traineeService)
                .updateTraineeTrainers(eq(traineeId), any(UpdateTraineeTrainersRequest.class));

        mockMvc.perform(patch("/api/v1/trainees/{id}/trainers", traineeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Username", "test.user")
                        .header("X-Password", "testpass"))
                .andExpect(status().isNotFound());

        verify(traineeService).isTraineeCredentialsValid("test.user", "testpass");
        verify(traineeService).updateTraineeTrainers(eq(traineeId), any(UpdateTraineeTrainersRequest.class));
    }

}