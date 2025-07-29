package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.api.payload.response.UnassignedActiveTrainerResponse;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.domain.model.Trainer;

public class TrainerDomainMapper {

    public static TrainerEntity toTrainerEntity(Trainer trainer) {
        TrainerEntity entity = new TrainerEntity();
        entity.setId(trainer.getId());
        entity.setUser(UserDomainMapper.toUserEntity(trainer.getUser()));
        entity.setTrainingType(
                trainer.getSpecialization() != null
                        ? TrainingTypeDomainMapper.toEntity(trainer.getSpecialization())
                        : null
        );
        return entity;
    }

    public static Trainer toTrainer(TrainerEntity entity) {
        Trainer trainer = new Trainer();
        trainer.setId(entity.getId());
        trainer.setUser(UserDomainMapper.toUser(entity.getUser()));
        trainer.setSpecialization(
                entity.getTrainingType() != null
                        ? TrainingTypeDomainMapper.toDomain(entity.getTrainingType())
                        : null
        );
        return trainer;
    }

    public static UnassignedActiveTrainerResponse toUnassignedActiveTrainerResponse(TrainerEntity trainerEntity) {
        return new UnassignedActiveTrainerResponse(
                trainerEntity.getUser().getUsername(),
                trainerEntity.getUser().getFirstName(),
                trainerEntity.getUser().getLastName(),
                trainerEntity.getTrainingType().getId()
        );
    }
}
