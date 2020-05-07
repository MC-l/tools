package com.mcl.tools;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 数字工具类
 * @Author MCl
 * @Date 2018-12-16 11:04
 */
public class NumUtil {

    private NumUtil(){}

    public static boolean isNum(String numStr){
        boolean isnum = numStr != null && !numStr.equalsIgnoreCase("null") && !numStr.trim().equals("");
        isnum = isnum ? Pattern.matches("-{0,1}([0-9]*\\.{0,1}\\d{1,})|([0-9]*\\.[0-9]*E-[0-9]{1,})$", numStr) : isnum;
        return isnum;
    }

    /**
     * 是否是正数
     * @param num
     * @return
     */
    public static boolean isPositive(Number num){
        if (num == null){
            return false;
        }
        if (num instanceof Integer){
            return num.intValue() > 0;
        } else if (num instanceof Short){
            return num.shortValue() > 0;
        } else if (num instanceof Long){
            return num.longValue() > 0L;
        } else if (num instanceof Float){
            return BigDecimal.valueOf((float)num).compareTo(BigDecimal.valueOf(0.0F)) > 0;
        } else if (num instanceof Double){
            return BigDecimal.valueOf((double)num).compareTo(BigDecimal.valueOf(0.0D)) > 0;
        } else if (num instanceof Byte){
            return num.byteValue() > 0;
        }
        return false;
    }

    /**
     * 是否是非正数
     * @param num
     * @return
     */
    public static boolean isNotPositive(Number num){
        return !isPositive(num);
    }

    /**
     * 是否是负数
     * @param num
     * @return
     */
    public static boolean isNegetive(Number num){
        if (num == null){
            return false;
        }
        if (num instanceof Integer){
            return num.intValue() < 0;
        } else if (num instanceof Short){
            return num.shortValue() < 0;
        } else if (num instanceof Long){
            return num.longValue() < 0L;
        } else if (num instanceof Float){
            return BigDecimal.valueOf((float)num).compareTo(BigDecimal.valueOf(0.0F)) < 0;
        } else if (num instanceof Double){
            return BigDecimal.valueOf((double)num).compareTo(BigDecimal.valueOf(0.0D)) < 0;
        } else if (num instanceof Byte){
            return num.byteValue() < 0;
        }
        return false;
    }

    /**
     * 是否是非负数
     * @param num
     * @return
     */
    public static boolean isNotNegetive(Number num){
        return !isNegetive(num);
    }

    /**
     * 是否全部都为正数
     * @param nums
     * @return
     */
    public static boolean isAllPositive(Number ... nums){
        checkNums(nums);
        return Arrays.stream(nums).allMatch(NumUtil::isPositive);
    }

    /**
     * 是否存在非正数
     * @param nums
     * @return
     */
    public static boolean hasNotPositive(Number ... nums){
        return !isAllPositive(nums);
    }


    /**
     * 是否是空或0
     * @param num
     * @return
     */
    public static boolean isEmpty(Number num){
        if (num == null){
            return true;
        }
        if (num instanceof Integer){
            return num.intValue() == 0;
        } else if (num instanceof Short){
            return num.shortValue() == 0;
        } else if (num instanceof Long){
            return num.longValue() == 0L;
        } else if (num instanceof Float){
            return BigDecimal.valueOf((float)num).compareTo(BigDecimal.valueOf(0.0F)) == 0;
        } else if (num instanceof Double){
            return BigDecimal.valueOf((double)num).compareTo(BigDecimal.valueOf(0.0D)) == 0;
        } else if (num instanceof Byte){
            return num.byteValue() == 0;
        }
        return false;
    }

    /**
     * 是否是非0数字
     * @param num
     * @return
     */
    public static boolean isNotEmpty(Number num){
        return !isEmpty(num);
    }

    /**
     * 是否存在空或0
     * @param nums
     * @return
     */
    public static boolean hasEmpty(Number ... nums){
        checkNums(nums);
        return Arrays.stream(nums).anyMatch(NumUtil::isEmpty);
    }

    private static void checkNums(Number ... nums){
        if (nums == null || nums.length == 0){
            throw new IllegalArgumentException("Parameter nums can not be empty.");
        }
    }

    /**
     * 计算一个随机值,该值介于[min,max)之间<br>
     * @param min
     * @param max
     * @return
     * @exception	当 min > max时,抛出运行时非法参数异常
     */
    public static int genRandBetween(int min, int max){
        if (min > max) throw new IllegalArgumentException("min > max");
        if (min == max) return min;
        Random rd = new Random();
        int nextInt = rd.nextInt(max-min);
        return nextInt + min;
    }


    /**
     * 判断数字集合中是否包含指定数字
     * @param n		指定数字
     * @param nums	数字集合
     * @return
     */
    public static boolean contains(Number n,Number ...nums) {
        if (n == null || nums == null) throw new IllegalArgumentException("参数都不能为空");
        for (Number num : nums) {
            if (n.equals(num)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如果为null,则返回0
     * @param number
     * @return
     */
    public static Number return0IfNull(Number number){
        return isEmpty(number) ? 0 : number;
    }

    /**
     * num 要转换的数 from源数的进制 to要转换成的进制
     * @param num
     * @param from
     * @param to
     * @return
     */
    public static String change(String num,int from, int to){
        return new java.math.BigInteger(num, from).toString(to);
    }

}
