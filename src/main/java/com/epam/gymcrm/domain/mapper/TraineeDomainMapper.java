package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.db.entity.TraineeEntity;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.domain.model.Trainee;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TraineeDomainMapper {

    public static TraineeEntity toTraineeEntity(Trainee trainee) {
        TraineeEntity entity = new TraineeEntity();
        entity.setId(trainee.getId());
        entity.setUser(UserDomainMapper.toUserEntity(trainee.getUser()));
        entity.setDateOfBirth(trainee.getDateOfBirth());
        entity.setAddress(trainee.getAddress());

        if (Objects.nonNull(trainee.getTrainings())) {
            Set<TrainingEntity> trainingEntities = trainee.getTrainings().stream()
                    .map(TrainingDomainMapper::toTrainingEntity)
                    .collect(Collectors.toSet());
            entity.setTrainings(trainingEntities);
        }

        if (Objects.nonNull(trainee.getTrainers())) {
            Set<TrainerEntity> trainerEntities = trainee.getTrainers().stream()
                    .map(TrainerDomainMapper::toTrainerEntity)
                    .collect(Collectors.toSet());

            entity.setTrainers(trainerEntities);
        }
        return entity;
    }
}
