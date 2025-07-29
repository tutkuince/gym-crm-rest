package com.epam.gymcrm.api.payload.response;

import java.util.List;

public record TrainerProfileResponse(
        String firstName,
        String lastName,
        Long specialization,
        boolean isActive,
        List<TraineeSummaryResponse> trainees
) {
}
