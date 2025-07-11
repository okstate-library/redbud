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

	public Metadata metadata;

	@Override
	public String toString() {

		return this.externalSystemId + " - " + this.barcode + " - " + this.username + " - " + this.personal.firstName
				+ " " + this.personal.lastName + "  -  " + this.customFields != null
						? this.customFields.additionalPatronGroup_4
						: "";
	}
}
