package com.hnrd.wxPay.utils;

import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;

import java.util.Date;

/**
 * 获取10位数的时间字符串
 */
public class TimeUtils {

	public static String getFormatTime(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	public static String getFormatTime(LocalDateTime date) {
		String time= StringToLocalDateTimeUtils.parseLocalDateTimeToString(date);
		return time.replaceAll("-","").replaceAll(":","").replaceAll(" ","");
	}

	public static Date addDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 2);
		return cal.getTime();
	}

	public static Date minusDay(Date date, int day) {

		return addDay(date, -day);

	}
	//获取当前时间的10位数时间戳
	public static String nowTimestamp() {
		Date date=new Date();
		long time=date.getTime();
		String timestr=time+"";
		return timestr.substring(0,10);
	}
	public static String localdatetimeTimestamp() {
		long timestamp=LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		return (timestamp+"").substring(0,10);	
	}
}
