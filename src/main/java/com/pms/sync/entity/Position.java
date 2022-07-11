package com.pms.sync.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Position {
	private String wid;
	private String positionNo;
	private String positionCode;
	private String positionName;
	private String parentPositionNo;
	private String positionNoPath;
	private String remark;
	private String positionType;
	private String nodeType;
	private int limitNum;
	private String userVp;
	private String uptFlag;
}
