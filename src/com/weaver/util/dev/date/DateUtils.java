/**
 * <br>
 * Copyright 2013 IFlyTek. All rights reserved.<br>
 * <br>
 * Package: com.com.utils <br>
 * FileName: DateUtils.java <br>
 * <br>
 * @version
 * @author wzgao
 * @created 2013-3-7
 * @last Modified
 * @history
 */

package com.weaver.util.dev.date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 时间工具类
 *
 * @author fsl
 */

public class DateUtils {

	public final static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 有符号时间
	 */
	public final static String FORMAT_NORMAL = "yyyy/MM/dd HH:mm:ss";

	/**
	 * 无符号时间
	 */
	public final static String FORMAT_NO_SIGN = "yyyyMMddHHmmss";
	/**
	 * 时间格式 （"yyyyMMdd"）
	 */
	public final static String FORMAT_YYYYMMDD = "yyyyMMdd";
	/**
	 * 时间格式 （"yyyyMM"）
	 */
	public final static String FORMAT_YYYYMM = "yyyyMM";
	/**
	 * 时间格式 （"yyyy"）
	 */
	public final static String FORMAT_YYYY = "yyyy";

	/**
	 * 时间格式 （"yyyy-MM-dd"）
	 */
	public final static String FORMAT_YYYYSMMSDD = "yyyy-MM-dd";

	/**
	 *
	 * 获取当前时间字符串
	 *
	 * @param formate
	 *            时间格式
	 * @return 时间字符串
	 * @author xkfeng@com.com
	 * @created 2013-4-2 下午07:10:02
	 * @lastModified
	 * @history
	 */
	public static String getDate(String formate) {
		if (!StringUtils.isNotBlank(formate)) {
			SimpleDateFormat df = new SimpleDateFormat(FORMAT_NO_SIGN);
			return df.format(new Date());
		} else {
			SimpleDateFormat df = new SimpleDateFormat(formate);
			return df.format(new Date());
		}
	}

	/**
	 *
	 * 获取当前时间字符串，格式“yyyyMMddHHmmss”
	 *
	 * @return 时间字符串
	 * @author xkfeng@com.com
	 * @created 2013-4-2 下午07:07:48
	 * @lastModified
	 * @history
	 */
	public static String getDate() {
		String formate = FORMAT_NO_SIGN;
		SimpleDateFormat df = new SimpleDateFormat(formate);
		return df.format(new Date());
	}

	/**
	 * 获取当前时间字符串，格式“yyyyMMdd” ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 *
	 * @return
	 * @author xzran
	 * @created 2016-2-22 下午06:44:25
	 * @lastModified
	 * @history
	 */
	public static String getDateEight() {
		String formate = FORMAT_YYYYMMDD;
		SimpleDateFormat df = new SimpleDateFormat(formate);
		return df.format(new Date());
	}

	/**
	 *
	 * 时间格式化
	 *
	 * @param date
	 *            日期时间
	 * @param formate
	 *            格式化
	 * @return 字符串类型时间
	 * @author xkfeng@com.com
	 * @created 2013-4-2 下午07:08:21
	 * @lastModified
	 * @history
	 */
	public static String getDate(Date date, String formate) {
		if (!StringUtils.isNotBlank(formate)) {
			SimpleDateFormat df = new SimpleDateFormat(FORMAT_NO_SIGN);
			return df.format(date);
		} else {
			SimpleDateFormat df = new SimpleDateFormat(formate);
			return df.format(date);
		}

	}

	/**
	 * 	获取去年的这个时候
	 *  @param formate
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月15日 下午2:02:17
	 *  @lastModified
	 *  @history
	 */
	public static String getLastYearNow(String formate){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 * 	获取明年的这个时候
	 *  @param formate
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月15日 下午2:02:17
	 *  @lastModified
	 *  @history
	 */
	public static String getNextYearNow(String formate){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 * 	一个月前的时间
	 *  @param formate
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月31日 下午7:54:07
	 *  @lastModified
	 *  @history
	 */
	public static String getMonthAgo(String formate){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.YEAR, calendar.get(Calendar.DATE)-30);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 * 	5年前的年底时间
	 *  @param formate
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月31日 下午7:57:24
	 *  @lastModified
	 *  @history
	 */
	public static String get5YearAgo(String formate){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-4);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 * 	取任意的月份
	 *  @param formate
	 *  @param ca
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月15日 下午2:11:29
	 *  @lastModified
	 *  @history
	 */
	public static String getLastOrNextMonth(String formate,int ca){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+ca);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 * 	顺次取年份
	 *  @param formate
	 *  @param ca
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月31日 下午8:05:57
	 *  @lastModified
	 *  @history
	 */
	public static String getLastOrNextYear(String formate,int ca){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+ca);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 * 	顺次取天
	 *  @param formate
	 *  @param ca
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年8月31日 下午8:06:43
	 *  @lastModified
	 *  @history
	 */
	public static String getLastOrNextDay(String formate,int ca){
		Calendar calendar = Calendar.getInstance();//得到日历的实例
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+ca);
		return DateUtils.getDate(calendar.getTime(),formate);
	}

	/**
	 *
	 * 格式化前台时间控件格式为yyyyMMddHHmmss 年月日的情况，添加6位000000
	 *
	 * @param date
	 *            日期时间
	 * @return 字符串类型时间
	 * @author wzgao
	 * @created 2013-3-19 下午02:46:34
	 * @lastModified
	 * @history
	 */
	public static String formatStartDate(String date) {

		if (StringUtils.isNotBlank(date) && date.length() == 10) {
			return date.replace(":", "").replace(" ", "").replace("-", "")
					.replace("/", "")
					+ "000000";
		} else if (StringUtils.isNotBlank(date) && date.length() == 4){
			return date + "0101000000";
		}else if (StringUtils.isNotBlank(date) && date.length() == 8){
			return date + "000000";
		}else if (StringUtils.isNotBlank(date)) {
			return date.replace(":", "").replace(" ", "").replace("-", "")
					.replace("/", "");
		}
		return null;
	}

	/**
	 *
	 * 格式化前台结束时间控件格式为yyyyMMddHHmmss 年月日的情况，添加6位595959
	 *
	 * @param date
	 *            日期时间
	 * @return 字符串类型时间
	 * @author wzgao
	 * @created 2013-4-1 下午01:34:51
	 * @lastModified
	 * @history
	 */
	public static String formatEndDate(String date) {
		if (StringUtils.isNotBlank(date) && date.length() == 10) {
			return date.replace(":", "").replace(" ", "").replace("-", "")
					.replace("/", "")
					+ "235959";
		} else if (StringUtils.isNotBlank(date) && date.length() == 4){
			return date + "1231235959";
		}else if (StringUtils.isNotBlank(date) && date.length() == 8){
			return date + "235959";
		}else if (StringUtils.isNotBlank(date)) {
			return date.replace(":", "").replace(" ", "").replace("-", "")
					.replace("/", "");
		}
		return null;
	}

	/**
	 *
	 * 格式化前台时间控件格式为HHmmss
	 *
	 * @param date
	 *            日期时间
	 * @return 字符串类型时间
	 * @author wzgao
	 * @created 2013-4-2 下午03:05:14
	 * @lastModified
	 * @history
	 */
	public static String formatTime(String date) {
		if (StringUtils.isNotBlank(date) && date.length() == 8) {
			return date.replace(":", "");
		}
		return null;
	}

	/**
	 *
	 * 格式化前台日期时间
	 *
	 * @param datetime
	 *            日期
	 * @return 格式话之后格式
	 * @author xkfeng@com.com
	 * @created 2013-8-19 下午04:12:54
	 * @lastModified
	 * @history
	 */
	public static String formatDateTime(String datetime) {
		if (StringUtils.isNotBlank(datetime)) {
			return datetime.replaceAll(":", "").replaceAll(" ", "")
					.replaceAll("-", "").replaceAll("/", "");
		}
		return null;
	}

	/**
	 *
	 * 数据库日期字符串格式化成前台展现格式
	 *
	 * @param date
	 *            yyyyMMDD格式
	 * @return yyyy-MM-dd格式
	 * @author lwchen
	 * @created 2013-8-23 下午04:20:37
	 * @lastModified
	 * @history
	 */
	public static String formatDate(String date) {
		if (date.length() == 8) {
			return date.substring(0, 4) + "/" + date.substring(4, 6) + "/"
					+ date.substring(6, 8);
		} else if (date.length() == 14) {
			return date.substring(0, 4) + "/" + date.substring(4, 6) + "/"
					+ date.substring(6, 8) + " " + date.substring(8, 10) + ":"
					+ date.substring(10, 12) + ":" + date.substring(12, 14);
		}

		return null;
	}

	/**
	 *
	 * 数据库日期字符串格式化成前台展现格式
	 *
	 * @param date
	 *            yyyyMMDD格式
	 * @return yyyy-MM-dd格式
	 * @author lwchen
	 * @created 2013-8-23 下午04:20:37
	 * @lastModified
	 * @history
	 */
	public static String formatDateLine(String date) {
		if (date.length() == 8) {
			return date.substring(0, 4) + "-" + date.substring(4, 6) + "-"
					+ date.substring(6, 8);
		} else if (date.length() == 14) {
			return date.substring(0, 4) + "-" + date.substring(4, 6) + "-"
					+ date.substring(6, 8) + " " + date.substring(8, 10) + ":"
					+ date.substring(10, 12) + ":" + date.substring(12, 14);
		}

		return null;
	}

	public static String formatDateLineNotYear(String date) {
		if (date.length() == 8) {
			return date.substring(4, 6) + "-"
					+ date.substring(6, 8);
		} else if (date.length() == 14) {
			return  date.substring(4, 6) + "-"
					+ date.substring(6, 8) + " " + date.substring(8, 10) + ":"
					+ date.substring(10, 12) + ":" + date.substring(12, 14);
		}

		return null;
	}

	/**
	 * 14位转换yyyy-MM-dd
	 *
	 * @author: xzran
	 * @createTime: 2017年11月18日 下午5:55:11
	 * @history:
	 * @param date
	 * @return String
	 */
	public static String formatDateLineEight(String date) {
		return date.substring(0, 4) + "-" + date.substring(4, 6) + "-"
				+ date.substring(6, 8);
	}

	/**
	 *
	 * 获得当前月的第一天
	 *
	 * @return data 日期
	 * @author lwchen
	 * @created 2013-8-23 下午05:06:52
	 * @lastModified
	 * @history
	 */
	public static Date getFirstDayOfMonth() {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, 1);

		return ca.getTime();
	}

	/**
	 * 字符转时间
	 *
	 * @param dateStr
	 *            时间字符串
	 * @param format
	 *            格式
	 * @return data 时间对象
	 * @author xkfeng@com.com
	 * @created 2013-8-26 下午04:15:30
	 * @lastModified
	 * @history
	 */
	public static Date parseDate(String dateStr, String format) {
		try {
			return new SimpleDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date(0);
	}

	/**
	 *
	 * 得到日期对应的周开始，结束，如"2013-08-20"->"2013.08.19-2013.08.25"
	 *
	 * @param dateStr
	 *            日期
	 * @return 时间范围string
	 * @author xkfeng@com.com
	 * @created 2013-9-5 下午07:28:50
	 * @lastModified
	 * @history
	 */
	public static String getDateRangeByDate(String dateStr) {
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int weekday = calendar.get(Calendar.DAY_OF_WEEK);
			return getDate(addDays(d, 2 - weekday), "yyyy.M.d") + "-"
					+ getDate(addDays(d, 7 - weekday + 1), "yyyy.M.d");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * 得到给定日期所在月的最后一天
	 *
	 * @param date
	 *            指定日期
	 * @param fromFmt
	 *            指定格式
	 * @param toFmt
	 *            转换格式
	 * @return 最后一天
	 * @author xkfeng@com.com
	 * @created 2013-8-21 下午03:37:07
	 * @lastModified
	 * @history
	 */
	public static String getLastDayByMonth(String date, String fromFmt,
										   String toFmt) {
		try {
			Date temp = new SimpleDateFormat(fromFmt).parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(temp);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date d = addMilliseconds(addMonths(calendar.getTime(), 1), -1);
			return getDate(d, toFmt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * 得到给定日期所在月的第一天
	 *
	 * @param date
	 *            指定日期
	 * @param fromFmt
	 *            指定格式
	 * @param toFmt
	 *            转换格式
	 * @return 指定日期所在月的第一天
	 * @author xkfeng@com.com
	 * @created 2013-8-26 下午08:31:12
	 * @lastModified
	 * @history
	 */
	public static String getFirstDayByMonth(String date, String fromFmt,
											String toFmt) {
		try {
			Date temp = new SimpleDateFormat(fromFmt).parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(temp);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return getDate(calendar.getTime(), toFmt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * 得到给定日期所在周星期一
	 *
	 * @param date
	 *            指定日期
	 * @param fromFmt
	 *            指定格式
	 * @param toFmt
	 *            转换格式
	 * @return 给定日期所在周星期一
	 * @author xkfeng@com.com
	 * @created 2013-8-21 下午03:27:36
	 * @lastModified
	 * @history
	 */
	public static String getFirstDateByWeek(String date, String fromFmt,
											String toFmt) {
		try {
			Date d = new SimpleDateFormat(fromFmt).parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int weekday = calendar.get(Calendar.DAY_OF_WEEK);
			String day = "";
			if (1 == weekday) {
				day = getDate(addDays(d, 2 - weekday - 7), toFmt);
			} else {
				day = getDate(addDays(d, 2 - weekday), toFmt);
			}
			return day;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * 得到给定日期所在周星期日
	 *
	 * @param date
	 *            指定日期
	 * @param fromFmt
	 *            指定格式
	 * @param toFmt
	 *            转换格式
	 * @return 日期所在周的星期日的日期
	 * @author xkfeng@com.com
	 * @created 2013-8-21 下午03:28:22
	 * @lastModified
	 * @history
	 */
	public static String getLastDateByWeek(String date, String fromFmt,
										   String toFmt) {
		try {
			Date d = new SimpleDateFormat(fromFmt).parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int weekday = calendar.get(Calendar.DAY_OF_WEEK);
			String day = "";
			if (1 == weekday) {
				day = getDate(addSeconds(d, 86400 - 1), toFmt);
			} else {
				day = getDate(addSeconds(d, (7 - weekday + 2) * 86400 - 1),
						toFmt);
			}
			return day;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 得到前一天
	 *
	 * @param fromFmt
	 *            formate格式
	 * @return 当前日期前一天
	 * @author rongwang
	 * @created 2013-10-22 上午10:45:46
	 * @lastModified
	 * @history
	 */
	public static String getYesterday(String fromFmt) {
		// 取时间
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		// 把日期往后增加一天.整数往后推,负数往前移动
		calendar.add(calendar.DATE, -1);
		// 这个时间就是日期往后推一天的结果
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(fromFmt);
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * Adds to a date returning a new object. The original date object is
	 * unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param calendarField
	 *            the calendar field to add to
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 * @deprecated Will become privately scoped in 3.0
	 */
	public static Date add(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	/**
	 * Adds to a date returning a new object. The original date object is
	 * unchanged.
	 *
	 * @param dateStr
	 *            the date, not null
	 *            the calendar field to add to
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 * @deprecated Will become privately scoped in 3.0
	 */
	public static String addHour(String dateStr, int amount,String formate) {
		if (dateStr == null&&"".equals(dateStr)) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(StrToDate(dateStr,formate));
		c.add(Calendar.HOUR_OF_DAY, amount);
		return DateToStr(c.getTime(),formate);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of years to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addYears(Date date, int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of months to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMonths(Date date, int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of weeks to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addWeeks(Date date, int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of days to a date returning a new object. The original date
	 * object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addDays(Date date, int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of hours to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addHours(Date date, int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of minutes to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMinutes(Date date, int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of seconds to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addSeconds(Date date, int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of milliseconds to a date returning a new object. The
	 * original date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMilliseconds(Date date, int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	/**
	 *
	 * 获取当前时间 （格式yyyyMMddHHmmss）
	 *
	 * @return
	 * @author aoliu
	 * @created 2016年1月6日 上午9:32:43
	 * @lastModified
	 * @history
	 */
	public static String getNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_NO_SIGN);
		return sdf.format(new Date());
	}

	/**
	 * 字符串yyyy-MM-dd HH:mm:ss 转换yyyyMMddHHmmss ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 *
	 * @param str
	 * @return
	 * @author qxb-810
	 * @created 2016年2月23日 下午4:10:30
	 * @lastModified
	 * @history
	 */
	public static String getDateFormat(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.replace("-", "").replace(":", "").replace(" ", "");
		}
		return StringUtils.EMPTY;
	}

	/**
	 * 字符串HH:mm:ss 转换HHmmss ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 *
	 * @param str
	 * @return
	 * @author fsl
	 * @created 2019年10月15日 下午2:10:30
	 * @lastModified
	 * @history
	 */
	public static String getTimeFormat(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.replace(":", "").replace(" ", "");
		}
		return StringUtils.EMPTY;
	}

	/**
	 *
	 *  获取指定格式时间
	 *  @param dateStr
	 *  @param formatStr
	 *  @return
	 *  @throws ParseException
	 *  @author qxb-810
	 *  @created 2016年3月14日 下午3:42:21
	 *  @lastModified
	 *  @history
	 */
	public static String getDateFormat(String dateStr, String formatStr)
			throws ParseException {
		if (StringUtils.isNotBlank(dateStr)) {
			if (StringUtils.isBlank(formatStr)) {
				formatStr = DateUtils.FORMAT_NORMAL;
			}
			SimpleDateFormat df = new SimpleDateFormat(formatStr);
			return df.format(df.parse(dateStr));
		}
		return "";
	}

	/**
	 *
	 *  获取当前年份
	 *  @return
	 *  @author lwsong
	 *  @created 2016年4月5日 下午5:46:46
	 *  @lastModified
	 *  @history
	 */
	public static int getCurrentYear(){
		Calendar c=Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}

	/**
	 *  相差 多少分钟
	 *  @return
	 *  @author khlu@iflyteck.com
	 *  @created 2017年6月19日 上午10:07:19
	 *  @lastModified
	 *  @history
	 */
	public static BigDecimal getMintesWithTwoTime(Date begin,Date end){
		if(begin == null || end == null){
			return null;
		}
		Calendar dateOne=Calendar.getInstance(),dateTwo=Calendar.getInstance();
		dateOne.setTime(begin);
		dateTwo.setTime(end);
		BigDecimal fengZi = new BigDecimal(dateTwo.getTimeInMillis() - dateOne.getTimeInMillis());
		BigDecimal fengmu = new BigDecimal(1000*50);
		return fengZi.divide(fengmu,2,RoundingMode.HALF_UP);
	}

	/**
	 * 获取几天前的时间,格式“yyyyMMddHHmmss”20171128201845
	 * @author: xyli9
	 * @createTime: 2017年11月28日 下午7:54:40
	 * @history:
	 * @param d
	 * @param day
	 * @return Date
	 */
	public static String getDateBefore(Date d,int day){
		Calendar now =Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
		String formate = FORMAT_NO_SIGN;
		SimpleDateFormat df = new SimpleDateFormat(formate);
		return df.format(now.getTime());
	}

	/**
	 * 获取几天前的时间,格式任意
	 *
	 * @author: xyli9
	 * @createTime: 2017年11月28日 下午7:54:40
	 * @history:
	 * @param d
	 * @param day
	 * @return Date
	 */
	public static String getDateBefore(Date d, int day, String formate) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.SECOND, now.get(Calendar.SECOND) - day * 24 * 60 * 60);
		SimpleDateFormat df = new SimpleDateFormat(formate);
		return df.format(now.getTime());
	}
	/**
	 *
	 *  取前几秒的时间
	 * @author: xzran
	 * @createTime: 2018年4月2日 下午3:51:15
	 * @history:
	 * @param time
	 * @param second   秒
	 * @return String
	 */
	public static String getDateAfterSecond( String time , int second){
		String formate = FORMAT_NO_SIGN;
		SimpleDateFormat df = new SimpleDateFormat(formate);
		Date d = null;
		try {
			d = df.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar now =Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.SECOND,now.get(Calendar.SECOND)- second);
		return df.format(now.getTime());
	}

	/**
	 * 比较两个日期获取较小值
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static String compareAndGetMin(String beginTime,String endTime) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date bt=sdf.parse(beginTime);
		Date et=sdf.parse(endTime);
		if (bt.before(et)){
			//表示bt小于et
			return beginTime;
		}else{
			return endTime;
		}
	}

	/**
	 * 比较两个日期并且获取较大值
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static String compareAndGetMax(String beginTime,String endTime) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date bt=sdf.parse(beginTime);
		Date et=sdf.parse(endTime);
		if (bt.before(et)){
			//表示bt小于et
			return endTime;
		}else{
			return beginTime;
		}
	}





	/**
	 * 拼接pmStr
	 * 拼接逻辑
	 *  1、超过一年显示1到12月
	 *  2、不到一年显示区间的月份
	 * @param minDate 开始的日期
	 * @param maxDate 结束的日期
	 * @return
	 */
	public static String appendPmStr(String minDate, String maxDate) throws ParseException {
		String pmStr="";
		String startMonth = minDate.substring(0,7);
		String endMonth = maxDate.substring(0,7);
		String startYear = minDate.substring(0,4);
		String endYear = maxDate.substring(0,4);
		int monthDiff = getMonthDiff(startMonth, endMonth);
		if((monthDiff-1)>=12){
			return "1月,2月,3月,4月,5月,6月,7月,8月,9月,10月,11月,12月";
		}
		int yearDiff = getYearDiff(startYear,endYear);
		if(yearDiff==0){
			String monthStart = minDate.substring(5, 7);
			String monthEnd = maxDate.substring(5,7);
			for (int i = Integer.valueOf(monthStart); i <= Integer.valueOf(monthEnd); i++) {
				pmStr = pmStr+","+i+"月";
			}
			if (pmStr.startsWith(",")){
				return pmStr.substring(1,pmStr.length());
			}
		}else{
			String monthStart = minDate.substring(5, 7);
			String monthEnd = maxDate.substring(5,7);
			for (int i = Integer.valueOf(monthStart); i <=12 ; i++) {
				pmStr = pmStr+","+i+"月";
			}
			for (int i = 1; i <= Integer.valueOf(monthEnd); i++) {
				pmStr = pmStr+","+i+"月";
			}
			if (pmStr.startsWith(",")){
				return pmStr.substring(1,pmStr.length());
			}
		}
		return pmStr;
	}


	/**
	 * 获取月份差
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static int getMonthDiff(String startDate, String endDate) {
		if(StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)){
			startDate = startDate.replaceAll("-","");
			endDate = endDate.replaceAll("-","");
			String startYear = startDate.substring(0,4);
			String endYear = endDate.substring(0,4);
			String startMon = startDate.substring(4,6);
			String endMon = endDate.substring(4,6);
			Integer yearDiff = Integer.valueOf(endYear)-Integer.valueOf(startYear);
			if(yearDiff==0){
				return  Integer.valueOf(endMon)-Integer.valueOf(startMon)+1;
			}
			Integer diff1 = 12-Integer.valueOf(startMon)+1;
			Integer diff2 = Integer.valueOf(endMon);
			return diff1+diff2+(yearDiff-1)*12;
		}
		return -1;
	}

	/**
	 *获取年份差
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static int getYearDiff(String startDate, String endDate) {
		/*if(!"".equals(startYear)&&null!=startYear&&!"".equals(endYear)&&null!=endYear){*/
		if(StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)){
			startDate = startDate.replaceAll("-","");
			endDate = endDate.replaceAll("-","");
			String startYear = startDate.substring(0,4);
			String endYear = endDate.substring(0,4);
			Integer yearDiff = Integer.valueOf(endYear)-Integer.valueOf(startYear);
			return yearDiff;
		}
		return -1;
	}

	/**
	 * 比较两个日期的大小 time1<time2  true
	 * @param time1
	 * @param time2
	 * @return
	 * @throws ParseException
	 */
	public static boolean compare(String time1,String time2,String formate) throws ParseException
	{
		//如果想比较日期则写成"yyyy-MM-dd"就可以了
		SimpleDateFormat sdf=new SimpleDateFormat(formate);
		//将字符串形式的时间转化为Date类型的时间
		Date a=sdf.parse(time1);
		Date b=sdf.parse(time2);
		//Date类的一个方法，如果a早于b返回true，否则返回false
		if(a.before(b))
			return true;
		else
			return false;
		/*
		 * 如果你不喜欢用上面这个太流氓的方法，也可以根据将Date转换成毫秒
		if(a.getTime()-b.getTime()<0)
			return true;
		else
			return false;
		*/
	}

	/**
	 * 获取两个日期相差几个月
	 * @author fsl
	 * @date 2016-11-30 下午7:57:32
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	public static int getMonth(String  startStr, String endStr,String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date start = sdf.parse(startStr);
		Date end = sdf.parse(endStr);
		if (start.after(end)) {
			Date t = start;
			start = end;
			end = t;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);
		Calendar temp = Calendar.getInstance();
		temp.setTime(end);
		temp.add(Calendar.DATE, 1);

		int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

		if ((startCalendar.get(Calendar.DATE) == 1)&& (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month + 1;
		} else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month;
		} else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
			return year * 12 + month;
		} else {
			return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
		}
	}

	/**
	 * 获取时间差信息  要求1.8jdk
	 * @param startTime
	 * @param endTime
	 * @param format
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static long dateDiff(String startTime, String endTime, String format,String type) throws Exception {
		DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
		//LocalDateTime time = LocalDateTime.now();
		LocalDateTime ldt = LocalDateTime.parse(startTime,df);
		LocalDateTime ldt2 = LocalDateTime.parse(endTime,df);
		//计算出两个时间的差值
		Duration differenceValue = Duration.between(ldt ,ldt2 );
		if("MIN".equals(type)){
			return differenceValue.toMinutes();
		}
		if("HOUR".equals(type)){
			return differenceValue.toHours();
		}
		if("SEC".equals(type)){
			return differenceValue.getSeconds();
		}
		if("DAY".equals(type)){
			return differenceValue.toDays();
		}
		return -1;
	}

	public static long dateDiffNow(String time, String format,String type) throws Exception {
		DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
		LocalDateTime localTime = LocalDateTime.now();
		LocalDateTime ldt = LocalDateTime.parse(time,df);
		//计算出两个时间的差值
		Duration differenceValue = Duration.between(ldt ,localTime );
		if("MIN".equals(type)){
			return differenceValue.toMinutes();
		}
		if("HOUR".equals(type)){
			return differenceValue.toHours();
		}
		if("SEC".equals(type)){
			return differenceValue.getSeconds();
		}
		if("DAY".equals(type)){
			return differenceValue.toDays();
		}
		return -1;
	}

	/**
	 * 获取指定时间时间增加指定秒数时间
	 * @param formate
	 * @return
	 */
	public static String getAddSecondsTime(String startTime,String formate,long time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		//Date date = new Date();
		Date date = new SimpleDateFormat(formate).parse(startTime);
		//long time = 60*1000;//60秒
		Date afterDate = new Date(date.getTime() + time);//60秒后的时间
		return sdf.format(afterDate);
	}

	/**
	 * 获取明天的日期字符串
	 * @return
	 */
	public static String tomorrowDateStr(String startTime,String formate){
		Date date = StrToDate(startTime,formate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		//把日期往后增加一天.整数往后推,负数往前移动(1:表示明天、-1：表示昨天，0：表示今天)
		calendar.add(Calendar.DATE,1);

		//这个时间就是日期往后推一天的结果
		date=calendar.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat(formate);
		String tomorrowStr = formatter.format(date);
		return tomorrowStr;
	}


	/***
	 * getGMTDate
	 * @description
	 * @author slfang
	 * @date 2021/9/23
	 * @return {@link String}
	 *
	 */
	public static String getGMTDate(){
		Date currentTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(currentTime);
	}


	public static Date StrToDate(String str,String formate) {

		SimpleDateFormat format = new SimpleDateFormat(formate);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String DateToStr(Date date,String formate) {
		SimpleDateFormat format = new SimpleDateFormat(formate);
		String str = format.format(date);
		return str;
	}

	public static int getDifferenceMonth(String start, String end) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String str1 = start.substring(0, 7);
		String str2 = end.substring(0, 7);
		Calendar bef = Calendar.getInstance();
		Calendar aft = Calendar.getInstance();

		try {
			bef.setTime(sdf.parse(str1));
			aft.setTime(sdf.parse(str2));
		} catch (ParseException var9) {
			var9.printStackTrace();
		}

		int result = aft.get(2) - bef.get(2);
		int month = (aft.get(1) - bef.get(1)) * 12;
		return Math.abs(month + result);
	}



	public static void main(String[] args) throws Exception {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(localDateTime.format(formatter));

		LocalDateTime localDateTimePass = localDateTime.minusMinutes(30);
		System.out.println(localDateTimePass.format(formatter));

	}

}
