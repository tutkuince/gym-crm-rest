package com.epam.gymcrm.api.payload.response;

public record TrainerSummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        String trainerSpecialization
) {
}
