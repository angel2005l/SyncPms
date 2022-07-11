package com.pms.sync.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
	private String wid;
	private String userId;
	private String userName;
	private String actualName;
	private String password;
	private String sex;
	private String email;
	private int disabled;
	private String  isDomain;
	private String jobNumber;
	private String departName;
	private String vp;
	private String oneDepartment;
	private String twoDepartment;
	private String gradeLevel;
	private String uptFlag;//Y/N 
	
}
