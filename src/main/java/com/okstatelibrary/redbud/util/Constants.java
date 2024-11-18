package com.okstatelibrary.redbud.util;

import java.util.ArrayList;
import java.util.List;

import com.okstatelibrary.redbud.entity.CsvFileModel;

public class Constants {
	public static final List<CsvFileModel> csvFileModels = new ArrayList<CsvFileModel>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		{

			add(new CsvFileModel("plfeed", new String[] { "OKS-OSU", "OTL-OSUIT", "OUH-CHS", "OK1-NEO", "OLR-CSC",
					"OUJ-OKC", "PS1-OPSU", "OTU-TUL" }));
			add(new CsvFileModel("noc", new String[] { "ONN-NOC" }));
			add(new CsvFileModel("pts", new String[] { "OKG-PTS" }));
		}

	};

	public enum ErrorLevel {
		INFO, WARNING, ERROR
	}

	public static String expired_user_cutom_field = "Expired user custom field update";
	
	
	public static String default_date = "0001-01-01";

}
