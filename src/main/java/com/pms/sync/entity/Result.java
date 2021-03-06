package com.pms.sync.entity;

public class Result<T> {

	public static String SUCCESS = "200";
	public static String ERROR = "500";
	public static String ERROR_API = "501";
	
	private String code;
	private String message;
	private T data;

	public Result(String code, String message) {
		this.code = code;
		this.message = message;
		this.data =null;
	}

	public Result(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
