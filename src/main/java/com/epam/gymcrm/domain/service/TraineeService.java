package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.mapper.*;
import com.epam.gymcrm.api.payload.request.*;
import com.epam.gymcrm.api.payload.response.*;
import com.epam.gymcrm.db.entity.TraineeEntity;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.db.repository.TraineeRepository;
import com.epam.gymcrm.db.repository.TrainerRepository;
import com.epam.gymcrm.db.repository.TrainingRepository;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.db.repository.specification.TrainingSpecification;
import com.epam.gymcrm.domain.mapper.TraineeDomainMapper;
import com.epam.gymcrm.domain.mapper.TrainerDomainMapper;
import com.epam.gymcrm.domain.model.Trainee;
import com.epam.gymcrm.domain.model.Trainer;
import com.epam.gymcrm.domain.model.User;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.epam.gymcrm.util.DateConstants.DEFAULT_DATE_FORMATTER;

@Service
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    public TraineeService(
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            UserRepository userRepository,
            TrainingRepository trainingRepository,
            TrainingRepository trainingRepository1) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository1;
    }

    @Transactional
    public TraineeRegistrationResponse createTrainee(TraineeRegistrationRequest traineeRegistrationRequest) {
        logger.info("Creating new trainee: {} {}", traineeRegistrationRequest.firstName(), traineeRegistrationRequest.lastName());

        User user = UserUtils.createUser(traineeRegistrationRequest.firstName(), traineeRegistrationRequest.lastName(), userRepository);

        Trainee trainee = new Trainee();
        String dateOfBirth = traineeRegistrationRequest.dateOfBirth();
        String address = traineeRegistrationRequest.address();
        trainee.setUser(user);

        if (Objects.nonNull(dateOfBirth) && !dateOfBirth.isBlank()) {
            trainee.setDateOfBirth(LocalDate.parse(dateOfBirth, DEFAULT_DATE_FORMATTER));
        }

        if (Objects.nonNull(address) && !address.isBlank()) {
            trainee.setAddress(address);
        }

        TraineeEntity traineeEntity = TraineeDomainMapper.toTraineeEntity(trainee);

        TraineeEntity savedTraineeEntity = traineeRepository.save(traineeEntity);


        logger.info("Trainee created: id={}, username={}", savedTraineeEntity.getId(), savedTraineeEntity.getUser().getUsername());
        return TraineeResponseMapper.toTraineeRegisterResponse(savedTraineeEntity);
    }

    public TraineeProfileResponse findByUsername(String username) {
        logger.info("Request to find trainee by username received. Username: {}", username);
        TraineeEntity traineeEntity = traineeRepository.findByUserUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    logger.warn("Find trainee by username failed: No trainee found with username: {}", username);
                    return new NotFoundException("Find trainee by username failed: No trainee found with username: " + username);
                });
        logger.info("Trainee found successfully. id={}, username={}", traineeEntity.getId(), traineeEntity.getUser().getUsername());
        return TraineeProfileMapper.toTraineeProfileResponse(traineeEntity);
    }

    @Transactional
    public TraineeProfileUpdateResponse update(TraineeUpdateRequest traineeUpdateRequest) {
        String username = traineeUpdateRequest.username();
        logger.info("Update requested for trainee. Username: {}", username);

        TraineeEntity traineeEntity = traineeRepository.findByUserUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    logger.warn("Update failed: Trainee not found. Username: {}", username);
                    return new NotFoundException("Trainee to update not found with Username: " + username);
                });

        UserEntity userEntity = traineeEntity.getUser();
        if (Objects.isNull(userEntity)) {
            throw new IllegalStateException(
                    String.format("User entity is null while updating Trainee (username=%s). Data integrity violation during update!", username)
            );
        }

        Trainee trainee = TraineeDomainMapper.toTrainee(traineeEntity);
        User oldUser = trainee.getUser();

        User updatedUser = new User();
        updatedUser.setId(oldUser.getId());
        updatedUser.setUsername(oldUser.getUsername());
        updatedUser.setPassword(oldUser.getPassword());

        // Mandatory Fields
        updatedUser.setFirstName(traineeUpdateRequest.firstName());
        updatedUser.setLastName(traineeUpdateRequest.lastName());
        updatedUser.setActive(traineeUpdateRequest.isActive());

        trainee.setUser(updatedUser);


        // Optional Fields
        if (Objects.nonNull(traineeUpdateRequest.dateOfBirth()) && !traineeUpdateRequest.dateOfBirth().isBlank()) {
            try {
                trainee.setDateOfBirth(LocalDate.parse(traineeUpdateRequest.dateOfBirth(), DEFAULT_DATE_FORMATTER));
            } catch (Exception e) {
                logger.warn("Invalid dateOfBirth provided during updateTraineeProfile: '{}'", traineeUpdateRequest.dateOfBirth());
                throw new BadRequestException(
                        String.format("Invalid dateOfBirth format: %s. Expected format: yyyy-MM-dd", traineeUpdateRequest.dateOfBirth())
                );
            }
        }

        if (Objects.nonNull(traineeUpdateRequest.address()) && !traineeUpdateRequest.address().isBlank()) {
            trainee.setAddress(traineeUpdateRequest.address());
        }

        TraineeEntity updatedTraineeEntity = TraineeDomainMapper.toTraineeEntity(trainee);
        updatedTraineeEntity.setId(traineeEntity.getId());
        updatedTraineeEntity.setTrainers(traineeEntity.getTrainers());
        updatedTraineeEntity.setTrainings(traineeEntity.getTrainings());

        // Save
        TraineeEntity saved = traineeRepository.save(updatedTraineeEntity);


        logger.info("Trainee profile updated successfully. ID: {}, Username: {}", saved.getId(), saved.getUser().getUsername());
        return TraineeProfileUpdateMapper.toTraineeProfileUpdateResponse(saved);
    }

    @Transactional
    public void deleteTraineeByUsername(String username) {
        logger.info("Delete request received for trainee. username={}", username);
        TraineeEntity traineeEntity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Trainee not found for deletion. username={}", username);
                    return new NotFoundException("Trainee not found with username: " + username);
                });
        traineeRepository.delete(traineeEntity);
        logger.info("Trainee deleted successfully. username={}", username);
    }

    @Transactional
    public TraineeTrainerUpdateResponse updateTraineeTrainers(TraineeTrainerUpdateRequest request) {
        String traineeUsername = request.traineeUsername();
        logger.info("Updating trainers for trainee: id={}, newTrainers={}", traineeUsername, request.trainers());

        TraineeEntity traineeEntity = traineeRepository.findByUserUsernameWithTrainers(traineeUsername)
                .orElseThrow(() -> {
                    logger.warn("Trainee to update trainers not found: username={}", traineeUsername);
                    return new NotFoundException("Trainee not found with username: " + traineeUsername);
                });

        Trainee trainee = TraineeDomainMapper.toTrainee(traineeEntity);

        List<String> trainerUsernames = request.trainers().stream()
                .map(TrainerUsernameRequest::trainerUsername)
                .toList();

        List<TrainerEntity> trainerEntities = trainerUsernames.isEmpty()
                ? List.of()
                : trainerRepository.findAllByUserUsernameIn(trainerUsernames);

        Set<Trainer> domainTrainers = trainerEntities.stream()
                .map(TrainerDomainMapper::toTrainer)
                .collect(Collectors.toSet());
        trainee.setTrainers(domainTrainers);

        TraineeEntity updatedEntity = TraineeDomainMapper.toTraineeEntity(trainee);
        updatedEntity.setId(traineeEntity.getId());
        updatedEntity.setTrainings(traineeEntity.getTrainings());

        TraineeEntity saved = traineeRepository.save(updatedEntity);

        logger.info("Updated trainers for trainee: id={}", saved.getId());

        return TraineeTrainerUpdateMapper.toResponse(trainerEntities);
    }

    public TraineeTrainingsListResponse getTraineeTrainings(TraineeTrainingsFilter filter) {
        logger.info("Trainee trainings requested. username={}, periodFrom={}, periodTo={}, trainerName={}, trainingType={}",
                filter.username(), filter.periodFrom(), filter.periodTo(), filter.trainerName(), filter.trainingType());

        traineeRepository.findByUserUsernameWithTrainers(filter.username())
                .orElseThrow(() -> {
                    logger.warn("Trainee not found while fetching trainings: username={}", filter.username());
                    return new NotFoundException("Trainee not found: " + filter.username());
                });

        LocalDate from = null, to = null;
        if (Objects.nonNull(filter.periodFrom()) && !filter.periodFrom().isBlank()) {
            logger.debug("Filtering from date: {}", filter.periodFrom());
            from = LocalDate.parse(filter.periodFrom(), DEFAULT_DATE_FORMATTER);
        }
        if (Objects.nonNull(filter.periodTo()) && !filter.periodTo().isBlank()) {
            logger.debug("Filtering to date: {}", filter.periodTo());
            to = LocalDate.parse(filter.periodTo(), DEFAULT_DATE_FORMATTER);
        }

        Specification<TrainingEntity> specification = TrainingSpecification.traineeUsername(filter.username())
                .and(TrainingSpecification.fromDate(from))
                .and(TrainingSpecification.toDate(to))
                .and(TrainingSpecification.trainerName(filter.trainerName()))
                .and(TrainingSpecification.trainingType(filter.trainingType()));

        List<TrainingEntity> trainings = trainingRepository.findAll(specification);

        logger.info("Trainee trainings fetch completed. username={}, trainingsCount={}", filter.username(), trainings.size());

        return TraineeTrainingsListResponseMapper.toTraineeTrainingsListResponse(trainings);
    }

    /*public TraineeDto findByUsername(String username) {
        logger.info("Request to find trainee by username received. Username: {}", username);
        Trainee trainee = traineeRepository.findByUserUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    logger.warn("Find trainee by username failed: No trainee found with username: {}", username);
                    return new NotFoundException("Find trainee by username failed: No trainee found with username: " + username);
                });
        logger.info("Trainee found successfully. id={}, username={}", trainee.getId(), trainee.getUser().getUsername());
        return TraineeMapper.toTraineeDto(trainee);
    }*/

    /*

    public boolean isTraineeCredentialsValid(String username, String password) {
        logger.info("Login attempt for trainee. Username: {}", username);
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login failed: No Trainee found with username '{}'. Credentials: [username='{}', password='***']", username, username);
                    return new NotFoundException("Login failed: No Trainee found with username '" + username + "'");
                });

        if (!trainee.getUser().getPassword().equals(password)) {
            logger.warn("Login failed: Invalid password for Trainee with username '{}'. Provided password: '***'", username);
            throw new InvalidCredentialsException("Login failed: Invalid password for Trainee with username '" + username + "'");
        }

        logger.info("Login success: Trainee '{}' authenticated successfully.", username);
        return Boolean.TRUE;
    }

    public TraineeDto findByUsername(String username) {
        logger.info("Request to find trainee by username received. Username: {}", username);
        Trainee trainee = traineeRepository.findByUserUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    logger.warn("Find trainee by username failed: No trainee found with username: {}", username);
                    return new NotFoundException("Find trainee by username failed: No trainee found with username: " + username);
                });
        logger.info("Trainee found successfully. id={}, username={}", trainee.getId(), trainee.getUser().getUsername());
        return TraineeMapper.toTraineeDto(trainee);
    }

    @Transactional
    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        logger.info("Request to change password received for trainee. Username: {}", username);

        // Find trainee by username
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Password change failed: Trainee not found. Username: {}", username);
                    return new NotFoundException("Password change failed: Trainee not found with username: " + username);
                });

        // Check if old password matches
        if (!trainee.getUser().getPassword().equals(oldPassword)) {
            logger.warn("Password change failed: Invalid old password provided. Username: {}", username);
            throw new InvalidCredentialsException("Password change failed: Invalid old password for trainee with username: " + username);
        }

        // Set new password
        trainee.getUser().setPassword(newPassword);

        // Save trainee
        traineeRepository.save(trainee);
        logger.info("Password changed successfully for trainee. Username: {}", username);
    }

    @Transactional
    public void activateTrainee(Long id) {
        logger.info("Received request to activate trainee. id={}", id);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cannot activate trainee. Trainee not found for activation. id={}", id);
                    return new NotFoundException("Trainee to activate not found. id=" + id);
                });

        if (Boolean.TRUE.equals(trainee.getUser().getActive())) {
            logger.warn("Trainee activation skipped. Trainee already active. id={}, username={}", id, trainee.getUser().getUsername());
            throw new IllegalStateException("Trainee is already active.");
        }

        trainee.getUser().setActive(Boolean.TRUE);
        traineeRepository.save(trainee);
        logger.info("Trainee activated successfully. id={}, username={}", id, trainee.getUser().getUsername());
    }

    @Transactional
    public void deactivateTrainee(Long id) {
        logger.info("Received request to deactivate trainee. id={}", id);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cannot deactivate trainee. Trainee not found for deactivation. id={}", id);
                    return new NotFoundException("Trainee to deactivate not found. id=" + id);
                });

        if (Boolean.FALSE.equals(trainee.getUser().getActive())) {
            logger.warn("Trainee deactivation skipped. Trainee already inactive. id={}, username={}", id, trainee.getUser().getUsername());
            throw new IllegalStateException("Trainee is already inactive.");
        }
        trainee.getUser().setActive(Boolean.FALSE);
        traineeRepository.save(trainee);
        logger.info("Trainee deactivated successfully. id={}, username={}", id, trainee.getUser().getUsername());
    }

    @Transactional
    public void deleteTraineeByUsername(String username) {
        logger.info("Delete request received for trainee. username={}", username);
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Trainee not found for deletion. username={}", username);
                    return new NotFoundException("Trainee not found with username: " + username);
                });
        traineeRepository.delete(trainee);
        logger.info("Trainee deleted successfully. username={}", username);
    }

    */
}
