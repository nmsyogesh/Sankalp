package org.sankalpnitjamshedpur.entity;

public class StudentClass {
	private int classId;
	private String className;
	
	
	public StudentClass(int ClassId, String ClassName) {
		this.classId = ClassId;
		this.className = ClassName;
	}
	
	public int getClassId() {
		return classId;
	}
	public void setClassId(int ClassId) {
		this.classId = ClassId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String ClassName) {
		this.className = ClassName;
	}
	
	

}
