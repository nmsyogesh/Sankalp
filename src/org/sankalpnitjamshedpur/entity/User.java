package org.sankalpnitjamshedpur.entity;

public class User {
	private String name;
	private String rollNo;
	private String emailId;
	private int batch;
	private String password;
	private long mobileNo;
	private String volunteerId;	

	public String getVolunteerId() {
		return volunteerId;
	}

	public void setVolunteerId(String volunteerId) {
		this.volunteerId = volunteerId;
	}

	public User(String name, String rollNo, String emailId, int batch,
			String password, long mobileNo) {
		this.mobileNo = mobileNo;
		this.name = name;
		this.rollNo = rollNo;
		this.emailId = emailId;
		this.batch = batch;
		this.password = password;
		this.volunteerId = String.format("%d%s", batch, rollNo);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

}
