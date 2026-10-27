package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharep.domain.user.validation.ValidLoginId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginIdChangeRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 50, message = "비밀번호는 50자 이하로 입력해주세요.")
    private String password;

    @JsonProperty("new-id")
    @NotBlank(message = "새 아이디는 필수입니다.")
    @ValidLoginId
    private String newId;
}
