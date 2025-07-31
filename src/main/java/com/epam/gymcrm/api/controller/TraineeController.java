package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.*;
import com.epam.gymcrm.api.payload.response.*;
import com.epam.gymcrm.domain.service.TraineeService;
import com.epam.gymcrm.exception.UnauthorizedException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.epam.gymcrm.api.auth.AuthSessionManager.isLoggedInd;
import static com.epam.gymcrm.api.auth.AuthSessionManager.logout;

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
    public ResponseEntity<TraineeProfileResponse> getTraineeByUsername(@RequestParam(name = "username") String username) {
        // Check if the user is authenticated
        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        TraineeProfileResponse traineeProfileResponse = traineeService.findByUsername(username);

        // Logout the user immediately after the successful change.
        logout(username);

        return ResponseEntity.ok(traineeProfileResponse);
    }

    @PutMapping
    public ResponseEntity<TraineeProfileUpdateResponse> updateTrainee(@RequestBody @Valid TraineeUpdateRequest traineeUpdateRequest) {
        String username = traineeUpdateRequest.username();

        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        TraineeProfileUpdateResponse response = traineeService.update(traineeUpdateRequest);

        logout(username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeByUsername(@PathVariable(name = "username") String username) {
        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        traineeService.deleteTraineeByUsername(username);

        logout(username);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/trainers")
    public ResponseEntity<TraineeTrainerUpdateResponse> updateTraineeTrainers(@RequestBody @Valid TraineeTrainerUpdateRequest request) {
        String username = request.traineeUsername();
        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        TraineeTrainerUpdateResponse response = traineeService.updateTraineeTrainers(request);

        logout(username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/trainings")
    public ResponseEntity<TraineeTrainingsListResponse> getTraineeTrainings(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "periodFrom", required = false) String periodFrom,
            @RequestParam(name = "periodTo", required = false) String periodTo,
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @RequestParam(name = "trainingType", required = false) String trainingType
    ) {
        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        TraineeTrainingsFilter filter = new TraineeTrainingsFilter(
                username, periodFrom, periodTo, trainerName, trainingType
        );
        TraineeTrainingsListResponse response = traineeService.getTraineeTrainings(filter);

        logout(username);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> activateTrainee(@RequestBody @Valid UpdateActiveStatusRequest updateActiveStatusRequest) {
        String username = updateActiveStatusRequest.username();

        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        traineeService.updateActivateStatus(updateActiveStatusRequest);

        logout(username);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/unassigned-trainers")
    public ResponseEntity<UnassignedActiveTrainerListResponse> getUnassignedTrainersForTrainee(@RequestParam(name = "username") String username) {
        if (!isLoggedInd(username)) {
            // If not logged in, throw an error.
            throw new UnauthorizedException("Trainee not authenticated, please login first!");
        }

        UnassignedActiveTrainerListResponse response = traineeService.getUnassignedActiveTrainersForTrainee(username);

        logout(username);

        return ResponseEntity.ok(response);
    }
}
