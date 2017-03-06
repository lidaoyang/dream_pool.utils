package com.util.common;

public class NodeAttribute {

	private String id;
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public NodeAttribute(String id, String url) {
		super();
		this.id = id;
		this.url = url;
	}

}
