package com.sharep.domain.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String SPECIALS = "!@#$%^&*(),.?\":{}|<>";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true;
        }
        if (password.length() < 9 || password.length() > 50) {
            return false;
        }

        int letters = 0;
        int specials = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                letters++;
            } else if (SPECIALS.indexOf(c) >= 0) {
                specials++;
            } else {
                return false;
            }
        }
        return letters >= 8 && letters <= 30 && specials >= 1 && specials <= 20;
    }
}
