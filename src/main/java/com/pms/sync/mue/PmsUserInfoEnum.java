package com.pms.sync.mue;

public enum PmsUserInfoEnum {
	JOB_NUMBER("job_number", "工号"),
	NAME("name", "姓名"),
	EMP_STATUS("emp_status", "员工当前状态描述"),
	GRADE_LEVEL("grade_level", "职级");
	
	private String code;
	private String text;

	private PmsUserInfoEnum(String code, String text) {
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
