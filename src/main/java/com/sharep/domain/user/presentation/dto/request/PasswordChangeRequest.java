package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @JsonProperty("current-password") @NotBlank @Size(max = 50) String currentPassword,
        @JsonProperty("new-password") @NotBlank @Size(min = 9, max = 50)
        @Pattern(regexp = "^(?=(?:[^A-Za-z]*[A-Za-z]){8,30}[^A-Za-z]*$)(?=(?:[A-Za-z]*[^A-Za-z]){1,20}[A-Za-z]*$)[A-Za-z!@#$%^&*(),.?\":{}|<>]+$")
        String newPassword) {
}
