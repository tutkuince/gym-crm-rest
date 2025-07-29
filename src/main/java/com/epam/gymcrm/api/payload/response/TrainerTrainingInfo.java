package com.epam.gymcrm.api.payload.response;

public record TrainerTrainingInfo(
        String trainingName,
        String trainingDate,
        String trainingType,
        Integer trainingDuration,
        String traineeName
) {
}
