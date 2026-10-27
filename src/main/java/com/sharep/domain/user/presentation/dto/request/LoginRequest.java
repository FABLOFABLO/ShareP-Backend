package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    @Size(min = 5, max = 30)
    @JsonProperty("login_id")
    private String loginId;

    @NotBlank
    @Size(min = 8, max = 50)
    private String password;

}
