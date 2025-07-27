package com.epam.gymcrm.api.mapper;

import com.epam.gymcrm.api.payload.response.TraineeRegisterResponse;
import com.epam.gymcrm.db.entity.TraineeEntity;

public class TraineeResponseMapper {

    public static TraineeRegisterResponse toTraineeRegisterResponse(TraineeEntity traineeEntity) {
        return new TraineeRegisterResponse(traineeEntity.getUser().getUsername(), traineeEntity.getUser().getPassword());
    }
}
