package com.sharep.domain.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    @Size(max = 30)
    private String loginId;

    @NotBlank
    @Size(max = 50)
    private String password;

}
