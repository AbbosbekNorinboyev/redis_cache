package uz.brb.redis_cache.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExpiryDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiryDate {

    String message() default "Invalid date format. Use yyMM";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}