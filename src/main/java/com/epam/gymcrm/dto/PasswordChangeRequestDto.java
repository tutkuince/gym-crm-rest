package com.epam.gymcrm.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordChangeRequestDto {

    @NotBlank(message = "newPassword is required")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
