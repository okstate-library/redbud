package com.okstatelibrary.redbud.util;

import java.util.ArrayList;
import java.util.List;

import com.okstatelibrary.redbud.entity.CsvFileModel;

/**
 * Utility class that holds constant values and configuration used across the
 * Redbud application.
 * <p>
 * This includes predefined CSV file configurations, error levels, and default
 * string constants.
 * </p>
 */
public class Constants {

	/**
	 * A static list of predefined {@link CsvFileModel} instances used for
	 * CSV-related operations.
	 * <p>
	 * These models define a mapping between a source name (like "plfeed", "noc")
	 * and the associated library codes represented as an array of Strings.
	 * </p>
	 */
	public static final List<CsvFileModel> csvFileModels = new ArrayList<CsvFileModel>() {

		private static final long serialVersionUID = 1L;

		{
			add(new CsvFileModel("plfeed", new String[] { "OKS-OSU" }));

//            add(new CsvFileModel( "OKS-OSU","OTL-OSUIT", "OUH-CHS", "OK1-NEO","OLR-CSC", "OUJ-OKC", "PS1-OPSU", "OTU-TUL"
//                "noc",
//                new String[] { "ONN-NOC" }
//            ));

			// Additional models can be added here as needed.
			// Example:
			// add(new CsvFileModel("pts", new String[] { "OKG-PTS" }));
		}
	};

	/**
	 * Enumeration representing the level of an error message.
	 */
	public enum ErrorLevel {
		/** Informational message (not an error). */
		INFO,

		/** Warning message (non-critical issue). */
		WARNING,

		/** Error message (critical issue). */
		ERROR
	}

	/**
	 * A constant message representing a custom field update for expired users.
	 */
	public static String expired_user_cutom_field = "Expired user custom field update";

	/**
	 * A default date string used when no valid date is available. Format:
	 * YYYY-MM-DD (represents a "null" or default-like date).
	 */
	public static String default_date = "0001-01-01";
}
