package com.util.common;

import java.io.Serializable;

/**
 * @description:json模型
 * @author
 *
 */
public class Json implements Serializable {
	private static final long serialVersionUID = -1588738714395395372L;
	private boolean success = false;// 是否成功
	private String state = "";// 是否成功
	private String msg = "";// 提示信息
	private String msg_desc = "";// 中文提示信息
	private Object result = null;// 其他信息
	private int num = 0;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getMsg_desc() {
		return msg_desc;
	}

	public void setMsg_desc(String msg_desc) {
		this.msg_desc = msg_desc;
	}

}
