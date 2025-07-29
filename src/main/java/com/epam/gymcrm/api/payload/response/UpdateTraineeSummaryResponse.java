package com.epam.gymcrm.api.payload.response;

public record UpdateTraineeSummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Long trainerSpecialization
) {
}
