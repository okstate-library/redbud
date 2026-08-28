package com.okstatelibrary.redbud.entity;

import java.util.List;

public class ReportModel {

	public String fileName;

	public int columnIndexErrorRows;

	public List<SubReportModel> subReports;

//	 Details related to the user's status convert to inactive
	public int setToInactiveUserCount;

	public int setToInactiveSucessUserCount;

	public int setToInactiveErrorUserCount;

	public List<String> setToInactiveErrorUserList;
}
