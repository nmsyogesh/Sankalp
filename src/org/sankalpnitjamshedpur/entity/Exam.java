package org.sankalpnitjamshedpur.entity;

public class Exam {
	private int examId;
	private String examDate;
	private String examType;
	public Exam(int examId, String examDate, String examType) {
		super();
		this.examId = examId;
		this.examDate = examDate;
		this.examType = examType;
	}
	public int getExamId() {
		return examId;
	}
	public void setExamId(int examId) {
		this.examId = examId;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamType() {
		return examType;
	}
	public void setExamType(String examType) {
		this.examType = examType;
	}
	
	
}
