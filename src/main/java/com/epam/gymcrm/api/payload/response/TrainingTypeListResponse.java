package com.epam.gymcrm.api.payload.response;

import java.util.List;

public record TrainingTypeListResponse(
        List<TrainingTypeResponse> trainingTypes
) {
}
