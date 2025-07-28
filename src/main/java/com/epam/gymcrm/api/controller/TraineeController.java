package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.*;
import com.epam.gymcrm.api.payload.response.*;
import com.epam.gymcrm.domain.service.TraineeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    public ResponseEntity<TraineeRegistrationResponse> createTrainee(@RequestBody @Valid TraineeRegistrationRequest traineeRegistrationRequest) {
        return new ResponseEntity<>(traineeService.createTrainee(traineeRegistrationRequest), HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileResponse> getTraineeByUsername(
            @RequestParam(name = "username") String username
    ) {
        //traineeService.isTraineeCredentialsValid(username, password);
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @PutMapping
    public ResponseEntity<TraineeProfileUpdateResponse> updateTrainee(@RequestBody @Valid TraineeUpdateRequest traineeUpdateRequest) {
        return new ResponseEntity<>(traineeService.update(traineeUpdateRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeByUsername(@PathVariable(name = "username") String username) {
        traineeService.deleteTraineeByUsername(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/trainers")
    public ResponseEntity<TraineeTrainerUpdateResponse> updateTraineeTrainers(
            @RequestBody @Valid TraineeTrainerUpdateRequest request
    ) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainers(request));
    }

    @GetMapping("/trainings")
    public ResponseEntity<TraineeTrainingsListResponse> getTraineeTrainings(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "periodFrom", required = false) String periodFrom,
            @RequestParam(name = "periodTo", required = false) String periodTo,
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @RequestParam(name = "trainingType", required = false) String trainingType
    ) {
        TraineeTrainingsFilter filter = new TraineeTrainingsFilter(
                username, periodFrom, periodTo, trainerName, trainingType
        );
        return ResponseEntity.ok(traineeService.getTraineeTrainings(filter));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> activateTrainee(@RequestBody @Valid UpdateActiveStatusRequest updateActiveStatusRequest) {
        traineeService.updateActivateStatus(updateActiveStatusRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unassigned-trainers")
    public ResponseEntity<UnassignedActiveTrainerListResponse> getUnassignedTrainersForTrainee(
            @RequestParam(name = "username") String username
    ) {
        return ResponseEntity.ok(traineeService.getUnassignedActiveTrainersForTrainee(username));
    }
}
