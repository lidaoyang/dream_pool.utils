/**
 * Copyright (c) HANGZHOU EWELL TECHNOLOGY Co., LTD.
 * 8 Feng Qing Road, Liangzhu Culture Zone, Hangzhou
 * All rights reserved.
 *
 * "[Description of code or deliverable as appropriate] is the copyrighted,
 * proprietary property of HANGZHOU EWELL TECHNOLOGY Co., LTD. and its 
 * subsidiaries and affiliates which retain all right and title."
 * 
 * Revision History
 *
 * Date            Programmer              Notes
 * ---------    ---------------------  --------------------------------------------
 * 2012-7-9	       Administrator              Initial
 */
package com.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 单号生成器
 */
public class CodeGenerateUtil {

	private static Map<String, String> codeMap = new HashMap<String, String>();
	
	
	/**
	 * 初始化某一类型的单号
	 * 
	 * @param codePrefix
	 *            单号前缀，用来标识某一类型单号
	 * @param code
	 *            初始化单号
	 */
	public static void initial(String codePrefix, String code) {

		codeMap.put(codePrefix, "".equals(code)?null:code);
	}

	/**
	 * 生成某一类型唯一单号
	 * 
	 * @param codePrefix 单号前缀
	 * @return
	 * @throws Exception 
	 */
	synchronized public static String generateCode(String codePrefix) {

		boolean isfa=codePrefix.startsWith("FA");
		String code = codeMap.get(isfa?"FA":codePrefix);
		String result = null;
		String today = DateUtils.nowDate("");
		if (code == null || !code.contains(today)) {
			if (isfa) {
				codePrefix=codePrefix.substring(3);
				try {
					result=createSerialCode(codePrefix, null, 1);
					codePrefix="FA";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				result = codePrefix + today + "001";	
			}
		}
		else {
			if (isfa) {
				codePrefix=codePrefix.substring(3);
				try {
					int length = today.length();
					int num=Integer.valueOf(code.substring(length));
					result = createSerialCode(codePrefix, null, num+1);
					codePrefix="FA";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				int length = (codePrefix + today).length();
				result = codePrefix + today + increase(code.substring(length), (code.length() - length));
			}
		}
		codeMap.put(codePrefix, result);

		return result;
	}

	private static String increase(String str, int width) {

		int number = Integer.parseInt(str);

		int num = ++number;

		return enoughZero(num, width);
	}

	private static String enoughZero(long num, int len) {

		String fm="";
		for (int i = 0; i < len; i++) {
			fm+="0";
		}
		DecimalFormat df = new DecimalFormat(fm);
		String tm = df.format(num);
		return tm;
	}
	
	/**
	 * @throws Exception
	 * @描述 获得卡片编号 单据号码格式: CUSTOM{-},APP_ID#N,STORE_ID#N,ACC_ID1#N,ACC_ID2#N,DATE#N[yyyyMMdd],NO#N:流水号(N:几位)， 以上任意组合，以"|"分割。【例:STORE_ID|CUSTOM{-}|ACC_ID1#1|DATE#8[yyyyMMdd]】
	 */
	public static String createSerialCode(String serialcode,Map<String,Object> map, long number) throws Exception{
		if (StringUtils.isNotBlank(serialcode)) {
			// 如果不带# 那么是CUSTOM，获得{}中间的内容
			// 带#，判断是否是NO 是 取自动增长值里的内容 ， 不够位数 前面补0
			// 否 判断是否是DATE 是的话 按照[]里面的格式取则取
			// 都不是 前面的字段名 后面是长度
			// 按顺序拼字符串 再返回
			String[] codetemp = serialcode.split("\\|");
			String returncode = "";
			int length = 0;
			for (int i = 0; i < codetemp.length; i++) {
				if (codetemp[i].contains("#")) {
					if ("NO#".equalsIgnoreCase(codetemp[i].substring(0, 3))) {
						// NO型
						length = Integer.parseInt(codetemp[i].substring(3, codetemp[i].length()));
						returncode += enoughZero(number, length);;
					} else if ("DATE#".equalsIgnoreCase(codetemp[i].substring(0, 5))) {
						// DATE型
						SimpleDateFormat df = new SimpleDateFormat(codetemp[i].substring(codetemp[i].indexOf("[") + 1, codetemp[i].indexOf("]")));
						String sdate = df.format(new Date());
						length = Integer.parseInt(codetemp[i].substring(codetemp[i].indexOf("#") + 1, codetemp[i].indexOf("[")));
						returncode += sdate.substring(0, length);

					} else {
						// 字段型
						String[] temp = codetemp[i].split("#");  
						Integer leng = Integer.parseInt(temp[1]);
						if(null == map.get(temp[0]) || map.get(temp[0]).toString().length() < leng){
							throw new Exception("字段:"+temp[0] +" 值不够 "+Integer.parseInt(temp[1])+" 位");
						}
						returncode += map.get(temp[0]).toString().substring(0, Integer.parseInt(temp[1]));
					}
				} else {
					// CUSTOM型
					returncode += codetemp[i].substring(codetemp[i].indexOf("{"), codetemp[i].indexOf("}"));
				}
			}
			return returncode;
		} else {
			return "";
		}
	}
	
}