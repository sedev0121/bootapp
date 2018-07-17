package com.srm.platform.vendor.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "action")
public class Action {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Transient
	private boolean available = false;

	@Transient
	private Long functionActionId;

	public Long getFunctionActionId() {
		return functionActionId;
	}

	public void setFunctionActionId(Long functionActionId) {
		this.functionActionId = functionActionId;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Action() {

	}

	public static Action clone(Action action) {
		Action clone = new Action();
		clone.id = action.id;
		clone.name = action.name;
		clone.available = action.available;
		clone.functionActionId = action.functionActionId;
		return clone;
	}

	public Action(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
