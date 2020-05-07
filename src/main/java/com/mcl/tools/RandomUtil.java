package com.mcl.tools;

import java.util.Random;

/**
 * @Author MCl
 * @Date 2018-11-27 10:33
 */
public final class RandomUtil {

    /**
     * 获取随机字符串
     * @param length    长度
     * @return
     */
    public static String getRandomString(int length){
        String ascii = "1234567890qwertyuiopQWERTYUIOPasdfghjklASDFGHJKLzxcvbnmZXCVBNM";
        return getRandomString(ascii,length);
    }

    /**
     * 随机数字
     * @param length
     * @return
     */
    public static String getRandomNumber(int length){
        if (length == 0){
            return "";
        }
        String nums = "0123456789";
        if (length == 1){
            return getRandomString(nums,1);
        }
        String firstNum = getRandomString("123456789",1);
        return firstNum+getRandomString(nums,length-1);
    }
    
    /**
     * 随机码=15位时间戳+(length-15)位随机码
     * @param length 总长度（若小于15，则为15）
     * @return
     */
    public static String getRandomNumberBaseTime(int length){
        String s = String.valueOf(System.currentTimeMillis());
        if (length > s.length()) {
            s = s + getRandomNumber(length - s.length());
        }
        return s;
    }

    /**
     * 获取随机字符串
     * @param strSrcPool    字符串源
     * @param length    长度
     * @return
     */
    public static String getRandomString(String strSrcPool, int length){
        int len = strSrcPool.length();
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(strSrcPool.charAt(r.nextInt(len)));
        }

        return sb.toString();
    }

}
