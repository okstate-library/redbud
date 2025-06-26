package com.okstatelibrary.redbud.model;

import java.util.Date;

public class CirculationLoanReportModel {

	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumLoans() {
		return numLoans;
	}

	public void setNumLoans(int numLoans) {
		this.numLoans = numLoans;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(String publishYear) {
		this.publishYear = publishYear;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getStaffNote() {
		return staffNote;
	}

	public void setStaffNote(String staffNote) {
		this.staffNote = staffNote;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getRenewalCount() {
		return renewalCount;
	}

	public void setRenewalCount(Integer renewalCount) {
		this.renewalCount = renewalCount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the callNumber
	 */
	public String getCallNumber() {
		return callNumber;
	}

	/**
	 * @param callNumber the callNumber to set
	 */
	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}

	private String location;

	private String callNumber;

	private String itemId;

	private String barcode;

	private String materialType;

	private String title;

	private int numLoans;

	private String author;

	private String edition;

	private String publishYear;

	private String statement;

	private String staffNote;

	private boolean isOpen;
	
    private String action;
    
    private Integer renewalCount;
    
    private String date;
}
