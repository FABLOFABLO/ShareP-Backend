package com.sharep.domain.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequest {

    private static final String SPECIALS = "!@#$%^&*(),.?\":{}|<>";

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(max = 30, message = "아이디는 30자 이하로 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 50, message = "비밀번호는 50자 이하로 입력해주세요.")
    private String password;

    @JsonIgnore
    @AssertTrue(message = "아이디는 영문 5~20자, 숫자 0~10자까지만 허용되며, 특수문자는 사용할 수 없습니다.")
    public boolean isLoginIdPolicyValid() {
        if (loginId == null) {
            return true;
        }
        if (loginId.length() < 5 || loginId.length() > 30) {
            return false;
        }

        int letters = 0;
        int digits = 0;
        for (int i = 0; i < loginId.length(); i++) {
            char c = loginId.charAt(i);
            if (isEnglishLetter(c)) {
                letters++;
            } else if (c >= '0' && c <= '9') {
                digits++;
            } else {
                return false;
            }
        }
        return letters >= 5 && letters <= 20 && digits <= 10;
    }

    @JsonIgnore
    @AssertTrue(message = "비밀번호는 영문 8~30자와 특수문자 1~20자만 사용할 수 있습니다.")
    public boolean isPasswordPolicyValid() {
        if (password == null) {
            return true;
        }
        if (password.length() < 9 || password.length() > 50) {
            return false;
        }

        int letters = 0;
        int specials = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (isEnglishLetter(c)) {
                letters++;
            } else if (SPECIALS.indexOf(c) >= 0) {
                specials++;
            } else {
                return false;
            }
        }
        return letters >= 8 && letters <= 30 && specials >= 1 && specials <= 20;
    }

    private static boolean isEnglishLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
