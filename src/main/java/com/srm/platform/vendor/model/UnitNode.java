package com.srm.platform.vendor.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitNode {
	private Long id;
	private String text;
	private Long parentId;
	private List<UnitNode> children = new ArrayList<>();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<UnitNode> getChildren() {
		return children;
	}

	public void setChildren(List<UnitNode> children) {
		this.children = children;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UnitNode(Long id, String text, Long parentId) {
		this.id = id;
		this.text = text;
		this.parentId = parentId;
	}

	private UnitNode findParent(UnitNode node) {
		UnitNode parentNode = null;
		if (node.parentId == this.id) {
			parentNode = this;
		} else {
			for (int i = 0; i < this.children.size(); i++) {
				parentNode = children.get(i).findParent(node);
				if (parentNode != null)
					break;
			}
		}

		return parentNode;
	}

	public void addNode(UnitNode node) {
		UnitNode parent = findParent(node);
		if (parent != null) {
			parent.children.add(node);
		}
	}
}
