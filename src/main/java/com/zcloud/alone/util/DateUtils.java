package com.zcloud.alone.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期（时间）工具类
 * @author dzm
 */
public class DateUtils {

	/**
	 * 将 Date 转成 LocalDate
	 * @param date
	 * @return
	 */
	public static LocalDate dateToLocalDate(Date date) {
		if(null == date) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * 将 Date 转成 LocalDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		if(null == date) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * 将 LocalDate 转成 Date
	 * @param localDate
	 * @return
	 */
	public static Date localDateToDate(LocalDate localDate) {
		if(null == localDate) {
			return null;
		}
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 将 LocalDateTime 转成 Date
	 * @return
	 */
	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		if(null == localDateTime) {
			return null;
		}
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}