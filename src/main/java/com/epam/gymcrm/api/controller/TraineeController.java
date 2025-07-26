package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.TraineeRegisterRequest;
import com.epam.gymcrm.api.payload.response.TraineeRegisterResponse;
import com.epam.gymcrm.domain.service.TraineeService;
import com.epam.gymcrm.dto.PasswordChangeRequestDto;
import com.epam.gymcrm.dto.TraineeDto;
import com.epam.gymcrm.dto.UpdateTraineeTrainersRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    public ResponseEntity<TraineeRegisterResponse> createTrainee(@RequestBody @Valid TraineeRegisterRequest traineeRegisterRequest) {
        return new ResponseEntity<>(traineeService.createTrainee(traineeRegisterRequest), HttpStatus.CREATED);
    }

    /*@GetMapping(value = "/{id}")
    public ResponseEntity<TraineeDto> getTraineeById(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        return ResponseEntity.ok(traineeService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TraineeDto>> getAllTrainees(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        return ResponseEntity.ok(traineeService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTrainee(
            @PathVariable("id") Long id,
            @RequestBody @Valid TraineeDto traineeDto,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        traineeService.isTraineeCredentialsValid(username, password);
        traineeDto.setId(id);
        traineeService.update(traineeDto);
        return ResponseEntity.noContent().build();
    }

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
