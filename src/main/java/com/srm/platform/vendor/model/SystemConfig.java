package com.srm.platform.vendor.model;

import java.io.Serializable;

public class SystemConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8869620719383949484L;
	
	public String minpassword;
	
	public String mailhost;
	
	public String mailuser;
	
	public String mailpassword;
	
	public String sessiontimeout;
	
	public String smshost;
	
	public String smsuser;
	
	public String smspassword;
	
	public SystemConfig() {
	}
}
