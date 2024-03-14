package com.okstatelibrary.redbud.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvUserModel {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(CsvUserModel.class);

	private String institution;

	private String bannerId;

	private String iSo;

	private String userGroup;

	private String firstName;

	private String middleName;

	private String lastName;

	private String preferedFirstName;

	//private String preferedLastName;

	private String localPhone;

	private String workPhone;

	private String okeyEmail;

	private String okeyUsername;

	/**
	 * @return the institution
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * @param institution the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * @return the bannerId
	 */
	public String getBannerId() {
		return bannerId;
	}

	/**
	 * @param bannerId the bannerId to set
	 */
	public void setBannerId(String bannerId) {
		this.bannerId = bannerId;
	}

	/**
	 * @return the bar code
	 */
	public String getISOCode() {

		if (iSo != null && !iSo.trim().isEmpty()) {
			return iSo;
		} else {
			return bannerId;
		}

	}

	/**
	 * @param iso the bar code to set
	 */
	public void setISOCode(String isoCode) {
		this.iSo = isoCode;
	}

	public String getMainUserGroup() {
		String[] multiCampus = this.userGroup.split(";");

		return multiCampus[0].trim();
	}

	/**
	 * @return the userGroup
	 */
	public String getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the preferedFirstName
	 */
	public String getPreferedFirstName() {
		return preferedFirstName;
	}

	/**
	 * @param preferedFirstName the preferedFirstName to set
	 */
	public void setPreferedFirstName(String preferedFirstName) {
		this.preferedFirstName = preferedFirstName;
	}

//	/**
//	 * @return the preferedLastName
//	 */
//	public String getPreferedLastName() {
//		return preferedLastName;
//	}
//
//	/**
//	 * @param preferedLastName the preferedLastName to set
//	 */
//	public void setPreferedLastName(String preferedLastName) {
//		this.preferedLastName = preferedLastName;
//	}

	/**
	 * @return the localPhone
	 */
	public String getLocalPhone() {
		return localPhone;
	}

	/**
	 * @param localPhone the localPhone to set
	 */
	public void setLocalPhone(String localPhone) {
		this.localPhone = localPhone;
	}

	/**
	 * @return the workPhone
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * @param workPhone the workPhone to set
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * @return the okeyEmail
	 */
	public String getOkeyEmail() {
		return okeyEmail;
	}

	/**
	 * @param okeyEmail the okeyEmail to set
	 */
	public void setOkeyEmail(String okeyEmail) {
		this.okeyEmail = okeyEmail;
	}

	/**
	 * @return the okeyUsername
	 */
	public String getOkeyUsername() {
		return okeyUsername;
	}

	/**
	 * @param okeyUsername the okeyUsername to set
	 */
	public void setOkeyUsername(String okeyUsername) {
		this.okeyUsername = okeyUsername;
	}

	public CsvUserModel(String value) {

		try {

			String[] cvsValues = value.split(",");

			// System.out.println("Length of the array" + cvsValues.length);

			this.setInstitution(cvsValues[0].trim());
			this.setBannerId(cvsValues[1].trim());
			this.setISOCode(cvsValues[2].trim());
			this.setUserGroup(cvsValues[3].trim());
			this.setFirstName(cvsValues[4].trim());
			this.setMiddleName(cvsValues[5].trim());
			this.setLastName(cvsValues[6].trim());
			this.setPreferedFirstName(cvsValues[7].trim());
			//this.setPreferedLastName(cvsValues[8].trim());
			this.setLocalPhone(cvsValues[9].trim());
			this.setWorkPhone(cvsValues[10].trim());
			this.setOkeyEmail(cvsValues[11].trim());
			this.setOkeyUsername(cvsValues[12].trim());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error("Error when reading the csv line :- " + value);
		}
	}

	@Override
	public String toString() {
		return "CSV: " + this.getBannerId() + "  -  " + this.getFirstName() + "  -  " + this.getUserGroup();
	}
}
