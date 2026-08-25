package com.sharep.domain.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(max = 30, message = "아이디는 30자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=(?:.*[A-Za-z]){5})(?!(?:.*[A-Za-z]){21})(?!(?:.*[0-9]){11})[A-Za-z0-9]{5,30}$",
            message = "아이디는 영문 5~20자, 숫자 0~10자만 사용할 수 있습니다."
    )
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 50, message = "비밀번호는 50자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=(?:.*[A-Za-z]){8})(?!(?:.*[A-Za-z]){31})(?=(?:.*[!@#$%^&*(),.?\":{}|<>]))(?!(?:.*[!@#$%^&*(),.?\":{}|<>]){21})[A-Za-z!@#$%^&*(),.?\":{}|<>]+$",
            message = "비밀번호는 영문 8~30자와 특수문자 1~20자만 사용할 수 있습니다."
    )
    private String password;
}