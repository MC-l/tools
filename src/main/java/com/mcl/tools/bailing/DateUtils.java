package com.mcl.tools.bailing;

import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @auth caiguowei
 * @date 2020/4/22
 */
public final class DateUtils {

    public static boolean isDateType(Object obj){

        Assert.notNull(obj,"参数不能为空");

        boolean b1 = obj instanceof Date;
        boolean b2 = obj instanceof LocalDate;
        boolean b3 = obj instanceof LocalDateTime;
        return b1 || b2 || b3;
    }

    public static Date toDate(Object obj) {

        Assert.notNull(obj,"参数不能为空");

        if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof LocalDate){
            return asDate((LocalDate) obj);
        } else if (obj instanceof LocalDateTime){
            return asDate((LocalDateTime) obj);
        }
        throw new RuntimeException(obj.toString() + "无法转换为 java.util.Date");
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}