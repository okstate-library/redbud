package com.okstatelibrary.redbud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.okstatelibrary.redbud.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "circulation_log")
public class CirculationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@Transient
	private String loanId;

	private String location_id;

	private String call_number;

	private String itemId;

	private String barcode;

	private String materialType;

	private String title;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Date loanDate;

	private int numLoans;

	private String author;

	private String edition;

	private String publishYear;

	private String statement;

	private String staffNote;

	private boolean isOpen;

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	private int renewalCount;

	private int almaNumLoans;

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

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the edition
	 */
	public String getEdition() {
		return edition;
	}

	/**
	 * @param edition the edition to set
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}

	/**
	 * @return the publishYear
	 */
	public String getPublishYear() {
		return publishYear;
	}

	/**
	 * @param publishYear the publishYear to set
	 */
	public void setPublishYear(String publishYear) {
		this.publishYear = publishYear;
	}

	/**
	 * @return the renewalCount
	 */
	public int getRenewalCount() {
		return renewalCount;
	}

	/**
	 * @param renewalCount the renewalCount to set
	 */
	public void setRenewalCount(int renewalCount) {
		this.renewalCount = renewalCount;
	}

	/**
	 * @return the almaNumLoans
	 */
	public int getAlmaNumLoans() {
		return almaNumLoans;
	}

	/**
	 * @param almaNumLoans the almaNumLoans to set
	 */
	public void setAlmaNumLoans(int almaNumLoans) {
		this.almaNumLoans = almaNumLoans;
	}

	/**
	 * @return the staffNote
	 */
	public String getStaffNote() {
		return staffNote;
	}

	/**
	 * @param staffNote the staffNote to set
	 */
	public void setStaffNote(String staffNote) {
		this.staffNote = staffNote;
	}
//
//	@ManyToOne
//	@JoinColumn(name = "location", referencedColumnName = "location_id", insertable = false, updatable = false)
//	private Location locationObj;
//
//	public Location getLocationObj() {
//		return locationObj;
//	}
//
//	public void setLocationObj(Location locationObj) {
//		this.locationObj = locationObj;
//	}

	/**
	 * @return the statement
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * @param statement the statement to set
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * @return the location_id
	 */
	public String getLocationId() {
		return location_id;
	}

	/**
	 * @param location_id the location_id to set
	 */
	public void setLocationId(String location_id) {
		this.location_id = location_id;
	}

}