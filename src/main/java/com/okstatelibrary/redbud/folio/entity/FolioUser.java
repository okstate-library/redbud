package com.okstatelibrary.redbud.folio.entity;

import java.util.ArrayList;

public class FolioUser {
	public String username;
	public String externalSystemId;
	public String id;
	public boolean active;
	public String patronGroup;
	public String barcode;
	public ArrayList<Object> departments;
	public ArrayList<Object> proxyFor;

	public Personal personal;

	public String expirationDate;

	public CustomFields customFields;

//	public Date updatedDate;
	public Metadata metadata;

	@Override
	public String toString() {
		return "FOLIO: " + this.externalSystemId + "  - " + this.personal.firstName + "  -  "
				+ this.customFields.additionalPatronGroup_4;
	}
}
