package org.sankalpnitjamshedpur.entity;

import java.util.ArrayList;

import android.net.Uri;

public class ClassRecord {
	private ArrayList<Uri> uriList;
	private long startTime, endTime;
	private String volunteerId;
	private int centreNo;
	
	public ClassRecord() {
		
	}
	
	public ClassRecord(ArrayList<Uri> uriList, long startTime, long endTime,
			String volunteerId, int centreNo) {
		super();
		this.uriList = uriList;
		this.startTime = startTime;
		this.endTime = endTime;
		this.volunteerId = volunteerId;
		this.centreNo = centreNo;
	}
	
	public ArrayList<Uri> getUriList() {
		return uriList;
	}
	public void setUriList(ArrayList<Uri> uriList) {
		this.uriList = uriList;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String getVolunteerId() {
		return volunteerId;
	}
	public void setVolunteerId(String volunteerId) {
		this.volunteerId = volunteerId;
	}
	public int getCentreNo() {
		return centreNo;
	}
	public void setCentreNo(int centreNo) {
		this.centreNo = centreNo;
	}
}
