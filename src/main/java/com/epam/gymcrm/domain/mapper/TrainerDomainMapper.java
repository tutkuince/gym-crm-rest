package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.db.entity.TraineeEntity;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.domain.model.Trainer;

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
}
