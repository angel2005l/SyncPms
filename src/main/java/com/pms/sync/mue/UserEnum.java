package com.pms.sync.mue;

public enum UserEnum {

	USER_NAME("userName","用户账号"),
	ACTUAL_NAME("actualName","用户名称"),
	SEX("sex","性别"),
	EMAIL("email","邮箱"),
	DISABLED("disabled","是否启用"),
	JOB_NUMBER("jobNumber","用户工号"),
	DEPART_NAME("departName","职位ID"),
	VP("vp","VP"),
	ONE_DEPARTMENT("oneDepartment","一级部门负责人"),
	TWO_DEPARTMENT("twoDepartment","二级部门负责人"),
	GRADE_LEVEL("gradeLevel","职级");
	
	private String code;
	private String text;

	private UserEnum(String code, String text) {
		this.code = code;
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
