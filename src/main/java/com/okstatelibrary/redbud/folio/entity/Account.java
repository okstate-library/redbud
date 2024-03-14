package com.okstatelibrary.redbud.folio.entity;

import java.util.ArrayList;
import java.util.Date;

public class Account {
	public double amount;
	public double remaining;
	public Status status;
	public PaymentStatus paymentStatus;
	public String feeFineType;
	public String feeFineOwner;
	public String title;
	public String barcode;
	public String materialType;
	public String location;
	public Metadata metadata;
	public Date dueDate;
	public String loanId;
	public String userId;
	public String itemId;
	public String materialTypeId;
	public String feeFineId;
	public String ownerId;
	public String id;
	public String holdingsRecordId;
	public String instanceId;
	public ArrayList<Object> contributors;
	public String loanPolicyId;
	public String overdueFinePolicyId;
	public String lostItemFeePolicyId;
}
