package com.mcl.valid;


import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 手机号校验器
 * 使用方法：
 *  @Phone(message = "手机号码不正确")
 *  private Integer phone;
 *
 * @auth caiguowei
 * @date 2020/4/8
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern regex = Pattern.compile("^(13|14|15|17|18|19)[0-9]{9}$");
    private Phone enumValid;
    @Override
    public void initialize(Phone constraintAnnotation) {
        this.enumValid = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果指定值或枚举，则默认不校验
        if (enumValid.required() && (StringUtils.isBlank(value) || value.length() != 11)){
            return false;
        }

        return regex.matcher(value).find();
    }

}
