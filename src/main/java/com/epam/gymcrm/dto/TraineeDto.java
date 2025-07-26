package com.epam.gymcrm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class TraineeDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dateOfBirth;

    private String address;

    // Many-to-Many Trainer
    private List<Long> trainerIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "First name is required") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "First name is required") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "Last name is required") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "Last name is required") String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Long> getTrainerIds() {
        return trainerIds;
    }

    public void setTrainerIds(List<Long> trainerIds) {
        this.trainerIds = trainerIds;
    }
}
