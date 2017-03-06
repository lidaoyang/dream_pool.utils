package com.util.common;

import java.util.LinkedList;
import java.util.List;

public class Node {
	private String id;
	private String parentId;
	private String text;
	private String state;
	private boolean checked;
	private NodeAttribute attributes;
	private List<Node> children = new LinkedList<Node>();
	private String order;

	public Node(String id, String parentId, String text, String state,
			boolean checked, NodeAttribute attributes, String order) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.text = text;
		this.state = state;
		this.checked = checked;
		this.attributes = attributes;
		this.order = order;
	}

	public Node(String id, String parentId, String text, String state,
			boolean checked, NodeAttribute attributes, List<Node> children,
			String order) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.text = text;
		this.state = state;
		this.checked = checked;
		this.attributes = attributes;
		this.children = children;
		this.order = order;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public NodeAttribute getAttributes() {
		return attributes;
	}

	public void setAttributes(NodeAttribute attributes) {
		this.attributes = attributes;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
