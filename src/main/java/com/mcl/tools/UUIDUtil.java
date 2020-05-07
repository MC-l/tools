package com.mcl.tools;

import java.util.UUID;

/**
 * @Author MCl
 * @Date 2018-11-11 13:43
 */
public final class UUIDUtil {

    /**
     * 15位
     * @return
     */
    public static Long genIdBaseTime() {
        String numStr = RandomUtil.getRandomNumber(4);
        String s = String.valueOf(System.currentTimeMillis()).substring(2);
        return Long.valueOf(s + numStr);
    }


    
    public static String getUUId(){
        return UUID.randomUUID().toString().replace("-","");
    }
    
}
