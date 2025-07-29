package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddTrainingRequest(
        @NotBlank(message = "Trainee username must not be blank")
        String traineeUsername,

        @NotBlank(message = "Trainer username must not be blank")
        String trainerUsername,

        @NotBlank(message = "Training name must not be blank")
        String trainingName,

        @NotBlank(message = "Training date must not be blank (yyyy-MM-dd HH:mm:ss)")
        String trainingDate,

        @NotNull(message = "Training duration must not be null")
        Integer trainingDuration
) {
}
