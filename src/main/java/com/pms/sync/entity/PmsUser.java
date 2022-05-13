package com.pms.sync.entity;

public class PmsUser {
	private String wid;
	private String userCode;
	private String userName;
	private String status;
	private String jobLev;

	/*
	 * 组织架构，事业部名称，单元名称 暂考虑
	 */
	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJobLev() {
		return jobLev;
	}

	public void setJobLev(String jobLev) {
		this.jobLev = jobLev;
	}

}
