package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.payload.request.AddTrainingRequest;
import com.epam.gymcrm.db.entity.TraineeEntity;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.TrainerRepository;
import com.epam.gymcrm.db.repository.TrainingRepository;
import com.epam.gymcrm.domain.mapper.TraineeDomainMapper;
import com.epam.gymcrm.domain.mapper.TrainerDomainMapper;
import com.epam.gymcrm.domain.mapper.TrainingDomainMapper;
import com.epam.gymcrm.domain.model.Trainee;
import com.epam.gymcrm.domain.model.Trainer;
import com.epam.gymcrm.domain.model.Training;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.TrainerScheduleConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    public TrainingService(TrainingRepository trainingRepository,
                           TrainerRepository trainerRepository,
                           TraineeRepository traineeRepository) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
    }

    @Transactional
    public void addTraining(AddTrainingRequest request) {
        logger.info("Received request to add new training: traineeUsername='{}', trainerUsername='{}', trainingName='{}'",
                request.traineeUsername(), request.trainerUsername(), request.trainingName());

        TraineeEntity traineeEntity = traineeRepository.findByUserUsername(request.traineeUsername())
                .orElseThrow(() -> {
                    logger.warn("Failed to add training: Trainee not found. traineeUsername='{}'", request.traineeUsername());
                    return new NotFoundException("Trainee not found: " + request.traineeUsername());
                });

        TrainerEntity trainerEntity = trainerRepository.findByUserUsername(request.trainerUsername())
                .orElseThrow(() -> {
                    logger.warn("Failed to add training: Trainer not found. trainerUsername='{}'", request.trainerUsername());
                    return new NotFoundException("Trainer not found: " + request.trainerUsername());
                });

        Trainee trainee = TraineeDomainMapper.toTraineeShallow(traineeEntity);
        Trainer trainer = TrainerDomainMapper.toTrainerShallow(trainerEntity);

        Training training;
        try {
            training = new Training(
                    trainee,
                    trainer,
                    request.trainingName(),
                    trainer.getSpecialization(),
                    LocalDateTime.parse(request.trainingDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    request.trainingDuration()
            );
        } catch (DateTimeParseException ex) {
            logger.error("Failed to add training: Invalid date format. value='{}', expectedFormat='yyyy-MM-dd HH:mm:ss'", request.trainingDate());
            throw new BadRequestException("Invalid trainingDate format. Must be yyyy-MM-dd HH:mm:ss");
        }

        TrainingEntity trainingEntity = TrainingDomainMapper.toTrainingEntity(training);

        checkTrainerAvailability(trainer.getId(), training.getTrainingDate());
        trainingRepository.save(trainingEntity);

        logger.info("Training added successfully: trainingId={}, trainingName='{}', traineeUsername='{}', trainerUsername='{}'",
                trainingEntity.getId(), trainingEntity.getTrainingName(), request.traineeUsername(), request.trainerUsername());
    }

    private void checkTrainerAvailability(Long trainerId, LocalDateTime trainingDate) {
        boolean isBusy = trainingRepository
                .findByTrainerIdAndTrainingDate(trainerId, trainingDate)
                .isPresent();
        if (isBusy) {
            logger.warn("Trainer {} has a schedule conflict at {}", trainerId, trainingDate);
            throw new TrainerScheduleConflictException("Trainer is already assigned to another training at the same time!");
        }
    }
}
