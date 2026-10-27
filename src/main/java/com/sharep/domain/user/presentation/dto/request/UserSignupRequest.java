package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.user.validation.ValidLoginId;
import com.sharep.domain.user.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    @ValidLoginId
    @JsonProperty("login_id")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @ValidPassword
    private String password;
}