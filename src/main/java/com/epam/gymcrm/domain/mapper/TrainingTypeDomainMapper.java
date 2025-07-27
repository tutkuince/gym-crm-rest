package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.db.entity.TrainingTypeEntity;
import com.epam.gymcrm.domain.model.TrainingType;

public class TrainingTypeDomainMapper {

    public static TrainingTypeEntity toTrainingTypeEntity(TrainingType trainingType) {
        TrainingTypeEntity entity = new TrainingTypeEntity();
        entity.setId(trainingType.getId());
        entity.setTrainingTypeName(trainingType.getTrainingTypeName());
        return entity;
    }

    public static TrainingType toTrainingType(TrainingTypeEntity entity) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(entity.getId());
        trainingType.setTrainingTypeName(entity.getTrainingTypeName());
        return trainingType;
    }
}
