package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.dto.PasswordChangeRequestDto;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<TrainerDto> createTrainer(@RequestBody @Valid TrainerDto trainerDto) {
        return new ResponseEntity<>(trainerService.createTrainer(trainerDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerDto> getTrainerById(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        return ResponseEntity.ok(trainerService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TrainerDto>> getAllTrainers(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        return ResponseEntity.ok(trainerService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTrainer(
            @PathVariable("id") Long id,
            @RequestBody @Valid TrainerDto trainerDto,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        trainerDto.setId(id);
        trainerService.update(trainerDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<TrainerDto> getTrainerByUsername(
            @RequestParam(name = "username") String uname,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        return ResponseEntity.ok(trainerService.findByUsername(uname));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changeTrainerPassword(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password,
            @RequestBody PasswordChangeRequestDto passwordChangeRequest
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        trainerService.changeTrainerPassword(username, password, passwordChangeRequest.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateTrainee(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        trainerService.activateTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateTrainee(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        trainerService.deactivateTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<TrainerDto>> getUnassignedTrainersForTrainee(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password,
            @RequestParam("traineeUsername") String traineeUsername
    ) {
        trainerService.isTrainerCredentialsValid(username, password);
        List<TrainerDto> trainers = trainerService.getUnassignedTrainersForTrainee(traineeUsername);
        return ResponseEntity.ok(trainers);
    }
}
