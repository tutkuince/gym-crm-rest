package com.epam.gymcrm.api.payload.response;

import java.util.Set;

public record TraineeProfileResponse(
        String firstName,
        String lastName,
        String dateOfBirth,
        String address,
        boolean isActive,
        Set<TrainerInfoResponse> trainerInfoResponses
) {
}
