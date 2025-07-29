package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.response.TrainingTypeListResponse;
import com.epam.gymcrm.domain.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    public ResponseEntity<TrainingTypeListResponse> getAll() {
        return ResponseEntity.ok(trainingTypeService.findAllTrainingTypes());
    }

}
