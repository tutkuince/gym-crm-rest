package com.epam.gymcrm.api.payload.response;

import java.util.List;

public record UpdateTrainerProfileResponse(
        String username,
        String firstName,
        String lastName,
        Long specialization,
        Boolean isActive,
        List<UpdateTraineeSummaryResponse> trainees
) {
}
