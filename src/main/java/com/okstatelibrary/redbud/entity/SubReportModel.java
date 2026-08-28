package com.okstatelibrary.redbud.entity;

import java.util.List;

public class SubReportModel {

	public SubReportModel(String institueCodes, String patronGroupName) {
		this.institueCodes = institueCodes;
		this.patronGroupName = patronGroupName;
	}

	public String institueCodes;

	public String patronGroupName;

	// New users details

	public int possibleNewUserCount;

	public int setNewUserSucessCount;

	public int setNewUserErrorCount;

	public List<String> setNewUserErrorUserList;

	// Existing users
	public int possibleModifiedUserCount;

	public int modifiedSucessUserCount;

	public int modifiedErrorUserCount;

	public List<String> modifiedErrorUserList;

}
