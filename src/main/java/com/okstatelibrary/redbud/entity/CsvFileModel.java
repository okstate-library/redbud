package com.okstatelibrary.redbud.entity;

public class CsvFileModel {

	public String csvFilePath;

	public String[] institueCodes;

	public CsvFileModel(String csvFilePath, String[] institueCodes) {
		
		// name  of the folder that latest 
		this.csvFilePath = csvFilePath;
		
		this.institueCodes = institueCodes;
	}
}
