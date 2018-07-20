package com.srm.platform.vendor.utility;

public class GenericJsonResponse<T> {

	public final static int SUCCESS = 1;
	public final static int FAILED = 0;

	private int success;
	private String errmsg;
	private T data;

	public GenericJsonResponse(int success, String errmsg, T data) {
		this.success = success;
		this.errmsg = errmsg;
		this.data = data;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
