package com.epam.gymcrm.api.payload.request;

public record TraineeTrainingsFilter(
        String username,
        String periodFrom,
        String periodTo,
        String trainerName,
        String trainingType
) {
}
