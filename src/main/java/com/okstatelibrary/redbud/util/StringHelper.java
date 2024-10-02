package com.okstatelibrary.redbud.util;

public class StringHelper {

	public static boolean isStringNullOrEmpty(String str) {
		
		if (str != null && !str.trim().isEmpty()) {
			return false;
		}
		
		return true;
	}

}
