package com.okstatelibrary.redbud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.okstatelibrary.redbud.util.DateUtil;

@Entity
@Table(name = "circulation_log")
public class CirculationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@Transient
	private String loanId;

	private String location;

	private String call_number;

	private String itemId;

	private String barcode;

	private String materialType;

	private String title;

	private Date loanDate;

	private int numLoans;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the folioGroupId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param folioGroupId the folioGroupId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode() {
//		if (barcode != null && !barcode.trim().isEmpty()) {
//			return "N/A";
//
//		}
		return barcode;
	}

	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return the materialType
	 */
	public String getMaterialType() {
		return materialType;
	}

	/**
	 * @param materialType the materialType to set
	 */
	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	/**
	 * @return the loanDate
	 */
	public String getLoanDate() {
		return DateUtil.getShortDate(loanDate);
	}

	/**
	 * @param loanDate the loanDate to set
	 */
	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	/**
	 * @return the numLoans
	 */
	public int getNumLoans() {
		return numLoans;
	}

	/**
	 * @param numLoans the numLoans to set
	 */
	public void setNumLoans(int numLoans) {
		this.numLoans = numLoans;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the loanId
	 */
	public String getLoanId() {
		return loanId;
	}

	/**
	 * @param loanId the loanId to set
	 */
	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the call_number
	 */
	public String getCallNumber() {
		return call_number;
	}

	/**
	 * @param call_number the call_number to set
	 */
	public void setCallNumber(String call_number) {
		this.call_number = call_number;
	}

}