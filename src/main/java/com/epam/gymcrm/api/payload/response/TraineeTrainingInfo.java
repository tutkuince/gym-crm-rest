package com.epam.gymcrm.api.payload.response;

public record TraineeTrainingInfo(
        String trainingName,
        String trainingDate,
        String trainingType,
        int trainingDuration,
        String trainerName
) {
}
