package org.sankalpnitjamshedpur.entity;

public class Centre {
	private int centreId;
	private String centreName;
	
	
	public Centre(int centreId, String centreName) {
		this.centreId = centreId;
		this.centreName = centreName;
	}
	
	public int getCentreId() {
		return centreId;
	}
	public void setCentreId(int centreId) {
		this.centreId = centreId;
	}
	public String getCentreName() {
		return centreName;
	}
	public void setCentreName(String centreName) {
		this.centreName = centreName;
	}
	
	

}
