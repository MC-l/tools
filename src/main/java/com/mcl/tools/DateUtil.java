package com.mcl.tools;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 日期工具类
 * @author cgw
 * @date 2017年6月8日
 */
public final class DateUtil {
	
	
	private static final char[] zeroArray =
            "0000000000000000000000000000000000000000000000000000000000000000".toCharArray();
	
	private static int DATE_MILLIS = 86400000;	// 24*60*60*1000
	private static int HOUR_MILLIS = 3600000;	// 	  60*60*1000
	
	private static String getFormat(int level){
		if (level == Calendar.YEAR){
			return "yyyy";
		}else if (level == Calendar.MONTH){
			return "yyyy-MM";
		}else if (level == Calendar.DATE){
			return "yyyy-MM-dd";
		}else if (level == Calendar.HOUR){
			return "yyyy-MM-dd hh";
		}else if (level == Calendar.MINUTE){
			return "yyyy-MM-dd hh:mm";
		}else if (level == Calendar.SECOND){
			return "yyyy-MM-dd hh:mm:ss";
		}
		return null;
	}
	
	private static Calendar getCalendar(){
		return new GregorianCalendar();
	}
	

	/**
	 * 获取当前时间前/后若干天的时间
	 * @param date
	 * @param increment
	 * @return
	 */
	public static Date incrementAndGet(Date date, int increment){
	    return incrementAndGet(date, increment, Calendar.DATE);
	}
	
	public static Date decreseAndGet(Date date, int increment){
		return incrementAndGet(date,-increment,Calendar.DATE);
	}
	public static Date decreseAndGet(Date date, int increment, int field){
		return incrementAndGet(date,-increment,field);
	}
	
	
	/**
	 * 获取当前时间前/后若干时长的时间（calender.xxx）
	 * @param date
	 * @param increment
	 * @return
	 */
	public static Date incrementAndGet(Date date, int increment, int field){
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.add(field, increment);
		Date newDate = calendar.getTime();
		return newDate;
	}
	
	/**
	 * 获取date时间after之前或者之后的时间
	 * @param date 时间节点
	 * @param after 之前（负数）,之后（正数）
	 */
	public static Date afterOrBeforeDayFormByNow(Date date, int after) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH,after);
		return date = calendar.getTime();
	}
	
	/**
	 * leftDate < rightDate
	 * 
	 * @param leftDate
	 * @param rightDate
	 * @param level (eg:Calendar.DATE)
	 * @return
	 */
	public static boolean lt(Date leftDate, Date rightDate, int level){
		int cmp = compareTo(leftDate, rightDate,level);
		return cmp < 0;
	}
	
	/**
	 * leftDate <= rightDate
	 * 
	 * @param leftDate
	 * @param rightDate
	 * @param level (eg:Calendar.DATE)
	 * @return
	 */
	public static boolean le(Date leftDate, Date rightDate, int level){
		int cmp = compareTo(leftDate, rightDate,level);
		return (cmp <= 0 );
	}
	
	/**
	 * leftDate > rightDate
	 * 
	 * @param leftDate
	 * @param rightDate
	 * @param level (eg:Calendar.DATE)
	 * @return
	 */
	public static boolean gt(Date leftDate, Date rightDate, int level){
		int cmp = compareTo(leftDate, rightDate,level);
		return (cmp > 0 );
	}
	
	/**
	 * leftDate >= rightDate <br>
	 * 
	 * @param leftDate
	 * @param rightDate
	 * @param level (eg:Calendar.DATE)
	 * @return
	 */
	public static boolean ge(Date leftDate, Date rightDate, int level){
		int cmp = compareTo(leftDate, rightDate,level);
		return ( cmp >= 0 );
	}
	
	/**
	 * leftDate == rightDate <br>
	 * 
	 * @param leftDate
	 * @param rightDate
	 * @param level (eg:Calendar.DATE)
	 * @return
	 */
	public static boolean equals(Date leftDate, Date rightDate, int level){
		int cmp = compareTo(leftDate, rightDate,level);
		return ( cmp == 0 );
	}
	
	/**
	 * startDate <= targetDate <= endDate <br>
	 * 
	 * @param targetDate
	 * @param startDate
	 * @param endDate
	 * @param level (eg:Calendar.DATE)
	 * @return
	 */
	public static boolean isBetween(Date targetDate, Date startDate, Date endDate, int level){
		boolean startLe = le(startDate,targetDate,level);
		boolean endGe = ge(endDate,targetDate,level);
		return startLe && endGe;
	}
	
	/**
	 * parse String --> Date
	 * @param dateStr
	 * @param format
	 * @return date
	 */
	public static Date parse(String dateStr, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date targetDate = null;
		try {
			targetDate = sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("Can`t parse the ["+dateStr+"] become Date Object");
		}
		
		return targetDate;
	}
	
	/**
	 * 转换日期
	 * @param dateStr
	 * @param format
	 * @param throwException	true:如果转换异常,则抛异常; false:如果转换异常,则返回null
	 * @return
	 */
	public static Date parse(String dateStr, String format, boolean throwException) {
		try {
			return parse(dateStr,format);
		} catch (Exception e) {
			if (throwException) {
				throw e;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * parse Date ---> String
	 * @param date
	 * @param format
	 * @return string
	 */
	public static String parse(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	
	/**
	 * Compare leftDate and rightDate 
	 * @param leftDate
	 * @param rightDate
	 * @param level (eg:Calendar.DATE)
	 * @return returns<br>
	 * 1: leftDate > rightDate <br>
	 * 0: leftDate = rightDate <br>
	 * -1:leftDate < rightDate
	 */
	public static int compareTo(Date leftDate, Date rightDate, int level){
		String format = getFormat(level);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String ldStr = sdf.format(leftDate);
		String rdStr = sdf.format(rightDate);
		return ldStr.compareToIgnoreCase(rdStr);
	}
	
	/**
	 * sub = leftDate - rightDate<br>
	 * 不考虑时间轴
	 * @param leftDate
	 * @param rightDate
	 * @param field
	 * @return sub
	 */
	public static int sub(Date leftDate, Date rightDate, int field){
		Calendar calendar = getCalendar();
		calendar.setTime(leftDate);
		int left = calendar.get(field);
		calendar.setTime(rightDate);
		int right = calendar.get(field);
		
		return left - right;
	}
	
	/**
	 * sub = leftDate - rightDate<br>
	 * 考虑时间轴
	 * @param leftDate
	 * @param rightDate
	 * @param field
	 * @return
	 */
	public static int subTimeAxised(final Date leftDate, final Date rightDate, int field){
		
		String fmt = getFormat(field);
		Date leftDate1 = parse(parse(leftDate, fmt),fmt);
		Date rightDate1 = parse(parse(rightDate, fmt),fmt);
		
		Calendar leftCal = Calendar.getInstance();
		leftCal.setTime(leftDate1);
		Calendar rightCal = Calendar.getInstance();
		rightCal.setTime(rightDate1);
		
		Integer difference = null;
		Long left = leftDate1.getTime();
		Long right = rightDate1.getTime();
		Long delta = left -right;	// millisecond
		
		int deltaYears = leftCal.get(Calendar.YEAR) - rightCal.get(Calendar.YEAR);
		int deltaMonthes = leftCal.get(Calendar.MONTH) - rightCal.get(Calendar.MONTH);
		
		switch(field){
			case Calendar.YEAR:
				difference = deltaYears;
				break;
			case Calendar.MONTH:
				difference = (int) (deltaYears * 12 + deltaMonthes);
				break;
			case Calendar.DATE:
				difference = (int) Math.floor(delta / DATE_MILLIS);
				break;
			case Calendar.HOUR:
				difference = (int) Math.floor(delta / HOUR_MILLIS);
				break;
			case Calendar.MINUTE:
				difference = (int) Math.floor(delta / 60000);
				break;
			case Calendar.SECOND:
				difference = (int) Math.floor(delta / 1000);
				break;
			case Calendar.MILLISECOND:
				difference = (int) (left -right);
				break;
			default:
				difference = (int) Math.floor(delta / DATE_MILLIS);
				break;
		}

		return difference;
	}
	
	public static String zeroPadString(String string, int length) {
        if (string == null || string.length() > length) {
            return string;
        }
        StringBuilder buf = new StringBuilder(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }
	
	public static String dateToMillis(Date date) {
        return zeroPadString(Long.toString(date.getTime()), 15);
    }
	
	
	/**
	 * yyyy-MM-dd HH:mm:ss 格式的字符串转Date
	 * @param dateStr
	 * @return
	 */
	public static Date StringToDate(String dateStr){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	/**
	 * date类型转换为long类型
	 * @param date
	 * @return
	 */
 	public static long dateToLong(Date date) {
 		return date.getTime();
 	}
	
	/**
	 * @Title: yearPoor  
	 * @Description: 计算两个date时间段的年份之差
	 * @param @param date date时间
	 * @return Integer 年份=之差
	 * @author 彭河川
	 * @date 2017年8月8日    下午8:08:23
	 */
	public static Integer yearPoor(Date date) {
		if (date == null) {
			return 0;
		} else {
			Calendar bef = Calendar.getInstance();
			Calendar aft = Calendar.getInstance();
			bef.setTime(date);
			aft.setTime(new Date());
			return (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR));
		}
	}
	
	/**
	 * CST时间字符串转换成date
	 * @param cst
	 * @return
	 */
	public static Date CST2Date(String cst) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		Date date = null;
		try {
			date = sdf.parse(cst);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 计算两个时间的毫秒数
	 * @param date1 被减数（晚一点的时间）
	 * @param date2 减数（早一点的时间）
	 */
	public static long subDateForSecond(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return 0L;
		}
		BigDecimal sub = new BigDecimal(date1.getTime()).subtract(new BigDecimal(date2.getTime()));
		return sub.longValue();
	}
	
	/**
	 * 获取年月日时分秒毫秒
	 * @param date
	 * @param field
	 * @return
	 */
	public static int getField(Date date, int field){
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return calendar.get(field);
	}
	
	/**
	 * 判断day是否是指定date当月的最后一天
	 * @param date
	 * @param day
	 * @return
	 */
	public static boolean isLastDayOfMonth(Date date, int day){
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		int maxDay = calendar.getActualMaximum(Calendar.DATE);
		
		return day == maxDay;
	}
	
	/**
	 * 获取该年是365天还是366天
	 * @param date
	 * @return
	 */
	public static int getDaysOfYear(Date date){
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
	}
	
	public static Date setHMS(Date date,int hours,int minutes,int seconds) {
		Calendar instance = Calendar.getInstance();
		instance.setTime(date);
		instance.set(Calendar.HOUR_OF_DAY, hours <=0 ? 0 : hours);
		instance.set(Calendar.MINUTE, minutes <=0 ? 0 : minutes);
		instance.set(Calendar.SECOND, seconds <=0 ? 0 : seconds);
		return instance.getTime();
	}
	
	/**
	 * 比较两个date的时间先后顺序，前者在后者之前返回true
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException
	 */
	public static boolean compare(Date date1,Date date2) throws ParseException  {
		if(date1.getTime()-date2.getTime()<0) {
			 return true; 
		}else {
            return false; 
		}
	}
	
	/**
	 * 获取现在的时间
	 * @param format
	 * @return
	 */
	public static String now(String format) {
		return parse(new Date(),format);
	}
	/**
	 * 判断是否是同一时期
	 * @param d1
	 * @param d2
	 * @param field
	 * @return
	 */
	public static boolean isSame(Date d1,Date d2,int field) {
		return DateUtil.subTimeAxised(d1, d2, field) == 0;
	}
	
	/**
	 * 获取日期是星期几
	 * @param date 日期
	 */
	public static String getWeekFromDate(Date date) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}
	
	public static Date max(Date ...dates) {
		if (dates == null || dates.length == 0) {
			return null;
		}
		Date max = dates[0];
		for (int i = 1; i < dates.length; i++) {
			if (lt(max,dates[i],Calendar.DATE)) {
				max = dates[i];
			}
		}
		return max;
	}
	public static Date min(Date ...dates) {
		if (dates == null || dates.length == 0) {
			return null;
		}
		Date min = dates[0];
		for (int i = 1; i < dates.length; i++) {
			if (gt(min,dates[i],Calendar.DATE)) {
				min = dates[i];
			}
		}
		return min;
	}


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
