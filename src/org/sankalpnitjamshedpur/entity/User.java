package org.sankalpnitjamshedpur.entity;

public class User {
	private String name;
	private int rollNo;
	private String emailId;
	private int batch;
	private String branch;
	private long mobileNo;
	private String volunteerId;	
	private String securityToken;	


	public User(String name, int rollNo, String emailId, int batch, String branch,
		long mobileNo, String volunteerId, String securityToken) {
		this.branch = branch.toUpperCase();
		this.mobileNo = mobileNo;
		this.name = name;
		this.rollNo = rollNo;
		this.emailId = emailId.toUpperCase();
		this.batch = batch;
		this.volunteerId = (volunteerId!=null)?volunteerId : String.format("%d%s%03d", batch, branch, rollNo);
		this.securityToken = securityToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRollNo() {
		return rollNo;
	}

	public void setRollNo(int rollNo) {
		this.rollNo = rollNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public String getVolunteerId() {
		return volunteerId;
	}

	public void setVolunteerId(String volunteerId) {
		this.volunteerId = volunteerId;
	}
}
