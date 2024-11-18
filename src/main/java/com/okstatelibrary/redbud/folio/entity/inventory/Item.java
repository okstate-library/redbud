package com.okstatelibrary.redbud.folio.entity.inventory;

import java.util.ArrayList;
import java.util.Date;

import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.folio.entity.Status;
import com.okstatelibrary.redbud.util.StringHelper;

public class Item {
	public String itemId;
	public String userId;
	public String instanceId;
	public Date loanDate;
	public Date dueDate;

	public PermanentLocation permanentLocation;

	public HoldingsLocation holdingsLocation;

	public String id;
	public String _version;
	public Status status;
	public ArrayList<Object> administrativeNotes;
	public String title;
	public String callNumber;
	public String hrid;
	public ArrayList<ContributorName> contributorNames;
	public ArrayList<String> formerIds;
	public boolean discoverySuppress;
	public String holdingsRecordId;
	public String barcode;
	public String itemLevelCallNumber;
	public String volume;
	public String enumeration;
	public String copyNumber;
	public ArrayList<Note> notes;
	public ArrayList<Object> circulationNotes;
	public Tags tags;
	public ArrayList<Object> yearCaption;
	public ArrayList<Object> electronicAccess;
	public ArrayList<Object> statisticalCodeIds;
	public Object purchaseOrderLineIdentifier;
	public MaterialType materialType;
	public PermanentLoanType permanentLoanType;
	public Metadata metadata;
	public EffectiveCallNumberComponents effectiveCallNumberComponents;
	public String effectiveShelvingOrder;
	public boolean isBoundWith;
	public EffectiveLocation effectiveLocation;

	public String getContributorNames() {

		StringBuilder result = new StringBuilder();

		if (contributorNames != null) {

			for (int i = 0; i < contributorNames.size(); i++) {

				result.append(contributorNames.get(i).name);

				if (i < contributorNames.size() - 1) {

					result.append(", ");
				}

			}

			return result.toString();
		}

		return "N/A";

	}
}
