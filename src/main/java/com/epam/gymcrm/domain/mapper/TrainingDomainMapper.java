package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.domain.model.Training;

public class TrainingDomainMapper {

    public static TrainingEntity toTrainingEntity(Training training) {
        TrainingEntity entity = new TrainingEntity();
        entity.setId(training.getId());
        entity.setTrainee(TraineeDomainMapper.toTraineeEntity(training.getTrainee()));
        entity.setTrainer(TrainerDomainMapper.toTrainerEntity(training.getTrainer()));
        entity.setTrainingName(training.getTrainingName());
        entity.setTrainingDate(training.getTrainingDate());
        entity.setTrainingDuration(training.getTrainingDuration());
        return entity;
    }
}
