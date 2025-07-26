package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.domain.model.Trainee;
import com.epam.gymcrm.domain.model.Trainer;
import com.epam.gymcrm.domain.model.User;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.exception.InvalidCredentialsException;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.TrainerRepository;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    public TrainerService(TrainerRepository trainerRepository, UserRepository userRepository, TraineeRepository traineeRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
    }

    @Transactional
    public TrainerDto createTrainer(TrainerDto trainerDto) {
        logger.info("Creating new trainer: {} {}", trainerDto.getFirstName(), trainerDto.getLastName());
        Trainer trainer = TrainerMapper.toTrainer(trainerDto);

        User user = UserUtils.createUser(trainerDto.getFirstName(), trainerDto.getLastName(), userRepository);

        trainer.setUser(user);

        // Save
        Trainer savedTrainer = trainerRepository.save(trainer);

        logger.info("Trainer created: id={}, username={}", trainer.getId(), user.getUsername());
        return TrainerMapper.toTrainerDto(savedTrainer);
    }

    public TrainerDto findById(Long id) {
        logger.info("Finding trainer by id: {}", id);
        Trainer trainer = trainerRepository.findByIdWithTrainees(id)
                .orElseThrow(() -> {
                    logger.warn("Trainer not found for id: {}", id);
                    return new NotFoundException("Trainer not found with id: " + id);
                });
        logger.info("Trainer found by Id: id={}, username={}", trainer.getId(), trainer.getUser().getUsername());
        return TrainerMapper.toTrainerDto(trainer);
    }

    public List<TrainerDto> findAll() {
        logger.info("Retrieving all trainers");
        return trainerRepository.findAllWithTrainees().stream()
                .map(TrainerMapper::toTrainerDto)
                .toList();
    }

    @Transactional
    public void update(TrainerDto trainerDto) {
        Long id = trainerDto.getId();
        Trainer trainer = trainerRepository.findByIdWithTrainees(id)
                .orElseThrow(() -> {
                    logger.warn("Trainer to update not found: id={}", id);
                    return new NotFoundException("Trainer not found with id: " + id);
                });

        logger.info("Updating trainer: id={}, username={}", id, trainer.getUser().getUsername());

        String oldFirstName = trainer.getUser().getFirstName();
        String oldLastName = trainer.getUser().getLastName();

        // Update first name, last name, active status if present in DTO
        if (Objects.nonNull(trainerDto.getFirstName())) {
            trainer.getUser().setFirstName(trainerDto.getFirstName());
        }
        if (Objects.nonNull(trainerDto.getLastName())) {
            trainer.getUser().setLastName(trainerDto.getLastName());
        }
        if (Objects.nonNull(trainerDto.getActive())) {
            trainer.getUser().setActive(trainerDto.getActive());
        }

        // Update specialization if present
        if (trainerDto.getSpecialization() != null) {
            trainer.setSpecialization(trainerDto.getSpecialization());
        }

        // Check if first name or last name has changed, then update username accordingly
        boolean nameChanged =
                (Objects.nonNull(trainerDto.getFirstName()) && !Objects.equals(trainerDto.getFirstName(), oldFirstName)) ||
                        (Objects.nonNull(trainerDto.getLastName()) && !Objects.equals(trainerDto.getLastName(), oldLastName));

        if (nameChanged) {
            String newFirstName = Objects.nonNull(trainerDto.getFirstName()) ? trainerDto.getFirstName() : oldFirstName;
            String newLastName = Objects.nonNull(trainerDto.getLastName()) ? trainerDto.getLastName() : oldLastName;
            String newUsername = UserUtils.generateUniqueUsername(newFirstName, newLastName, userRepository);
            trainer.getUser().setUsername(newUsername);
        }

        Trainer updatedTrainer = trainerRepository.save(trainer);

        logger.info("Trainer updated: id={}, username={}", updatedTrainer.getId(), updatedTrainer.getUser().getUsername());
    }

    public boolean isTrainerCredentialsValid(String username, String password) {
        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login failed: No Trainer found with username '{}'. Credentials: [username='{}', password='***']", username, username);
                    return new NotFoundException("Login failed: No Trainer found with username '" + username + "'");
                });

        if (!trainer.getUser().getPassword().equals(password)) {
            logger.warn("Login failed: Invalid password for Trainer with username '{}'. Provided password: '***'", username);
            throw new InvalidCredentialsException("Login failed: Invalid password for Trainer with username '" + username + "'");
        }

        logger.info("Login success: Trainer '{}' authenticated successfully.", username);
        return Boolean.TRUE;
    }

    public TrainerDto findByUsername(String username) {
        logger.info("Finding trainer by username: {}", username);
        Trainer trainer = trainerRepository.findByUserUsernameWithTrainees(username)
                .orElseThrow(() -> {
                    logger.warn("Trainer not found with username: {}", username);
                    return new NotFoundException("Trainer not found with username: " + username);
                });
        logger.info("Trainer found by username: id={}, username={}", trainer.getId(), trainer.getUser().getUsername());
        return TrainerMapper.toTrainerDto(trainer);
    }

    @Transactional
    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        // Find trainer by username
        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Trainer not found for password change: username={}", username);
                    return new NotFoundException("Trainer not found with username: " + username);
                });

        // Check if old password matches
        if (!trainer.getUser().getPassword().equals(oldPassword)) {
            logger.warn("Invalid old password for username: {}", username);
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        // Set new password
        trainer.getUser().setPassword(newPassword);

        // Save trainer
        trainerRepository.save(trainer);
        logger.info("Password changed successfully for trainer username: {}", username);
    }

    @Transactional
    public void activateTrainer(Long id) {
        logger.info("Received request to activate trainer. id={}", id);

        Trainer trainer = trainerRepository.findByIdWithTrainees(id)
                .orElseThrow(() -> {
                    logger.warn("Cannot activate trainer. Trainer not found for activation. id={}", id);
                    return new NotFoundException("Trainer to activate not found. id=" + id);
                });

        if (Boolean.TRUE.equals(trainer.getUser().getActive())) {
            logger.warn("Trainer activation skipped. Trainer already active. id={}, username={}", id, trainer.getUser().getUsername());
            throw new IllegalStateException("Trainer is already active.");
        }

        trainer.getUser().setActive(Boolean.TRUE);
        trainerRepository.save(trainer);
        logger.info("Trainer activated successfully. id={}, username={}", id, trainer.getUser().getUsername());
    }

    @Transactional
    public void deactivateTrainer(Long id) {
        logger.info("Received request to deactivate trainer. id={}", id);

        Trainer trainer = trainerRepository.findByIdWithTrainees(id)
                .orElseThrow(() -> {
                    logger.warn("Cannot deactivate trainer. Trainer not found for deactivation. id={}", id);
                    return new NotFoundException("Trainer to deactivate not found. id=" + id);
                });

        if (Boolean.FALSE.equals(trainer.getUser().getActive())) {
            logger.warn("Trainer deactivation skipped. Trainer already inactive. id={}, username={}", id, trainer.getUser().getUsername());
            throw new IllegalStateException("Trainer is already inactive.");
        }
        trainer.getUser().setActive(Boolean.FALSE);
        trainerRepository.save(trainer);
        logger.info("Trainer deactivated successfully. id={}, username={}", id, trainer.getUser().getUsername());
    }

    /*public List<TrainerDto> getUnassignedTrainersForTrainee(String traineeUsername) {
        logger.info("Request received to get unassigned trainers for trainee: username={}", traineeUsername);

        Trainee trainee = traineeRepository.findByUserUsernameWithTrainers(traineeUsername)
                .orElseThrow(() -> {
                    logger.warn("Trainee not found when trying to get unassigned trainers. username={}", traineeUsername);
                    return new NotFoundException("Trainee not found with username: " + traineeUsername);
                });

        List<Trainer> unassigned = trainerRepository.findUnassignedTrainersForTrainee(trainee.getId());

        logger.info("Found {} unassigned trainers for trainee '{}'", unassigned.size(), traineeUsername);

        return unassigned.stream()
                .map(TrainerMapper::toTrainerDto)
                .toList();
    }*/
}
