package com.okstatelibrary.redbud.entity;

import java.util.List;

public class SubReportModel {

	public SubReportModel(String institueCodes, String patronGroupName) {
		this.institueCodes = institueCodes;
		this.patronGroupName = patronGroupName;
	}

	public String institueCodes;

	public String patronGroupName;

	// New users

	public int newUsersFromCSVCount;

	public int newUsersFromCSVAddedFolioCount;

	public int newUsersFromCSVAddedFolioErrorCount;

	public List<String> existingUserModified;

	public List<String> newUsersFromCSVAddedFolioErrorUserList;

	// Existing users
	public int usersInFolioAndCsvCount;

	public int modifiedUsersInFolioAndCsvCount;

	public int modifiedUsersInFolioAndCsvErrorCount;

	public List<String> modifiedUsersInFolioAndCsvUserList;
	
	public List<String> modifiedUsersInFolioAndCsvErrorUserList;

	// The Folio Users most of the time need to
	public int usersInFoliOnlyCount;

	public int modifiedUsersInFoliOnlyCount;

	public int modifiedUsersInFoliOnlyErrorCount;

	public List<String> modifiedUsersInFoliOnlyErrorUserList;

}
