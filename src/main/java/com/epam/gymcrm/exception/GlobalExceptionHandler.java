package com.epam.gymcrm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TrainerScheduleConflictException.class)
    public ResponseEntity<ErrorResponse> handleTrainerScheduleConflict(TrainerScheduleConflictException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Schedule Conflict",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Error",
                errors,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Credentials",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Invalid Operation",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
