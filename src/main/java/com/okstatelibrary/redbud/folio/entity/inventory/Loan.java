package com.okstatelibrary.redbud.folio.entity.inventory;

import java.util.Date;

import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.folio.entity.Status;

public class Loan {
	public String id;
	public String userId;
	public String itemId;
	public String itemEffectiveLocationIdAtCheckOut;
	public Status status;
	public Date loanDate;
	private Date dueDate;
	public String action;
	public String loanPolicyId;
	public String checkoutServicePointId;
	public String overdueFinePolicyId;
	public String lostItemPolicyId;
	public Metadata metadata;
	// public PatronGroupAtCheckout patronGroupAtCheckout;
	public Item item;
	// public CheckoutServicePoint checkoutServicePoint;
	public Borrower borrower;
	public LoanPolicy loanPolicy;
	// loanPolicy;
	// public OverdueFinePolicy overdueFinePolicy;
	// public LostItemPolicy lostItemPolicy;
	// public FeesAndFines feesAndFines;

	/**
	 * @return the dueDate
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

}
