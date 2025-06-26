package com.okstatelibrary.redbud.enums;

public enum LoanAction {
	none((short) 0), checkedout((short) 1), renewed((short) 2), checkedOutThroughOverride((short) 3),
	checkedin((short) 4);

	/*
	 * Important Note: Must have semicolon at the end when there is a enum field or
	 * method
	 */
	private final short shortCode;

	LoanAction(short code) {
		this.shortCode = code;
	}

	public short getCode() {
		return this.shortCode;
	}

	// Static method to get enum by code
	public static LoanAction fromCode(int code) {

		for (LoanAction status : LoanAction.values()) {
			if (status.getCode() == code) {
				return status;
			}
		}

		System.out.println("Action requested is in fromCode method : --- " + code);

		return none;
	}

	public static short toCode(String action) {

		for (LoanAction status : LoanAction.values()) {

			if (status.toString().contentEquals(action)) {
				return status.getCode();
			}
		}

		System.out.println("Action requested is in toCode method : --- " + action);

		return none.getCode();
	}

}