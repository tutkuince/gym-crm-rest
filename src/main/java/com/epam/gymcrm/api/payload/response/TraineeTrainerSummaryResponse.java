package com.epam.gymcrm.api.payload.response;

public record TraineeTrainerSummaryResponse(
        String username,
        String firstName,
        String lastName,
        Long specialization
) {
}
