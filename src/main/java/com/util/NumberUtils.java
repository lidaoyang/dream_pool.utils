package com.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @description:数字工具类
 * @itemName:swell
 */
public class NumberUtils extends org.apache.commons.lang.math.NumberUtils {
	public static int parseInt(Object str) {
		String s = StrUtils.GetString(str);
		int result = 0;
		if (!s.equals(""))
			result = Integer.parseInt(s);
		return result;
	}
 
	public static long parseLong(Object str) {
		String s = StrUtils.GetString(str);
		long result = 0L;
		if (!s.equals(""))
			result = Long.parseLong(s);
		return result;
	}

	public static float parseFloat(Object str) {
		String s = StrUtils.GetString(str);
		float result = 0.000F;
		if (!s.equals(""))
			result = Float.parseFloat(s);
		return result;
	}

	public static double parseDouble(Object str) {
		String s = StrUtils.GetString(str);
		double result = 0.000D;
		if (!s.equals(""))
			result = Double.parseDouble(s);
		return result;
	}

	public static String formatNumber(double value, int digitNum) {
		String str = "0.000";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digitNum; i++) {
			sb.append("0");
		}
		DecimalFormat nf = new DecimalFormat("#0." + sb.toString());
		nf.setParseIntegerOnly(false);
		nf.setDecimalSeparatorAlwaysShown(false);
		str = nf.format(value);
		if (StrUtils.isEmpty(str.split("\\.")[0]))
			str = "0" + str;
		return str;
	}
	/**
	 * 
	 * @param code
	 * @param 保留num的位数,num 代表长度   
	 * 0 代表前面补充0     
	 * d 代表参数为正数型 
	 * @return
	 */
	public static String formatNumber(String code, int num) {
        // 保留num的位数
		// 0 代表前面补充0     
        // num 代表长度    
        // d 代表参数为正数型 
		String result = String.format("%0" + num + "d", Integer.parseInt(code));

        return result;
	}
	public static double formatDouble(double value,int digitNum){
		  BigDecimal doublevalue = new BigDecimal(value); 
		  return doublevalue.setScale(digitNum, BigDecimal.ROUND_HALF_UP).doubleValue();
	  }
	public static String numUnitConver(double num) {
		DecimalFormat df = new DecimalFormat("#0.00");
		if(num>1000000000000L||num<-1000000000000L){
			num = num/1000000000000L;
			return df.format(num)+"万亿";
		}else if(num>100000000||num<-100000000){
			num = num/100000000;
			return df.format(num)+"亿";
		}else if(num>10000||num<-10000){
			num = num/10000;
			return df.format(num)+"万";
		}
		return df.format(num);
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	public static boolean isNum(String str) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	public static void main(String[] arg) {
		System.out.println(formatNumber("150", 5));
	}
}
