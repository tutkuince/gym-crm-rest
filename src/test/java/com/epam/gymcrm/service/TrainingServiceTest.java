package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.TrainerScheduleConflictException;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    TrainingRepository trainingRepository;
    @Mock
    TrainerRepository trainerRepository;
    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainingTypeRepository trainingTypeRepository;
    @InjectMocks
    TrainingService trainingService;

    TrainingDto dto;
    Trainer trainer;
    Trainee trainee;
    TrainingType trainingType;
    Training training;

    @BeforeEach
    void setUp() {
        dto = new TrainingDto();
        dto.setTrainerId(1L);
        dto.setTraineeId(2L);
        dto.setTrainingTypeId(3L);
        dto.setTrainingName("Cardio");
        dto.setTrainingDate(LocalDateTime.of(2025, 7, 14, 10, 0));
        dto.setTrainingDuration(60);

        trainer = new Trainer();
        trainer.setId(1L);
        trainee = new Trainee();
        trainee.setId(2L);
        trainingType = new TrainingType();
        trainingType.setId(3L);

        training = TrainingMapper.toTraining(dto);
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
    }

    @Test
    void shouldCreateTraining() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.findAll()).thenReturn(Collections.emptyList());
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> {
            Training t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TrainingDto result = trainingService.createTraining(dto);

        assertNotNull(result);
        assertEquals("Cardio", result.getTrainingName());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void shouldThrowIfTrainerNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> trainingService.createTraining(dto));
    }

    @Test
    void shouldThrowIfTraineeNotFoundWhenCreate() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> trainingService.createTraining(dto));
    }

    @Test
    void shouldThrowIfTrainingTypeNotFoundWhenCreate() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> trainingService.createTraining(dto));
    }

    @Test
    void shouldThrowIfTrainerScheduleConflict() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(trainingType));

        Training existing = new Training();
        existing.setId(11L);
        existing.setTrainer(trainer);
        existing.setTrainingDate(dto.getTrainingDate());
        when(trainingRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(TrainerScheduleConflictException.class, () -> trainingService.createTraining(dto));
    }

    @Test
    void shouldFindById() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        TrainingDto result = trainingService.findById(1L);
        assertNotNull(result);
        assertEquals("Cardio", result.getTrainingName());
    }

    @Test
    void shouldThrowIfTrainingNotFoundById() {
        when(trainingRepository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> trainingService.findById(77L));
    }

    @Test
    void shouldFindAllTrainings() {
        Training t1 = new Training();
        t1.setId(1L);
        Training t2 = new Training();
        t2.setId(2L);
        when(trainingRepository.findAll()).thenReturn(List.of(t1, t2));
        List<TrainingDto> list = trainingService.findAll();
        assertEquals(2, list.size());
    }

    @Test
    void shouldUpdateTraining() {
        TrainingDto updateDto = new TrainingDto();
        updateDto.setId(1L);
        updateDto.setTrainingName("Updated Training");
        updateDto.setTrainerId(1L);
        updateDto.setTraineeId(2L);
        updateDto.setTrainingTypeId(3L);
        updateDto.setTrainingDate(LocalDateTime.of(2025, 7, 15, 10, 0));
        updateDto.setTrainingDuration(80);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        trainingService.update(updateDto);

        assertEquals("Updated Training", training.getTrainingName());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void shouldThrowIfTrainingNotFoundWhenUpdate() {
        TrainingDto updateDto = new TrainingDto();
        updateDto.setId(111L);
        when(trainingRepository.findById(111L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> trainingService.update(updateDto));
    }

    @Test
    void shouldThrowIfTrainerNotFoundDuringUpdate() {
        TrainingDto updateDto = new TrainingDto();
        updateDto.setId(1L);
        updateDto.setTrainerId(5L);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainerRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.update(updateDto));
    }

    @Test
    void shouldThrowIfTraineeNotFoundDuringUpdate() {
        TrainingDto updateDto = new TrainingDto();
        updateDto.setId(1L);
        updateDto.setTraineeId(22L);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(traineeRepository.findById(22L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.update(updateDto));
    }

    @Test
    void shouldThrowIfTrainingTypeNotFoundDuringUpdate() {
        TrainingDto updateDto = new TrainingDto();
        updateDto.setId(1L);
        updateDto.setTrainingTypeId(33L);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingTypeRepository.findById(33L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.update(updateDto));
    }

    @Test
    void shouldThrowIfTrainerScheduleConflictDuringUpdate() {
        TrainingDto updateDto = new TrainingDto();
        updateDto.setId(2L);
        updateDto.setTrainerId(1L);
        updateDto.setTrainingDate(LocalDateTime.of(2025, 7, 15, 10, 0));

        Training existing = new Training();
        existing.setId(2L);

        Training other = new Training();
        other.setId(3L);
        other.setTrainer(trainer);
        other.setTrainingDate(updateDto.getTrainingDate());

        when(trainingRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAll()).thenReturn(List.of(existing, other));

        assertThrows(TrainerScheduleConflictException.class, () -> trainingService.update(updateDto));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnTrainingsByCriteria() {
        // Arrange
        String traineeUsername = "testUser";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        String trainerName = "John Doe";
        String trainingType = "Cardio";

        Training t1 = new Training();
        Training t2 = new Training();
        List<Training> expectedTrainings = Arrays.asList(t1, t2);

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

        List<Training> result = trainingService.getTraineeTrainingsByCriteria(
                traineeUsername, from, to, trainerName, trainingType
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(expectedTrainings, result);

        verify(trainingRepository, times(1)).findAll(any(Specification.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnTrainerTrainingsByCriteria() {
        String trainerUsername = "jack.smith";
        LocalDate from = LocalDate.of(2024, 7, 1);
        LocalDate to = LocalDate.of(2024, 7, 25);
        String traineeName = "John";

        Training t1 = new Training();
        t1.setId(1L);
        Training t2 = new Training();
        t2.setId(2L);
        List<Training> trainings = Arrays.asList(t1, t2);

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.getTrainerTrainingsByCriteria(
                trainerUsername, from, to, traineeName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(trainingRepository, times(1)).findAll(any(Specification.class));
    }
}