package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TraineeTrainerUpdateRequest(
        @NotBlank String traineeUsername,
        @NotEmpty List<TrainerUsernameRequest> trainers
) {
}
