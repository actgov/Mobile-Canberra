package com.imagineteam.dataobjects;

public class DatasetListObject {

	private String name;
	private String color;
	private String datasetId;
	
	
	public DatasetListObject(String name, String color, String datasetId){
		this.name = name;
		this.color = color;
		this.datasetId = datasetId;
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public String getDatasetId() {
		return datasetId;
	}


	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}
	
	
}
