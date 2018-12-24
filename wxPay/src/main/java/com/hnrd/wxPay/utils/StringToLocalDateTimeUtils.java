package com.hnrd.wxPay.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class StringToLocalDateTimeUtils {

	public static LocalDateTime parseStringToLocalDateTime(String date) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(date, df);
		return dateTime;
	}
	
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
	
	/*
	 * 将Date类型转换为localDateTime
	 */
	public static LocalDateTime UDateToLocalDateTime(Date date) {
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		return localDateTime;
	}
	
	//获取东八区的当前时间
	public static LocalDateTime getCTTDateTime() {
		return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
	}
	public static String getStringLocalDateTimeCTT() {//获取当前东八区时间的字符串形式
		return StringToLocalDateTimeUtils.parseLocalDateTimeToString(StringToLocalDateTimeUtils.getCTTDateTime());
	}
}
