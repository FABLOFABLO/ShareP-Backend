package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginIdChangeRequest(
        @NotBlank @Size(max = 50) String password,
        @JsonProperty("new-id") @NotBlank @Size(min = 5, max = 30)
        @Pattern(regexp = "^(?=(?:[^A-Za-z]*[A-Za-z]){5,20}[^A-Za-z]*$)(?=(?:[^0-9]*[0-9]){0,10}[^0-9]*$)[A-Za-z0-9]+$")
        String newId) {
}
