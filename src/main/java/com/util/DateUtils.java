package com.util;

import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * @description:日期工具类
 * @itemName:swell
 */
public class DateUtils {
	public static String nowDate() {
		long now = System.currentTimeMillis();
		return String.format("%tF", now);
	}

	public static String nowDate(String sper) {
		long now = System.currentTimeMillis();
		String s = String.format("%tF", now);
		String result = s.replaceAll("-", sper);
		return result;
	}

	public static String nowChDate() {
		GregorianCalendar Time = new GregorianCalendar();
		int nowY = Time.get(1);
		int nowM = Time.get(2) + 1;
		int nowD = Time.get(5);

		return nowY + "年" + nowM + "月" + nowD + "日";
	}

	public static String nowTime() {
		long now = System.currentTimeMillis();
		return String.format("%tT", now);
	}

	public static String nowTime(String sperate) {
		String s_time = nowTime();
		if (StrUtils.isNotEmpty(sperate)) {
			s_time = s_time.replaceAll(":", sperate);
		}
		return s_time;
	}

	public static String nowChTime() {
		String time = nowTime();
		String[] arr = time.split(":");

		return arr[0] + "时" + arr[1] + "分" + arr[2] + "秒";
	}

	public static String nowDateTime() {
		String s_DateTime = "";
		s_DateTime = nowDateTime("-", " ", ":");
		return s_DateTime;
	}

	public static String nowDateTime(String dateSpe, String midSpe,
			String timeSpe) {
		String s_DateTime = "";
		if (midSpe == null)
			midSpe = " ";
		s_DateTime = nowDate(dateSpe) + midSpe + nowTime(timeSpe);

		return s_DateTime;
	}

	public static String nowChDateTime() {
		String s_DateTime = "";
		s_DateTime = nowChDate() + " " + nowChTime();
		return s_DateTime;
	}

	public static String getNextDateOfDays(String date, int days)
			throws ParseException {
		date = formatDate(date, "/");
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");// 设置日期格式
		Date dt = format.parse(date);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(dt);
		cal.add(5, days);
		int nowY = cal.get(1);
		int nowM = cal.get(2) + 1;
		int nowD = cal.get(5);
		return nowY + StrUtils.leftPad(nowM, 2, '0')
				+ StrUtils.leftPad(nowD, 2, '0');
	}

	public static String getPreDateOfDays(String date, int days)
			throws ParseException {
		return getNextDateOfDays(date, -1 * days);
	}

	public static String formatDateTime(String dateTime) {
		return formatDateTime(dateTime, "-", " ", ":");
	}

	public static String formatDateTime(String dateTime, String dateSpe,
			String midSpe, String timeSpe) {
		String s = dateTime;
		if ((StrUtils.isNotEmpty(dateTime)) && (dateTime.length() >= 14)) {
			if (dateSpe == null)
				dateSpe = "-";
			else
				dateSpe = dateSpe.trim();
			if (midSpe == null)
				midSpe = " ";
			if (timeSpe == null)
				timeSpe = ":";
			else {
				timeSpe = timeSpe.trim();
			}
			s = dateTime.substring(0, 4) + dateSpe + dateTime.substring(4, 6)
					+ dateSpe + dateTime.substring(6, 8) + midSpe
					+ dateTime.substring(8, 10) + timeSpe
					+ dateTime.substring(10, 12) + timeSpe
					+ dateTime.substring(12, 14);

			if (dateTime.length() == 17) {
				s = s + timeSpe + dateTime.substring(14, 17);
			}
		}

		return s;
	}

	public static String formatChDateTime(String dateTime) {
		String s = "";
		if ((StrUtils.isNotEmpty(dateTime)) && (dateTime.length() >= 14)) {
			s = dateTime.substring(0, 4) + "年" + dateTime.substring(4, 6) + "月"
					+ dateTime.substring(6, 8) + "日 "
					+ dateTime.substring(8, 10) + "时"
					+ dateTime.substring(10, 12) + "分"
					+ dateTime.substring(12, 14) + "秒";

			if (dateTime.length() == 17) {
				s = s + dateTime.substring(14, 17) + "毫秒";
			}
		}
		return s;
	}

	public static String formatDate(String date) {
		return formatDate(date, "-");
	}

	public static String formatDate(String date, String dateSpe) {
		String s = date;
		if ((StrUtils.isNotEmpty(date)) && (date.length() == 8)) {
			if (dateSpe == null)
				dateSpe = "-";
			else
				dateSpe = dateSpe.trim();
			s = date.substring(0, 4) + dateSpe + date.substring(4, 6) + dateSpe
					+ date.substring(6, 8);
		}
		return s;
	}

	public static String formatChDate(String date) {
		String s = date;
		if ((StrUtils.isNotEmpty(date)) && (date.length() == 8)) {
			s = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";
		}
		return s;
	}

	public static String formatTime(String time) {
		return formatTime(time, ":");
	}

	public static String formatTime(String time, String timeSpe) {
		String s = time;
		if ((StrUtils.isNotEmpty(time)) && (time.length() >= 6)) {
			if (timeSpe == null)
				timeSpe = ":";
			else
				timeSpe = timeSpe.trim();
			s = time.substring(0, 2) + timeSpe + time.substring(2, 4) + timeSpe
					+ time.substring(4, 6);

			if (time.length() == 9) {
				s = s + timeSpe + time.substring(6, 9);
			}
		}
		return s;
	}

	public static String formatChTime(String time) {
		String s = time;
		if ((StrUtils.isNotEmpty(time)) && (time.length() >= 6)) {
			s = time.substring(0, 2) + "时" + time.substring(2, 4) + "分"
					+ time.substring(4, 6) + "秒";

			if (time.length() == 9) {
				s = s + time.substring(6, 9) + "毫秒";
			}
		}
		return s;
	}

	public static String normalizeDate(String date) {
		String s = date;

		if ((StrUtils.isNotEmpty(date)) && (date.length() != 8)) {
			s = date.replaceAll("-", "");
		}

		return s;
	}

	public static String normalizeDateTime(String dateTime) {
		String s = dateTime;

		if ((StrUtils.isNotEmpty(dateTime)) && (dateTime.length() != 14)) {
			s = dateTime.replaceAll("-", "");
			s = s.replaceAll(":", "");
			s = s.replaceAll(" ", "");
		}

		return s;
	}

	public static String formatDateTime(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df1.format(date);
	}

	public static double getNumberOfDaysTimes(String fromDate, String toDate) {
		String strFromDate = "";
		String strToDate = "";

		if (fromDate.length() == 8)
			strFromDate = formatDate(fromDate, "/");
		else if (fromDate.length() == 14)
			strFromDate = formatDateTime(fromDate, "/", " ", ":");

		if (toDate.length() == 8)
			strToDate = formatDate(toDate, "/");
		else if (toDate.length() == 14)
			strToDate = formatDateTime(toDate, "/", " ", ":");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date dt1 = null;
		Date dt2 = null;
		try {
			dt1 = df1.parse(strToDate);
			dt2 = df1.parse(strFromDate);
		} catch (ParseException e) {
		}
		double days = dt1.getTime() - dt2.getTime();
		return days / 60.0D / 60.0D / 1000.0D / 24.0D;
	}

	public static int getNumberOfDays(String fromDate, String toDate) {
		double days = getNumberOfDaysTimes(fromDate, toDate);

		return (int) days;
	}

	public static double getDecimalDays(double days, int workHours) {
		double result = 0.0D;

		if ((int) days == days)
			return days;

		double hours = workHours / 48.0D;
		int idays = (int) days;
		double d = idays + hours;
		if (days >= d)
			result = idays + 1.0D;
		else
			result = idays + 0.5D;
		return result;
	}

	public static Timestamp nowTimestamp() {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date11 = new Date();
		String time = df1.format(date11);
		Timestamp ts = Timestamp.valueOf(time);
		return ts;
	}

	public static Timestamp getTimestamp(String StrDate) {
		String tss[] = StrDate.split(".");
		String t = "";
		String s = "";
		if (tss.length > 0) {
			t = tss[0];
			s = tss[1];
		} else {
			t = StrDate;
		}
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		Timestamp ts = null;
		try {
			d = df1.parse(t);
			String time = df1.format(d);
			ts = Timestamp.valueOf(time + s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts;
	}
	// 指定日期加减分钟
		public static Date getDateMinute(Date date, Integer minute) {
			// yyyy-MM-dd HH:mm:ss
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MINUTE, minute);
			return calendar.getTime();
		}
	// 指定日期加减小时
	public static Date getDateHour(Date date, Integer hours) {
		// yyyy-MM-dd HH:mm:ss
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, hours);
		return calendar.getTime();
	}
	// 指定日期加减天数
	public static Date GetDateDiff(Date date, Integer days) {
		// yyyy-MM-dd HH:mm:ss
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	// 指定日期加减天数
	public static String GetDateDiffToStr(Date date, Integer days) {
		// yyyy-MM-dd HH:mm:ss
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		return format.format(calendar.getTime());
	}

	/**
	 * 日期转换成字符串
	 * @param	date 日期
	 * @param Format 日期格式 为空时，默认未yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String DateToStr(Date date, String Format) {
		// yyyy-MM-dd HH:mm:ss
		if (StrUtils.GetString(Format).equals("")) {
			Format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat format = new SimpleDateFormat(Format);
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * @param	str 日期字符串
	 * @param Format 日期格式 为空时，默认未yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date StrToDate(String str, String Format) {
		if (StrUtils.GetString(Format).equals("")) {
			Format = "yyyy-MM-dd HH:mm:ss";
		}
		str = StrUtils.isEmpty(str) ? nowDate() : str;
		SimpleDateFormat format = new SimpleDateFormat(Format);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 根据日期获得星期
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekCh() {
		long now = System.currentTimeMillis();
		return String.format("%tA", now);
	}

	public static String getDateTime(int hours, String flag) {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		if (StrUtils.isEmpty(flag) || "before".equalsIgnoreCase(flag)) {
			c.add(Calendar.HOUR_OF_DAY, -1 * hours);// hours小时前
		} else {
			c.add(Calendar.HOUR_OF_DAY, 1 * hours);// hours小时后
		}
		return df1.format(c.getTime());
	}

	/**
	 * 把毫秒转化成时分秒(格式 00:00:25.341)
	 * 
	 * @return
	 */
	public static String getHMS(long millisecond) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;

		long hour = (millisecond) / hh;
		long minute = (millisecond - hour * hh) / mi;
		long second = (millisecond - hour * hh - minute * mi) / ss;
		long millis = millisecond - hour * hh - minute * mi - second * ss;

		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;

		String hms = strHour + ":" + strMinute + ":" + strSecond + "." + millis;

		return hms;
	}

	/**
	 * 获取输入时间的月初时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateOfMonthFirst(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 获取输入时间的月初时间--字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getStrOfMonthFirst(Date date, String formatStr) {
		return DateToStr(getDateOfMonthFirst(date),
				StringUtils.isBlank(formatStr) ? "yyyy-MM-dd" : formatStr);
	}

	/**
	 * 获取输入时间的月末时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateOfMonthLast(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	/**
	 * 获取输入时间的月末时间--字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getStrOfMonthLast(Date date, String formatStr) {
		return DateToStr(getDateOfMonthLast(date),
				StringUtils.isBlank(formatStr) ? "yyyy-MM-dd" : formatStr);
	}

	/**
	 * 返回日期添加后的日期结果
	 * 
	 * @param date
	 * @param num
	 *            增加的数量
	 * @param type
	 *            日期增加的类型
	 * @return
	 */
	public static Date getDateAddWithType(Date date, int type, int num) {
		if (null == date)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(type, num);
		return c.getTime();
	}

	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) {
			c.setTime(date);
		}
		return c.get(Calendar.YEAR);
	}

	/**
	 * 功能：判断是否是周一
	 */
	public static boolean isMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		if (week == 2) {
			return true;
		}
		return false;
	}
	/**
	 * 功能：判断是否是周六
	 */
	public static boolean isSaturday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		if (week == 7) {
			return true;
		}
		return false;
	}
	/**
	 * 功能：判断是否是周日
	 */
	public static boolean isSunday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		if (week == 1) {
			return true;
		}
		return false;
	}
	/**
	 * 功能：获取本周的开始时间
	 */
	public static Date getWeekStart(Date date) {// 当周开始时间
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(date);
		currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return (Date) currentDate.getTime();
	}

	/**
	 * 功能：获取本周的结束时间,（本周六）
	 */
	public static Date getWeekEnd(Date date) {// 当周结束时间

		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(date);
		currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		return (Date) currentDate.getTime();
	}
	/**
	 * 功能：获取指定时间是周几
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	/**
	 * 功能：获取指定日期月的某一天
	 */
	public static int getDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 功能：获取某个月的最后一天
	 */
	public static Date getMaxDayOfMonth(Date date) {
		String strdate = DateUtils.DateToStr(date, "yyyy-MM");
		Date datemonth = DateUtils.StrToDate(strdate, "yyyy-MM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(datemonth);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));// 获取某个月的最后一天
		date = cal.getTime();
		return date;
	}
	
	/**
	 * 功能：获取美股时间
	 * 返回Date
	 */
	public static Date getUSDate(Date date) {
		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfe.setTimeZone(TimeZone.getTimeZone("America/New_York"));//转换美国纽约时间
		Date us_date = DateUtils.StrToDate(sdfe.format(date), "yyyy-MM-dd HH:mm:ss");
		return us_date;
	}
	/**
	 * 功能：获取美股时间
	 * 返回StrDate
	 */
	public static String getUSDateStr(Date date) {
		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfe.setTimeZone(TimeZone.getTimeZone("America/New_York"));//转换美国纽约时间
		return sdfe.format(date);
	}

	/**
	 * 功能：获取指定日期一年中的第几周
	 */
	public static int getWeekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week_count = cal.get(Calendar.WEEK_OF_YEAR);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		int last_week = cal.get(Calendar.WEEK_OF_YEAR);
		if (week_count < last_week) {
			week_count = last_week + 1;
		}
		return week_count;
	}

	/**
	 * 功能：判断是否是今天
	 */
	public static boolean isToday(String date) {
		String strnow = DateToStr(new Date(), "yyyy-MM-dd");
		if (strnow.equals(date)) {
			return true;
		}
		return false;
	}
	/**
	 * 功能：计算2个日期的时间差
	 */
	public static int daysBetween(Date smdate,Date bdate) {    
		String format = "yyyy-MM-dd";
        String str_d1=DateUtils.DateToStr(smdate, format);
        String str_d2=DateUtils.DateToStr(bdate, format);
        smdate = DateUtils.StrToDate(str_d1, format);
        bdate = DateUtils.StrToDate(str_d2, format);
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }  
	/**
	 * 功能：把北京时间转换为纽约时间
	 */
	public static Date convertTimeZoneToUS(Date date) {    
		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfe.setTimeZone(TimeZone.getTimeZone("America/New_York"));//转换美国纽约时间
		Date us_date = DateUtils.StrToDate(sdfe.format(date), "yyyy-MM-dd HH:mm:ss");
            
       return us_date;           
    }  
	/**
	 * 功能：把北京时间转换为纽约时间
	 */
	public static String convertTimeZoneToStrUS(Date date) {    
		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfe.setTimeZone(TimeZone.getTimeZone("America/New_York"));//转换美国纽约时间
       return sdfe.format(date);           
    }
	/**
	 * 功能：把北京时间转换为纽约时间
	 */
	public static Date TimeZoneToUS(Date date) {    
		TimeZone srcTimeZone = TimeZone.getTimeZone("GMT+8");    
        TimeZone destTimeZone = TimeZone.getTimeZone("America/New_York");    
     	Date cdate = dateTransformBetweenTimeZone(date, srcTimeZone, destTimeZone);
       return cdate;           
    }  
	/**
	 * 功能：把北京时间转换为纽约时间
	 */
	public static String TimeZoneToStrUS(Date date) {    
		TimeZone srcTimeZone = TimeZone.getTimeZone("GMT+8");    
        TimeZone destTimeZone = TimeZone.getTimeZone("America/New_York");    
     	Date cdate = dateTransformBetweenTimeZone(date, srcTimeZone, destTimeZone);
     	return DateToStr(cdate, "yyyy-MM-dd HH:mm:ss");    
    }
	/**
	 * 功能：把纽约时间转换为北京时间
	 */
	public static Date convertTimeZoneToCH(Date date) {    
		TimeZone srcTimeZone = TimeZone.getTimeZone("America/New_York");    
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");    
     	Date cdate = dateTransformBetweenTimeZone(date, srcTimeZone, destTimeZone);
       return cdate;           
    }  
	/**
	 * 功能：把纽约时间转换为北京时间
	 */
	public static String convertTimeZoneToStrCH(Date date) {   
		TimeZone srcTimeZone = TimeZone.getTimeZone("America/New_York");    
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");    
     	Date cdate = dateTransformBetweenTimeZone(date, srcTimeZone, destTimeZone);
       return DateToStr(cdate, "yyyy-MM-dd HH:mm:ss");           
    }
	/**  
     * 获取更改时区后的日期  
     * @param date 日期  
     * @param sourceTimeZone 旧时区对象  
     * @param targetTimeZone 新时区对象  
     * @return 日期  
     */    
	private static Date dateTransformBetweenTimeZone(Date sourceDate,TimeZone sourceTimeZone,TimeZone targetTimeZone) {
		Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset()+ targetTimeZone.getRawOffset();
		return new Date(targetTime);
	}
	/**
	 * 功能：获取网络时间
	 * 如果失败，返回本地时间
	 */
	public static String getNetDate() {
		URLConnection uc = null;
		try {
			URL url=new URL("http://www.beijing-time.org");//取得资源对象  
	         uc = url.openConnection();// 生成连接对象
	         uc.connect();// 发出连接
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		 Date date = new Date();// 本地时间
		if (uc!=null) {
			long ld = uc.getDate();// 读取网站日期时间
	        date = new Date(ld);// 转换为标准时间对象
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 输出北京时间
		return sdf.format(date);
	}

	public static void main(String[] args) {
       System.out.println(daysBetween(StrToDate("2017-02-18", "yyyy-MM-dd"),StrToDate("2017-02-20", "yyyy-MM-dd")));
		
	}
}