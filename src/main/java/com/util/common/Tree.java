package com.util.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Tree {
	private List<Node> nodes = new LinkedList<Node>();
	private Node root = null;
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	public Node getRoot() {
		return root;
	}
	public void setRoot(Node root) {
		this.root = root;
	}
	public Tree(List<Node> nodes, Node root) {
		super();
		this.nodes = nodes;
		this.root = root;
	}
	
	public List<Node> build(){
		buildTree(root);
		List<Node> result = new ArrayList<Node>();
		result.add(root);
		return result;
	}
	private void buildTree(Node parent){
		Node node = null;
		for (int i = 0; i < nodes.size(); i++) {
			node = nodes.get(i);
			if (Objects.equals(node.getParentId(), parent.getId())) {
				parent.getChildren().add(node);
				buildTree(node);
				if (parent.isChecked()&&!node.isChecked()) {
					parent.setChecked(false);
				}
			}
		}
				
	}
}
