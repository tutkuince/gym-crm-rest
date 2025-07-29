package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.mapper.TrainerProfileResponseMapper;
import com.epam.gymcrm.api.mapper.TrainerRegistrationResponseMapper;
import com.epam.gymcrm.api.mapper.UpdateTrainerProfileResponseMapper;
import com.epam.gymcrm.api.payload.request.TrainerRegistrationRequest;
import com.epam.gymcrm.api.payload.request.UpdateTrainerProfileRequest;
import com.epam.gymcrm.api.payload.response.TrainerProfileResponse;
import com.epam.gymcrm.api.payload.response.TrainerRegistrationResponse;
import com.epam.gymcrm.api.payload.response.UpdateTrainerProfileResponse;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingTypeEntity;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.TrainerRepository;
import com.epam.gymcrm.db.repository.TrainingTypeRepository;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.domain.mapper.TrainerDomainMapper;
import com.epam.gymcrm.domain.mapper.TrainingTypeDomainMapper;
import com.epam.gymcrm.domain.model.Trainer;
import com.epam.gymcrm.domain.model.User;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainerService(TrainerRepository trainerRepository, UserRepository userRepository, TraineeRepository traineeRepository, TrainingTypeRepository trainingTypeRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional
    public TrainerRegistrationResponse createTrainer(TrainerRegistrationRequest request) {
        logger.info("Registering new trainer: {} {}", request.firstName(), request.lastName());

        TrainingTypeEntity specialization = trainingTypeRepository.findById(request.specialization())
                .orElseThrow(() -> {
                    logger.warn("Trainer registration failed: specialization not found. id={}", request.specialization());
                    return new NotFoundException("Specialization (training type) not found. id=" + request.specialization());
                });

        User user = UserUtils.createUser(request.firstName(), request.lastName(), userRepository);

        if (traineeRepository.existsByUserUsername(user.getUsername())) {
            logger.warn("Registration failed: User cannot be both trainer and trainee. username={}", user.getUsername());
            throw new BadRequestException("User cannot be both trainer and trainee.");
        }

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(TrainingTypeDomainMapper.toDomain(specialization));

        TrainerEntity trainerEntity = TrainerDomainMapper.toTrainerEntity(trainer);

        TrainerEntity saved = trainerRepository.save(trainerEntity);

        logger.info("Trainer registered successfully. id={}, username={}", saved.getId(), saved.getUser().getUsername());

        return TrainerRegistrationResponseMapper.toResponse(saved);
    }

    public TrainerProfileResponse getTrainerProfile(String username) {
        logger.info("Trainer profile request received. username={}", username);

        TrainerEntity trainerEntity = trainerRepository.findByUserUsernameWithTrainees(username)
                .orElseThrow(() -> {
                    logger.warn("Trainer not found for get trainee profile. username={}", username);
                    return new NotFoundException("Trainer not found with username: " + username);
                });

        return TrainerProfileResponseMapper.toResponse(trainerEntity);
    }

    @Transactional
    public UpdateTrainerProfileResponse updateTrainerProfile(UpdateTrainerProfileRequest request) {
        logger.info("Trainer profile update request received. username={}", request.getUsername());

        TrainerEntity trainerEntity = trainerRepository.findByUserUsernameWithTrainees(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Trainer not found for update. username={}", request.getUsername());
                    return new NotFoundException("Trainer not found with username: " + request.getUsername());
                });

        Trainer trainer = TrainerDomainMapper.toTrainer(trainerEntity);

        try {
            trainer.updateProfile(request.getFirstName(), request.getLastName(), request.getActive());
        } catch (IllegalStateException e) {
            logger.error("Failed to update trainer profile for username={}: {}", request.getUsername(), e.getMessage());
            throw e;
        }

        TrainerEntity updatedEntity = TrainerDomainMapper.toTrainerEntity(trainer);
        trainerRepository.save(updatedEntity);

        logger.info("Trainer profile updated successfully. username={}", request.getUsername());

        return UpdateTrainerProfileResponseMapper.toResponse(updatedEntity);
    }
}
