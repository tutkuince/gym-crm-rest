package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.payload.request.TraineeRegistrationRequest;
import com.epam.gymcrm.api.payload.request.TraineeUpdateRequest;
import com.epam.gymcrm.api.payload.response.TraineeProfileResponse;
import com.epam.gymcrm.api.payload.response.TraineeProfileUpdateResponse;
import com.epam.gymcrm.api.payload.response.TraineeRegistrationResponse;
import com.epam.gymcrm.db.entity.TraineeEntity;
import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeService traineeService;

    private TraineeEntity traineeEntity;
    private TraineeEntity savedTraineeEntity;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("ali.veli");
        userEntity.setPassword("12345");
        userEntity.setFirstName("Ali");
        userEntity.setLastName("Veli");
        userEntity.setActive(true);

        traineeEntity = new TraineeEntity();
        traineeEntity.setId(1L);
        traineeEntity.setUser(userEntity);
        traineeEntity.setDateOfBirth(LocalDate.of(1999, 1, 1));
        traineeEntity.setAddress("İstanbul");

        savedTraineeEntity = new TraineeEntity();
        savedTraineeEntity.setId(1L);
        savedTraineeEntity.setUser(userEntity);
        savedTraineeEntity.setDateOfBirth(LocalDate.of(1999, 1, 1));
        savedTraineeEntity.setAddress("İstanbul");
    }


    @Test
    void createTrainee_shouldReturnRegisterResponse_whenRequestIsValid() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "Ali", "Veli", "1999-01-01", "İstanbul"
        );

        when(traineeRepository.save(any(TraineeEntity.class)))
                .thenReturn(savedTraineeEntity);

        TraineeRegistrationResponse result = traineeService.createTrainee(request);

        assertNotNull(result);
        assertEquals("ali.veli", result.username());
        assertEquals("12345", result.password());

        verify(traineeRepository).save(any(TraineeEntity.class));
    }

    @Test
    void findByUsername_shouldReturnTraineeProfileResponse_whenTraineeExists() {
        String username = "ali.veli";
        when(traineeRepository.findByUserUsernameWithTrainers(username))
                .thenReturn(Optional.of(traineeEntity));

        TraineeProfileResponse response = traineeService.findByUsername(username);

        assertNotNull(response);
        assertEquals("Ali", response.firstName());
        assertEquals("Veli", response.lastName());

        verify(traineeRepository).findByUserUsernameWithTrainers(username);
    }

    @Test
    void findByUsername_shouldThrowNotFoundException_whenTraineeDoesNotExist() {
        String username = "nonexistent";
        when(traineeRepository.findByUserUsernameWithTrainers(username))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> traineeService.findByUsername(username));

        assertTrue(ex.getMessage().contains("No trainee found with username"));
        verify(traineeRepository).findByUserUsernameWithTrainers(username);
    }

    @Test
    void update_shouldUpdateTrainee_whenRequestIsValid() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "ali.veli", "Ali", "Veli", "1995-01-01", "Istanbul", true
        );

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("ali.veli");
        userEntity.setPassword("oldpass");
        userEntity.setFirstName("Ali");
        userEntity.setLastName("Old");
        userEntity.setActive(false);

        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setId(10L);
        traineeEntity.setUser(userEntity);

        when(traineeRepository.findByUserUsernameWithTrainers("ali.veli"))
                .thenReturn(Optional.of(traineeEntity));
        when(traineeRepository.save(any(TraineeEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TraineeProfileUpdateResponse response = traineeService.update(request);

        assertNotNull(response);
        assertEquals("ali.veli", response.username());
        assertEquals("Ali", response.firstName());
        assertEquals("Veli", response.lastName());
        assertEquals("1995-01-01", response.dateOfBirth());
        assertEquals("Istanbul", response.address());
        assertTrue(response.isActive());

        verify(traineeRepository).findByUserUsernameWithTrainers("ali.veli");
        verify(traineeRepository).save(any(TraineeEntity.class));
    }

    @Test
    void update_shouldThrowNotFoundException_whenTraineeDoesNotExist() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "notfound", "Test", "User", null, null, true
        );

        when(traineeRepository.findByUserUsernameWithTrainers("notfound"))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> traineeService.update(request));

        assertTrue(ex.getMessage().contains("Trainee to update not found"));
        verify(traineeRepository).findByUserUsernameWithTrainers("notfound");
    }

    @Test
    void update_shouldThrowIllegalStateException_whenUserEntityIsNull() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "ali.veli", "Ali", "Veli", null, null, true
        );
        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setId(1L);
        traineeEntity.setUser(null);

        when(traineeRepository.findByUserUsernameWithTrainers("ali.veli"))
                .thenReturn(Optional.of(traineeEntity));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> traineeService.update(request));

        assertTrue(ex.getMessage().contains("User entity is null"));
    }

    @Test
    void update_shouldThrowBadRequestException_whenDateOfBirthIsInvalid() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "ali.veli", "Ali", "Veli", "not-a-date", null, true
        );
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("ali.veli");
        userEntity.setPassword("oldpass");

        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setId(10L);
        traineeEntity.setUser(userEntity);

        when(traineeRepository.findByUserUsernameWithTrainers("ali.veli"))
                .thenReturn(Optional.of(traineeEntity));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> traineeService.update(request));

        assertTrue(ex.getMessage().contains("Invalid dateOfBirth format"));
    }

    @Test
    void update_shouldHandleNullOptionalFields() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "ali.veli", "Ali", "Veli", null, null, true
        );

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("ali.veli");
        userEntity.setPassword("oldpass");
        userEntity.setFirstName("Ali");
        userEntity.setLastName("Old");
        userEntity.setActive(false);

        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setId(10L);
        traineeEntity.setUser(userEntity);

        when(traineeRepository.findByUserUsernameWithTrainers("ali.veli"))
                .thenReturn(Optional.of(traineeEntity));
        when(traineeRepository.save(any(TraineeEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TraineeProfileUpdateResponse response = traineeService.update(request);

        assertNotNull(response);
        assertEquals("ali.veli", response.username());
        assertEquals("Ali", response.firstName());
        assertEquals("Veli", response.lastName());
        assertNull(response.dateOfBirth());
        assertNull(response.address());
        assertTrue(response.isActive());

        verify(traineeRepository).findByUserUsernameWithTrainers("ali.veli");
        verify(traineeRepository).save(any(TraineeEntity.class));
    }

    @Test
    void delete_shouldDeleteTrainee_whenExists() {
        String username = "ali.veli";
        TraineeEntity trainee = new TraineeEntity();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        trainee.setUser(user);

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeByUsername(username);

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenTraineeDoesNotExist() {
        String username = "unknown";
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> traineeService.deleteTraineeByUsername(username));
        assertTrue(ex.getMessage().contains("not found"));
    }

}