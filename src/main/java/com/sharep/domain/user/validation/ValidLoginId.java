package com.sharep.domain.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginIdValidator.class)
public @interface ValidLoginId {
    String message() default "영숫자 5~30자";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
