package com.okstatelibrary.redbud.folio.entity.loan;

import java.util.Date;

import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.folio.entity.Status;

public class Loan {
	public String id;
	public String userId;
	public String itemId;
	public String itemEffectiveLocationIdAtCheckOut;
	public Status status;
	private String loanDate;
	public String dueDate;
	public String action;
	public int renewalCount;
	public String actionComment;
	public String loanPolicyId;
	public String checkoutServicePointId;
	public String overdueFinePolicyId;
	public String lostItemPolicyId;
	public Metadata metadata;
	public PatronGroupAtCheckout patronGroupAtCheckout;
	public Item item;
	public CheckoutServicePoint checkoutServicePoint;
	public Borrower borrower;
	public LoanPolicy loanPolicy;
	public OverdueFinePolicy overdueFinePolicy;
	public LostItemPolicy lostItemPolicy;
	public FeesAndFines feesAndFines;
	public Date returnDate;
	public Date systemReturnDate;
	public String checkinServicePointId;
	public CheckinServicePoint checkinServicePoint;

	/**
	 * @return the loanDate
	 */
	public String getLoanDate() {
		return loanDate;
	}

	/**
	 * @param loanDate the loanDate to set
	 */
	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate.toString();
	}

	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate.toString();
	}
}
