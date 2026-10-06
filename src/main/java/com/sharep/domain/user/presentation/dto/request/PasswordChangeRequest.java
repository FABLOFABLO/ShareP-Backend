package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.user.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {
    @JsonProperty("current-password")
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    @Size(max = 50, message = "비밀번호는 50자 이하로 입력해주세요.")
    private String currentPassword;

    @JsonProperty("new-password")
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @ValidPassword
    private String newPassword;
}
