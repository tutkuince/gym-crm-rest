package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<TrainingDto> createTraining(@RequestBody @Valid TrainingDto dto) {
        return new ResponseEntity<>(trainingService.createTraining(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingDto> getTrainingById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(trainingService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TrainingDto>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTraining(@PathVariable("id") Long id, @RequestBody @Valid TrainingDto dto) {
        dto.setId(id);
        trainingService.update(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trainee/search")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @RequestParam(name = "trainingType", required = false) String trainingType) {

        List<Training> trainings = trainingService.getTraineeTrainingsByCriteria(
                username, from, to, trainerName, trainingType
        );

        List<TrainingDto> result = trainings.stream()
                .map(TrainingMapper::toTrainingDto)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trainer/search")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "traineeName", required = false) String traineeName
    ) {
        List<Training> trainings = trainingService.getTrainerTrainingsByCriteria(
                username, from, to, traineeName
        );
        List<TrainingDto> result = trainings.stream()
                .map(TrainingMapper::toTrainingDto)
                .toList();
        return ResponseEntity.ok(result);
    }

}
