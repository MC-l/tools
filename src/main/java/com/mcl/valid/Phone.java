package com.mcl.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {

    int[] value() default {};
    Class<? extends Enum> enumClass() default Enum.class;
    // 默认被注解的参数的值非必填
    boolean required() default false;

    String message() default "手机号码不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
