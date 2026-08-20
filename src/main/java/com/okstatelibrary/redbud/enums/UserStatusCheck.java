package com.okstatelibrary.redbud.enums;

public enum UserStatusCheck {
	TRUE((short) 1),
	FALSE((short) 2),
	BOTH((short) 3);
	
	/*
	 * Important Note: Must have semicolon at the end when there is a enum field or
	 * method
	 */
	private final short shortCode;

	UserStatusCheck(short code) {
		this.shortCode = code;
	}

	public short getCode() {
		return this.shortCode;
	}
}