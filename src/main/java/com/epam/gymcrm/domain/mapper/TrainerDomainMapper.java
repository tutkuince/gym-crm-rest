package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.api.payload.response.UnassignedActiveTrainerResponse;
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
        /*entity.setSpecialization(
                trainer.getSpecialization() != null ?
                        TrainingTypeDomainMapper.toEntity(trainer.getSpecialization()) : null
        );*/
        return entity;
    }

    public static Trainer toTrainer(TrainerEntity entity) {
        Trainer trainer = new Trainer();
        trainer.setId(entity.getId());
        trainer.setUser(UserDomainMapper.toUser(entity.getUser()));
        /*trainer.setSpecialization(
                entity.getSpecialization() != null ?
                        TrainingTypeDomainMapper.toDomain(entity.getSpecialization()) : null
        );*/
        return trainer;
    }

    public static UnassignedActiveTrainerResponse toUnassignedActiveTrainerResponse(TrainerEntity trainerEntity) {
        return new UnassignedActiveTrainerResponse(
                trainerEntity.getUser().getUsername(),
                trainerEntity.getUser().getFirstName(),
                trainerEntity.getUser().getLastName(),
                trainerEntity.getSpecialization()
        );
    }
}
