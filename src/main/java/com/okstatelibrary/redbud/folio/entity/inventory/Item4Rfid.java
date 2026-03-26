package com.okstatelibrary.redbud.folio.entity.inventory;

import java.util.ArrayList;

import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.folio.entity.Status;

public class Item4Rfid {

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
	public String copyNumber;
	public ArrayList<Note> notes;
	public ArrayList<Object> circulationNotes;
	public Tags tags;
	public ArrayList<Object> yearCaption;
	public ArrayList<Object> electronicAccess;
	public ArrayList<String> statisticalCodeIds;
	public Object purchaseOrderLineIdentifier;
	public MaterialType materialType;
	public PermanentLoanType permanentLoanType;
	public Metadata metadata;
	public EffectiveCallNumberComponents effectiveCallNumberComponents;
	public String effectiveShelvingOrder;
	public boolean isBoundWith;
	public EffectiveLocation effectiveLocation;

}
