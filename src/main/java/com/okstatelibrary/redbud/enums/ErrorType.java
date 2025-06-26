package com.okstatelibrary.redbud.enums;

public enum ErrorType {
	ERROR((short) 1),
	WARNING((short) 2),
	SUCESS((short) 3);
	
	/*
	 * Important Note: Must have semicolon at the end when there is a enum field or
	 * method
	 */
	private final short shortCode;

	ErrorType(short code) {
		this.shortCode = code;
	}

	public short getCode() {
		return this.shortCode;
	}
}