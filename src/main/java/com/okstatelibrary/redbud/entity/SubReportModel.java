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

	public int setNewUserCount;

	public int setNewUserSucessCount;

	public int setNewUserErrorCount;

	//public List<String> existingUserModified;

	public List<String> setNewUserErrorUserList;

	// Existing users
	public int usersInFolioAndCsvCount;

	public int modifiedSucessUserCount;

	public int modifiedErrorUserCount;

	public List<String> modifiedErrorUserList;

	// Details related to the user's status convert to inactive
	public int setToInactiveUserCount;

	public int setToInactiveSucessUserCount;

	public int setToInactiveErrorUserCount;

	public List<String> setToInactiveErrorUserList;

}
