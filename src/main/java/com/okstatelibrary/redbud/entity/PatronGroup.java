package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "patron_group")
public class PatronGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id", nullable = false, updatable = false)
	private Integer groupId;

	private String folioGroupId;

	private String folioGroupName;

	private String institutionCode;

	private String institutionGroup;

	@Column(nullable = false, columnDefinition = "BIT", length = 1)
	public byte isFolioOnly;

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the folioGroupId
	 */
	public String getFolioGroupId() {
		return folioGroupId;
	}

	/**
	 * @param folioGroupId the folioGroupId to set
	 */
	public void setFolioGroupId(String folioGroupId) {
		this.folioGroupId = folioGroupId;
	}

	/**
	 * @return the folioGroupName
	 */
	public String getFolioGroupName() {
		return folioGroupName;
	}

	/**
	 * @param folioGroupName the folioGroupName to set
	 */
	public void setFolioGroupName(String folioGroupName) {
		this.folioGroupName = folioGroupName;
	}

	/**
	 * @return the institutionCode
	 */
	public String getInstitutionCode() {
		return institutionCode;
	}

	/**
	 * @param institutionCode the institutionCode to set
	 */
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	/**
	 * @return the institutionGroup
	 */
	public String getInstitutionGroup() {
		return institutionGroup;
	}

	/**
	 * @param institutionGroup the institutionGroup to set
	 */
	public void setInstitutionGroup(String institutionGroup) {
		this.institutionGroup = institutionGroup;
	}

	public PatronGroup() {

	}

	public PatronGroup(String folio_id, String name) {
		setFolioGroupId(folio_id);
		setFolioGroupName(name);
	}

	/**
	 * @return the isFolioOnly
	 */
	public byte isFolioOnly() {
		return isFolioOnly;
	}

	/**
	 * @param isFolioOnly the isFolioOnly to set
	 */
	public void setFolioOnly(byte isFolioOnly) {
		this.isFolioOnly = isFolioOnly;
	}
}