package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.TraineeRegistrationRequest;
import com.epam.gymcrm.api.payload.request.TraineeUpdateRequest;
import com.epam.gymcrm.api.payload.response.TraineeProfileResponse;
import com.epam.gymcrm.api.payload.response.TraineeProfileUpdateResponse;
import com.epam.gymcrm.api.payload.response.TraineeRegistrationResponse;
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

    /*
    @GetMapping("/search")
    public ResponseEntity<TraineeDto> getTraineeByUsername(
            @RequestParam(name = "username") String uname,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        return ResponseEntity.ok(traineeService.findByUsername(uname));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changeTraineePassword(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password,
            @RequestBody @Valid PasswordChangeRequestDto passwordChangeRequest
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        traineeService.changeTraineePassword(username, password, passwordChangeRequest.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateTrainee(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        traineeService.activateTrainee(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateTrainee(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        traineeService.deactivateTrainee(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteTraineeByUsername(
            @RequestParam(name = "username") String username,
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword
    ) {
        traineeService.isTraineeCredentialsValid(authUsername, authPassword);
        traineeService.deleteTraineeByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/trainers")
    public ResponseEntity<Void> updateTraineeTrainers(
            @PathVariable("id") Long traineeId,
            @RequestBody UpdateTraineeTrainersRequest request,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        traineeService.updateTraineeTrainers(traineeId, request);
        return ResponseEntity.noContent().build();
    }*/

}
