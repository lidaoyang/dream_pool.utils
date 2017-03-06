package com.util;

import java.util.ResourceBundle;

/**
 * @description:项目参数工具
 * @itemName:gyl
 */
public class ResourceUtil {

	private static final ResourceBundle bundle = java.util.ResourceBundle
			.getBundle("common");

	/**
	 * 获得sessionInfo名字
	 * 
	 * @return
	 */
	public static final String getSessionInfoName() {
		return bundle.getString("sessionInfoName");
	}

	/**
	 * 获取主题名称
	 * @return
	 */
	public static final String getThemeName() {
		return bundle.getString("themeName");
	}
	
	/**
	 * 得到系统变量
	 * @param key
	 * @return
	 */
	public static final String getValue(String key){
		return bundle.getString(key);
	}
}
