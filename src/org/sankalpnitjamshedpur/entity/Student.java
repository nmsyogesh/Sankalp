package org.sankalpnitjamshedpur.entity;

public class Student {
	private String studentId;
	private String studentname;
	private int rollNo;
	public Student(String studentId, String studentname, int rollNo) {
		super();
		this.studentId = studentId;
		this.studentname = studentname;
		this.rollNo = rollNo;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStudentname() {
		return studentname;
	}
	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}
	public int getRollNo() {
		return rollNo;
	}
	public void setRollNo(int rollNo) {
		this.rollNo = rollNo;
	}
	
	
}
