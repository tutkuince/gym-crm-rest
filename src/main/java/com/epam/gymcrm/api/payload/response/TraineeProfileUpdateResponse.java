package com.epam.gymcrm.api.payload.response;

import java.util.Set;

public record TraineeProfileUpdateResponse(
        String username,
        String firstName,
        String lastName,
        String dateOfBirth,
        String address,
        boolean isActive,
        Set<TraineeTrainerSummaryResponse> trainers
) {
}
