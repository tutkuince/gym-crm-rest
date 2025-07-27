package com.epam.gymcrm.api.mapper;

import com.epam.gymcrm.api.payload.response.TrainerSummaryResponse;
import com.epam.gymcrm.db.entity.TrainerEntity;
import com.epam.gymcrm.db.entity.UserEntity;

public class TrainerSummaryMapper {

    public static TrainerSummaryResponse toTrainerSummaryResponse(TrainerEntity trainerEntity) {
        UserEntity user = trainerEntity.getUser();
        if (user == null) {
            throw new IllegalStateException(
                    String.format("TrainerSummaryMapper: Mapping failed for TrainerEntity (id=%d): Associated User entity is null!", trainerEntity.getId())
            );
        }
        return new TrainerSummaryResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                trainerEntity.getSpecialization()
        );
    }
}
