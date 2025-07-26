package com.epam.gymcrm.api.mapper;

import com.epam.gymcrm.api.payload.response.TraineeRegisterResponse;
import com.epam.gymcrm.db.entity.TraineeEntity;

public class TraineeResponseMapper {

    public static TraineeRegisterResponse toTraineeRegisterResponse(TraineeEntity traineeEntity) {
        TraineeRegisterResponse response = new TraineeRegisterResponse();
        response.setUsername(traineeEntity.getUser().getUsername());
        response.setPassword(traineeEntity.getUser().getPassword());
        return response;
    }
}
