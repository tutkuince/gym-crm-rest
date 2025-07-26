package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.domain.model.Trainee;
import com.epam.gymcrm.domain.model.Trainer;
import com.epam.gymcrm.domain.model.Training;
import com.epam.gymcrm.domain.model.TrainingType;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.TrainerScheduleConflictException;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.TrainerRepository;
import com.epam.gymcrm.db.repository.TrainingRepository;
import com.epam.gymcrm.db.repository.TrainingTypeRepository;
import com.epam.gymcrm.db.repository.specification.TrainingSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    public TrainingService(TrainingRepository trainingRepository,
                           TrainerRepository trainerRepository,
                           TraineeRepository traineeRepository,
                           TrainingTypeRepository trainingTypeRepository) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    /*@Transactional
    public TrainingDto createTraining(TrainingDto dto) {
        logger.info("Creating new training: {}", dto.getTrainingName());
        Training training = TrainingMapper.toTraining(dto);

        // Trainer
        Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                .orElseThrow(() -> {
                    logger.warn("Trainer not found for id: {}", dto.getTrainerId());
                    return new NotFoundException("Trainer not found with id: " + dto.getTrainerId());
                });

        // Trainee
        Trainee trainee = traineeRepository.findById(dto.getTraineeId())
                .orElseThrow(() -> {
                    logger.warn("Trainee not found for id: {}", dto.getTraineeId());
                    return new NotFoundException("Trainee not found with id: " + dto.getTraineeId());
                });

        // TrainingType
        TrainingType trainingType = trainingTypeRepository.findById(dto.getTrainingTypeId())
                .orElseThrow(() -> {
                    logger.warn("TrainingType not found for id: {}", dto.getTrainingTypeId());
                    return new NotFoundException("TrainingType not found with id: " + dto.getTrainingTypeId());
                });

        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);

        // Trainer schedule conflict check
        boolean isTrainerBusy = trainingRepository.findAll().stream()
                .anyMatch(t -> t.getTrainer().getId().equals(dto.getTrainerId())
                        && t.getTrainingDate().equals(training.getTrainingDate()));
        if (isTrainerBusy) {
            logger.warn("Trainer {} has a schedule conflict at {}", dto.getTrainerId(), dto.getTrainingDate());
            throw new TrainerScheduleConflictException("Trainer is already assigned to another training at the same time!");
        }

        Training savedTraining = trainingRepository.save(training);
        logger.info("Training created: id={}, name={}", savedTraining.getId(), savedTraining.getTrainingName());
        return TrainingMapper.toTrainingDto(savedTraining);
    }*/

    public TrainingDto findById(Long id) {
        logger.info("Finding training by id: {}", id);
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Training not found for id: {}", id);
                    return new NotFoundException("Training not found with id: " + id);
                });
        logger.info("Training found: id={}, name={}", training.getId(), training.getTrainingName());
        return TrainingMapper.toTrainingDto(training);
    }

    public List<TrainingDto> findAll() {
        logger.info("Retrieving all trainings");
        return trainingRepository.findAll().stream()
                .map(TrainingMapper::toTrainingDto)
                .toList();
    }

    /*@Transactional
    public void update(TrainingDto dto) {
        Long id = dto.getId();
        Training existing = trainingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Training not found for update operation with id: {}", id);
                    return new NotFoundException("Training not found for update operation with id: " + id);
                });

        // Update fields (partial update)
        if (!Objects.isNull(dto.getTrainingName())) existing.setTrainingName(dto.getTrainingName());

        // Trainer
        if (!Objects.isNull(dto.getTrainerId())) {
            Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                    .orElseThrow(() -> {
                        logger.warn("Trainer not found for update operation with id: {}", dto.getTrainerId());
                        return new NotFoundException("Trainer not found for update operation with id: " + dto.getTrainerId());
                    });
            existing.setTrainer(trainer);
        }

        // Trainee
        if (!Objects.isNull(dto.getTraineeId())) {
            Trainee trainee = traineeRepository.findById(dto.getTraineeId())
                    .orElseThrow(() -> {
                        logger.warn("Trainee not found for update operation with id: {}", dto.getTraineeId());
                        return new NotFoundException("Trainee not found for update operation with id: " + dto.getTraineeId());
                    });
            existing.setTrainee(trainee);
        }

        // TrainingType
        if (!Objects.isNull(dto.getTrainingTypeId())) {
            TrainingType trainingType = trainingTypeRepository.findById(dto.getTrainingTypeId())
                    .orElseThrow(() -> {
                        logger.warn("TrainingType not found for update operation with id: {}", dto.getTrainingTypeId());
                        return new NotFoundException("TrainingType not found for update operation with id: " + dto.getTrainingTypeId());
                    });
            existing.setTrainingType(trainingType);
        }
        if (!Objects.isNull(dto.getTrainingDate())) existing.setTrainingDate(dto.getTrainingDate());
        if (dto.getTrainingDuration() != 0) existing.setTrainingDuration(dto.getTrainingDuration());

        // Trainer schedule conflict check (update)
        if (!Objects.isNull(dto.getTrainerId()) && !Objects.isNull(dto.getTrainingDate())) {
            boolean conflict = trainingRepository.findAll().stream()
                    .anyMatch(t -> !t.getId().equals(id)
                            && t.getTrainer().getId().equals(dto.getTrainerId())
                            && t.getTrainingDate().equals(dto.getTrainingDate()));
            if (conflict) {
                logger.warn("Trainer {} has a schedule conflict at {} during update operation", dto.getTrainerId(), dto.getTrainingDate());
                throw new TrainerScheduleConflictException("Trainer is already assigned to another training at the same time during update operation!");
            }
        }

        trainingRepository.save(existing);
        logger.info("Training updated: id={}, name={}", existing.getId(), existing.getTrainingName());
    }*/

    public List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType) {

        Specification<Training> spec = TrainingSpecification.traineeUsername(traineeUsername)
                .and(TrainingSpecification.fromDate(from))
                .and(TrainingSpecification.toDate(to))
                .and(TrainingSpecification.trainerName(trainerName))
                .and(TrainingSpecification.trainingType(trainingType));

        return trainingRepository.findAll(spec);
    }

    public List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeName
    ) {
        Specification<Training> spec = TrainingSpecification.trainerUsername(trainerUsername)
                .and(TrainingSpecification.fromDate(from))
                .and(TrainingSpecification.toDate(to))
                .and(TrainingSpecification.traineeName(traineeName));
        return trainingRepository.findAll(spec);
    }

}
