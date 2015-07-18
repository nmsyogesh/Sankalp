package org.sankalpnitjamshedpur.entity;

import java.util.ArrayList;

import android.net.Uri;

public class ClassRecord {
	private ArrayList<Uri> uriList;
	private long startTime, endTime;
	private String volunteerId;
	private int centreNo;
	private double startGpsLatitude;
	private double startGpsLongitude;
	private double endGpsLatitude;
	private double endGpsLongitude;
	private boolean sentNotification;

	public ClassRecord() {

	}
	
	public ClassRecord(ArrayList<Uri> uriList, long startTime, long endTime,
			String volunteerId, int centreNo, double startGpsLatitude,
			double startGpsLongitude, double endGpsLatitude,
			double endGpsLongitude) {
		super();
		this.uriList = uriList;
		this.startTime = startTime;
		this.endTime = endTime;
		this.volunteerId = volunteerId;
		this.centreNo = centreNo;
		this.startGpsLatitude = startGpsLatitude;
		this.startGpsLongitude = startGpsLongitude;
		this.endGpsLatitude = endGpsLatitude;
		this.endGpsLongitude = endGpsLongitude;
		sentNotification = false;
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

	public double getStartGpsLatitude() {
		return startGpsLatitude;
	}

	public void setStartGpsLatitude(double startGpsLatitude) {
		this.startGpsLatitude = startGpsLatitude;
	}

	public double getStartGpsLongitude() {
		return startGpsLongitude;
	}

	public void setStartGpsLongitude(double startGpsLongitude) {
		this.startGpsLongitude = startGpsLongitude;
	}

	public double getEndGpsLatitude() {
		return endGpsLatitude;
	}

	public void setEndGpsLatitude(double endGpsLatitude) {
		this.endGpsLatitude = endGpsLatitude;
	}

	public double getEndGpsLongitude() {
		return endGpsLongitude;
	}

	public void setEndGpsLongitude(double endGpsLongitude) {
		this.endGpsLongitude = endGpsLongitude;
	}

	public boolean isSentNotification() {
		return sentNotification;
	}

	public void setSentNotification(boolean sentNotification) {
		this.sentNotification = sentNotification;
	}
}
