package com.pms.sync.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Depart {
	
	private String wid;
	private String deptNo;
	private String deptCode;
	private String deptName;
	private String parentDeptNo;
	private String deptNoPath;
	private String remark;
	private String deptType;
	private String nodeType;
	private int limitNum;
	private String userVp;
	private String uptFlag;
	
}
