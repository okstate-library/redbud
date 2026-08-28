package com.okstatelibrary.redbud.entity;

import java.util.ArrayList;

public class CsvRoot {

	public CsvRoot(String instituteCode) {
		
		this.institution = instituteCode;
		this.users = new ArrayList<CsvUserModel>();
		//this.expireUsers = new ArrayList<CsvUserModel>();
		//this.report = new ReportModel();
		
	}

	public String institution;

	public ArrayList<CsvUserModel> users;

	//public ArrayList<CsvUserModel> expireUsers;
	
	//public ReportModel report;
}
