package com.okstatelibrary.redbud.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap {

	public static String idle = "Idle";

	public static String running = "Running";
	
	public static String process_OCLC_NUmbers_Process = "oclc_Number_Process";
	
	public static String process_Execute_Inactive_Users = "Process_Execute_Inactive_Users";

	public static String process_Send_Test_Email = "Process_Send_Test_Email";
	
	public static String process_Alma_Loan_Count = "Process_Alma_Loan_Count";
	
	public static String process_Institutional_Holdings_Records_Process= "Institutional_Holdings_Records_Process";

	private static Map<Object, Object> cacheMap = new ConcurrentHashMap<>();

	public static void set(Object key, Object value) {
		cacheMap.put(key, value);
	}

	public static Object get(String key) {
		return cacheMap.get(key);
	}

	public static void clear() {
		cacheMap.clear();
	}

}
