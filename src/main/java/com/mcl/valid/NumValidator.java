package com.mcl.valid;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * 数值类型参数校验器
 * 使用方法：
 *  @Nums(enumClass = TestNum.class, message = "类型不正确")
 *  private Integer type;
 *
 * @auth caiguowei
 * @date 2020/4/8
 */
public class NumValidator implements ConstraintValidator<Nums, Integer> {

    private Nums enumValid;
    @Override
    public void initialize(Nums constraintAnnotation) {
        this.enumValid = constraintAnnotation;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // 如果指定值或枚举，则默认不校验
        boolean emptyValues = enumValid.value() == null || enumValid.value().length == 0;
        boolean emptyEnumClass = enumValid.enumClass() == null;
        if (emptyValues && emptyEnumClass) {
            return true;
        }

        // 如果参数非必填，且切参数值为空，则无需校验
        if (!enumValid.required() && value == null){
            return true;
        }
        // 如果参数必填，但参数值为空，则直接报错
        if (enumValid.required() && value == null){
            return false;
        }

        // 如果填写了 values， 则忽略 enumClass
        if (!emptyValues){

            for (int i = 0; i < enumValid.value().length; i++) {
                int v = enumValid.value()[i];
                if ( v == value){
                    return true;
                }
            }
            return false;
        } else {
            // 要求目标 枚举类 必须实现 getValues() 方法，如果没实现，则忽略校验
            /**
             * 如
             * 要校验的参数值
             * @EnumValid(enumClass = TestNum.class)
             * private Integer type;
             *
             * public enum TestNum {
             *
             *     A(1),B(2)
             *     ;
             *
             *     TestNum(Integer code) {
             *         this.code = code;
             *     }
             *
             *     public static Integer[] getValues(){
             *         TestNum[] values = TestNum.values();
             *         Integer[] target = new Integer[values.length];
             *         return Arrays.stream(values).map(v -> v.code.intValue()).collect(Collectors.toList()).toArray(target);
             *     }
             *
             *     private Integer code;
             * }
             */
            Class<? extends Enum> enumClass = enumValid.enumClass();
            try {
                Method getValues = enumClass.getDeclaredMethod("getValues");
                Object targetValues = getValues.invoke(enumClass.getClass());
                if (targetValues != null && targetValues.getClass().isArray()){
                    Integer[] intValus = (Integer[]) targetValues;
                    return Stream.of(intValus).anyMatch(v->value.equals(v));
                }
            } catch (Exception e) {
                return true;
            }
        }

        return false;
    }


}
