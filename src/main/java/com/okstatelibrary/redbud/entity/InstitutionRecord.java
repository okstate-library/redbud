package com.okstatelibrary.redbud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "institution_record")
public class InstitutionRecord {

	public InstitutionRecord() {

	}

	public InstitutionRecord(String institutionId, String locationId, int instanceCount, int holdingCount,
			int itemCount) {
		this.setInstitutionId(institutionId);
		this.setLocationId(locationId);
		this.setInstanceCount(instanceCount);
		this.setHoldingCount(holdingCount);
		this.setItemCount(itemCount);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "institution_record_id", nullable = false, updatable = false)
	private int institutionRecordId;

	private String institutionId;
	private String locationId;
	private int instanceCount;
	private int holdingCount;
	private int itemCount;
	private Date date;
	
	@Transient
	public String institution;

	@Transient
	public String location;

	public int getInstitutionRecordId() {
		return institutionRecordId;
	}

	public void setInstitutionRecordId(int institutionRecordId) {
		this.institutionRecordId = institutionRecordId;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

	public int getHoldingCount() {
		return holdingCount;
	}

	public void setHoldingCount(int holdingCount) {
		this.holdingCount = holdingCount;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

}
