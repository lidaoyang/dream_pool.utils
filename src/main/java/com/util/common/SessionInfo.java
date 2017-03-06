package com.util.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:SessionInfo模型
 * @itemName:
 */
public class SessionInfo {

	private String userId;
	private String loginName;
	private String ip;
	private String roleNames;
	private String roleIds;
	private String resourceIds;
	private String resourceNames;
	private List<String> resourceUrls = new ArrayList<String>();
	private String themes;//主题名称

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getResourceNames() {
		return resourceNames;
	}

	public void setResourceNames(String resourceNames) {
		this.resourceNames = resourceNames;
	}

	public List<String> getResourceUrls() {
		return resourceUrls;
	}

	public void setResourceUrls(List<String> resourceUrls) {
		this.resourceUrls = resourceUrls;
	}

	public String getThemes() 
	{
		return themes;
	}

	public void setThemes(String themes) 
	{
		this.themes = themes;
	}

}
