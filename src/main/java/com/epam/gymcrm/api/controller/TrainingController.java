package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.AddTrainingRequest;
import com.epam.gymcrm.domain.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<Void> createTraining(@RequestBody @Valid AddTrainingRequest request) {
        trainingService.addTraining(request);
        return ResponseEntity.ok().build();
    }


}
