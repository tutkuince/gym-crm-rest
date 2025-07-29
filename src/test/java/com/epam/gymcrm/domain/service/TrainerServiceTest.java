package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.payload.request.TrainerRegistrationRequest;
import com.epam.gymcrm.api.payload.response.TrainerProfileResponse;
import com.epam.gymcrm.api.payload.response.TrainerRegistrationResponse;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingTypeEntity;
import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.TrainerRepository;
import com.epam.gymcrm.db.repository.TrainingTypeRepository;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainerService trainerService;

    private TrainerEntity trainerEntity;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("ali.veli");
        userEntity.setPassword("12345");
        userEntity.setFirstName("Ali");
        userEntity.setLastName("Veli");
        userEntity.setActive(true);

        TrainingTypeEntity specialization = new TrainingTypeEntity();
        specialization.setId(1L);
        specialization.setTrainingTypeName("Fitness");

        trainerEntity = new TrainerEntity();
        trainerEntity.setId(10L);
        trainerEntity.setUser(userEntity);
        trainerEntity.setTrainingType(specialization);
        trainerEntity.setTrainees(Collections.emptySet());
    }

    @Test
    void createTrainer_shouldRegisterTrainer_whenValidRequest() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Ali", "Veli", 1L);

        TrainingTypeEntity specialization = new TrainingTypeEntity();
        specialization.setId(1L);
        specialization.setTrainingTypeName("Fitness");

        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(traineeRepository.existsByUserUsername("ali.veli")).thenReturn(false);

        TrainerEntity savedEntity = new TrainerEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("ali.veli");
        userEntity.setPassword("12345");
        savedEntity.setUser(userEntity);

        when(trainerRepository.save(any())).thenReturn(savedEntity);

        TrainerRegistrationResponse response = trainerService.createTrainer(request);

        assertNotNull(response);
        assertEquals("ali.veli", response.username());
        assertEquals("12345", response.password());

        verify(trainingTypeRepository).findById(1L);
        verify(traineeRepository).existsByUserUsername("ali.veli");
        verify(trainerRepository).save(any());
    }

    @Test
    void createTrainer_shouldThrowNotFoundException_whenSpecializationNotFound() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Ali", "Veli", 99L);

        when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> trainerService.createTrainer(request));

        assertTrue(ex.getMessage().contains("Specialization (training type) not found"));
        verify(trainingTypeRepository).findById(99L);
        verifyNoInteractions(trainerRepository);
    }

    @Test
    void createTrainer_shouldThrowBadRequest_whenUserIsTrainee() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Ali", "Veli", 1L);

        TrainingTypeEntity specialization = new TrainingTypeEntity();
        specialization.setId(1L);
        specialization.setTrainingTypeName("Fitness");
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(traineeRepository.existsByUserUsername("ali.veli")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> trainerService.createTrainer(request));

        assertTrue(ex.getMessage().contains("User cannot be both trainer and trainee"));
        verify(traineeRepository).existsByUserUsername("ali.veli");
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void getTrainerProfile_shouldReturnProfile_whenTrainerExists() {
        when(trainerRepository.findByUserUsernameWithTrainees("ali.veli"))
                .thenReturn(Optional.of(trainerEntity));

        TrainerProfileResponse response = trainerService.getTrainerProfile("ali.veli");

        assertNotNull(response);
        assertEquals("Ali", response.firstName());
        assertEquals("Veli", response.lastName());
        assertEquals(1L, response.specialization());
        assertTrue(response.isActive());
        assertTrue(response.trainees().isEmpty());

        verify(trainerRepository).findByUserUsernameWithTrainees("ali.veli");
    }

    @Test
    void getTrainerProfile_shouldThrowNotFoundException_whenTrainerNotFound() {
        when(trainerRepository.findByUserUsernameWithTrainees("nonexistent"))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                trainerService.getTrainerProfile("nonexistent")
        );
        assertTrue(ex.getMessage().contains("Trainer not found with username"));

        verify(trainerRepository).findByUserUsernameWithTrainees("nonexistent");
    }
}