package com.sharep.domain.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginIdValidator implements ConstraintValidator<ValidLoginId, String> {
    @Override
    public boolean isValid(String loginId, ConstraintValidatorContext context) {
        if (loginId == null || loginId.isBlank()) {
            return true;
        }
        if (loginId.length() < 5 || loginId.length() > 30) {
            return false;
        }

        int letters = 0;
        for (int i = 0; i < loginId.length(); i++) {
            char c = loginId.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                letters++;
            } else {
                return false;
            }
        }
        return letters >= 5 && letters <= 30;
    }
}
