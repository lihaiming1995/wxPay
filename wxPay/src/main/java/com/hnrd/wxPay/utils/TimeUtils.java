package com.hnrd.wxPay.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	/**
	 * 将字符串转化位为localdateTime时间格式
	 * @param date
	 * @return
	 */
	public static LocalDateTime parseStringToLocalDateTime(String date) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(date, df);
		return dateTime;
	}
	/**
	 * 将localhosttime类型转为字符串格式
	 * @param time
	 * @return
	 */
	public static String parseLocalDateTimeToString(LocalDateTime time) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String localTime = df.format(time);
		return localTime;
		
	}
	/**
	 * 获取下一天的时间
	 * @return
	 */
	public static LocalDateTime getNextDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 1);
		Date date = calendar.getTime();
		return UDateToLocalDateTime(date);
	}
	
	/**
	 * 将Date类型转换为localDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime UDateToLocalDateTime(Date date) {
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		return localDateTime;
	}
	
	/**
	 * 获取东八区的当前时间
	 * @return
	 */
	public static LocalDateTime getCTTDateTime() {
		return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
	}
	/**
	 * 获取当前东八区时间的字符串形式
	 * @return
	 */
	public static String getStringLocalDateTimeCTT() {
		return TimeUtils.parseLocalDateTimeToString(TimeUtils.getCTTDateTime());
	}
	/**
	 * 获取东八区当前时间的10位数时间戳
	 * @return
	 */
	public static String localdatetimeTimestamp() {
		long timestamp=LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		return (timestamp+"").substring(0,10);	
	}
	/**
	 * 将字符串格式的时间去掉所有数字以外的符号只留下规定位数的数字串
	 * @param date
	 * @return
	 */
	public static String getFormatTime(LocalDateTime date,int n) {
		String time= TimeUtils.parseLocalDateTimeToString(date);
		return time.replaceAll("-","").replaceAll(":","").replaceAll(" ","").substring(0, n);
	}
	public static void main(String[] args) {
		System.out.println(getStringLocalDateTimeCTT());
		System.out.println(getFormatTime(getCTTDateTime(),10));
	}
}
