package com.okstatelibrary.redbud.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap {

	public static String error = "Error occured";

	public static String idle = "Idle";

	public static String running = "Running";
	
	public static final String process_CirculationLog_API_Data_Extraction_By_Location = "Process_CirculationLog_API_Data_Extraction_By_Location";

	public static final String process_CirculationLog_API_Data_Extraction = "Process_CirculationLog_API_Data_Extraction";
	
	public static final String process_CirculationLog_API_Data_Extraction_Auto_Process = "Process_CirculationLog_API_Data_Extraction_Auto_Process";

	public static final String process_OCLC_NUmbers_Process = "oclc_Number_Process";

	public static final String process_Execute_Inactive_Users = "Process_Execute_Inactive_Users";

	public static final String process_Send_Test_Email = "Process_Send_Test_Email";

	public static final String process_Alma_Loan_Count = "Process_Alma_Loan_Count";

	public static final String process_Institutional_Holdings_Records_Process = "Institutional_Holdings_Records_Process";

	public static final String process_StaffNote_Update_Process = "StaffNote_Update_Process";
	
	public static final String process_Never_Circulated_Items_Seacrh_Process = "Never_Circulated_Items_Search_Process";


	private static Map<Object, Object> cacheMap = new ConcurrentHashMap<>();

	public static void set(Object key, Object value) {
		cacheMap.put(key, value);
	}

	public static Object get(String key) {
		return cacheMap.get(key);
	}

	public static void cledsdar() {
		cacheMap.clear();
	}

}
