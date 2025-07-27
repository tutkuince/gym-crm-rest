package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.db.entity.TraineeEntity;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.domain.model.Trainee;
import com.epam.gymcrm.domain.model.Trainer;
import com.epam.gymcrm.domain.model.Training;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TrainerDomainMapper {

    public static TrainerEntity toTrainerEntity(Trainer trainer) {
        TrainerEntity entity = new TrainerEntity();
        entity.setId(trainer.getId());
        entity.setUser(UserDomainMapper.toUserEntity(trainer.getUser()));
        entity.setSpecialization(trainer.getSpecialization());

        if (Objects.nonNull(trainer.getTrainings())) {
            Set<TrainingEntity> trainingEntities = trainer.getTrainings().stream()
                    .map(TrainingDomainMapper::toTrainingEntity)
                    .collect(Collectors.toSet());
            entity.setTrainings(trainingEntities);
        }

        if (Objects.nonNull(trainer.getTrainees())) {
            Set<TraineeEntity> traineeEntities = trainer.getTrainees().stream()
                    .map(TraineeDomainMapper::toTraineeEntity)
                    .collect(Collectors.toSet());
            entity.setTrainees(traineeEntities);
        }
        return entity;
    }

    public static Trainer toTrainer(TrainerEntity trainerEntity) {
        if (trainerEntity == null) return null;

        UserEntity userEntity = trainerEntity.getUser();
        if (userEntity == null) {
            throw new IllegalStateException(
                    String.format(
                            "TrainerDomainMapper: Mapping failed for TrainerEntity (id=%d): Associated User entity is null. Data integrity violation!",
                            trainerEntity.getId()
                    )
            );
        }

        Trainer trainer = new Trainer();
        trainer.setId(trainerEntity.getId());
        trainer.setUser(UserDomainMapper.toUser(userEntity));
        trainer.setSpecialization(trainerEntity.getSpecialization());

        if (Objects.nonNull(trainerEntity.getTrainings()) && !trainerEntity.getTrainings().isEmpty()) {
            Set<Training> trainings = trainerEntity.getTrainings().stream()
                    .map(TrainingDomainMapper::toTraining)
                    .collect(Collectors.toSet());
            trainer.setTrainings(trainings);
        }

        if (Objects.nonNull(trainerEntity.getTrainees()) && !trainerEntity.getTrainees().isEmpty()) {
            Set<Trainee> trainees = trainerEntity.getTrainees().stream()
                    .map(TraineeDomainMapper::toTrainee)
                    .collect(Collectors.toSet());
            trainer.setTrainees(trainees);
        }
        return trainer;
    }

}
