package com.pms.sync.mue;

public enum DepartEnum {

	
	DEPT_CODE("deptCode","部门编码"),
	DEPT_NAME("deptName","组织名称"),
	DEPT_STATUS("deptStatus","状态描述"),
	PARENT_DEPT_NO("parentDeptNo","上级组织ID"),
	USER_VP("userVp","组织负责人工号");
	
	private String code;
	private String text;

	private DepartEnum(String code, String text) {
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
