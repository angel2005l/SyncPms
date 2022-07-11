package com.pms.sync.mue;

public enum PositionEnum {

	
	POSITION_CODE("positionCode","职位编码"),
	POSITION_NAME("positionName","职位名称"),
	PARENT_POSITION_NO("parentPositionNo","上级职位ID"),
	USER_VP("userVp","职位负责人工号");
	
	private String code;
	private String text;

	private PositionEnum(String code, String text) {
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
