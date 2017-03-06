package com.util;

public class MobileUtil {
	public static String getDeviceType(String userAgent){
		String device = "";
		if(userAgent!=null){
			if(userAgent.indexOf("Trident")>-1)device = "trident";//IE内核
			if(userAgent.indexOf("Presto")>-1)device = "presto";//opera内核
			if(userAgent.indexOf("AppleWebKit")>-1)device = "webKit";//苹果、谷歌内核
			if(userAgent.indexOf("Gecko")>-1 && userAgent.indexOf("KHTML") == -1)device = "gecko";//火狐内核
			if(userAgent.matches("/AppleWebKit.*Mobile.*/") || userAgent.matches("/AppleWebKit/"))device = "mobile";
			if(userAgent.matches("/\\(i[^;]+;( U;)? CPU.+Mac OS X/"))device = "ios";//ios终端
			if(userAgent.indexOf("Android")>-1 || userAgent.indexOf("Linux") > -1)device = "android";//android终端或者uc浏览器
			if(userAgent.indexOf("iPhone") > -1 || userAgent.indexOf("Mac") > -1)device = "iPhone";//是否为iPhone或者QQHD浏览器
			if(userAgent.indexOf("iPad")>-1)device = "iPad";//是否iPad
			//if(userAgent.indexOf("Safari")>-1)device = "webApp";//是否web应该程序，没有头部与底部
		}else{
			device = "Android";
		}
		
		return device;
	}
}
