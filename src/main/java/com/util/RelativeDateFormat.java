package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RelativeDateFormat {
	 private static final long ONE_MINUTE = 60000L;
	    private static final long ONE_HOUR = 3600000L;
	    private static final long ONE_DAY = 86400000L;
	    //private static final long ONE_WEEK = 604800000L;

	    private static final String ONE_SECOND_AGO = "秒前";
	    private static final String ONE_MINUTE_AGO = "分钟前";
	    private static final String ONE_HOUR_AGO = "小时前";
	    private static final String ONE_DAY_AGO = "天前";
	    //private static final String ONE_MONTH_AGO = "月前";
	    //private static final String ONE_YEAR_AGO = "年前";

	    public static void main(String[] args) throws ParseException {
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
	        System.out.println(format(format.parse("2016-01-14 10:14:49")));
	    }

	    public static String format(Date date) {
	        long delta = new Date().getTime() - date.getTime();
	        if (delta < 1L * ONE_MINUTE) {
	            long seconds = toSeconds(delta);
	            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
	        }
	        if (delta < 45L * ONE_MINUTE) {
	            long minutes = toMinutes(delta);
	            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
	        }
	        if (delta < 24L * ONE_HOUR) {
	            long hours = toHours(delta);
	            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
	        }
	        Calendar cl = Calendar.getInstance();   
	        cl.add(Calendar.DATE, -1);
	        String now_date = DateUtils.DateToStr(cl.getTime(), "yyyy-MM-dd");
	        Date start_d = DateUtils.StrToDate(now_date+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
	        Date end_d = DateUtils.StrToDate(now_date+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
	        if (date.getTime()>start_d.getTime()&&date.getTime()<end_d.getTime()) {
	        	SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	            return "昨天 "+format.format(date);
	        }
	        SimpleDateFormat format = new SimpleDateFormat("yy-M-d HH:mm");
        	return format.format(date);
        	
        	/*if (delta < 30L * ONE_DAY) {
	            long days = toDays(delta);
	            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
	        } else {
	        	SimpleDateFormat format = new SimpleDateFormat("yy-M-d HH:mm");
	        	return format.format(date);
			}
	        if (delta < 12L * 4L * ONE_WEEK) {
	            long months = toMonths(delta);
	            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
	        } else {
	            long years = toYears(delta);
	            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
	        }*/
	    }

	    private static long toSeconds(long date) {
	        return date / 1000L;
	    }

	    private static long toMinutes(long date) {
	        return toSeconds(date) / 60L;
	    }

	    private static long toHours(long date) {
	        return toMinutes(date) / 60L;
	    }

	    private static long toDays(long date) {
	        return toHours(date) / 24L;
	    }

	    /*private static long toMonths(long date) {
	        return toDays(date) / 30L;
	    }

	    private static long toYears(long date) {
	        return toMonths(date) / 365L;
	    }*/

}
