package com.okstatelibrary.redbud.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties
@PropertySource("classpath:application.properties")
public class AppSystemProperties {

	public static List<String> getProperties() {
		List<String> list = new ArrayList<>();

		list.add("CvsFilePath" + " - " + CvsFilePath);
		list.add("SftpFilePath" + " - " + SftpFilePath);
		list.add("ScheduleCornJobsRunStatus" + " - " + ScheduleCornJobsRunStatus);

		return list;

	}

	@Value("${cvsfilepath}")
	private String cvsfilepath;

	public static String CvsFilePath;

	/**
	 * @param cvsfilepath the cvsfilepath to set
	 */
	public void setCvsfilepath(String cvsfilepath) {
		CvsFilePath = cvsfilepath;
	}

	@Value("${sftpfilepath}")
	private String sftpfilepath;

	public static String SftpFilePath;

	/**
	 * @param cvsfilepath the cvsfilepath to set
	 */
	public void setSftpfilepath(String sftpfilepath) {
		SftpFilePath = sftpfilepath;

	}

	@Value("${emailusername}")
	private String emailusername;

	public static String EmailUsername;

	/**
	 * @param emailUsername the emailUsername to set
	 */
	public void setEmailUsername(String emailUsername) {
		EmailUsername = emailUsername;
	}

	@Value("${emailpassword}")
	private String emailpassword;

	public static String EmailPassword;

	/**
	 * @param emailPassword the emailPassword to set
	 */
	public void setEmailPassword(String emailPassword) {
		EmailPassword = emailPassword;
	}

	@Value("${sendEmails}")
	private String sendEmails;

	public static String SendEmails;

	/**
	 * @param sendEmails the sendEmails to set
	 */
	public void setSendEmails(String sendEmails) {
		SendEmails = sendEmails;
	}

	@Value("${folioTenant}")
	private String folioTenant;

	public static String FolioTenant;

	/**
	 * @param folioTenant the folioTenant to set
	 */
	public void setFolioTenant(String folioTenant) {
		FolioTenant = folioTenant;
	}

	@Value("${folioURL}")
	private String folioURL;

	public static String FolioURL;

	/**
	 * @param folioURL the folioURL to set
	 */
	public void setFolioURL(String folioURL) {
		FolioURL = folioURL;
	}

	@Value("${folioUsername}")
	private String folioUsername;

	public static String FolioUsername;

	/**
	 * @param folioURL the folioURL to set
	 */
	public void setFolioUsername(String folioUsername) {
		FolioUsername = folioUsername;
	}

	@Value("${folioPassword}")
	private String folioPassword;

	public static String FolioPassword;

	/**
	 * @param folioURL the folioURL to set
	 */
	public void setFolioPassword(String folioPassword) {
		FolioPassword = folioPassword;
	}

	@Value("${oclcApiUrl}")
	private String oclcApiUrl;

	public static String OclcApiUrl;

	/**
	 * @param oclcApiUrl to set
	 */
	public void setOclcApiUrl(String oclcApiUrl) {
		OclcApiUrl = oclcApiUrl;
	}

	@Value("${oclcTokenEndpoint}")
	private String oclcTokenEndpoint;

	public static String OclcTokenEndpoint;

	/**
	 * @param oclcTokenEndpoint to set
	 */
	public void setOclcTokenEndpoint(String oclcTokenEndpoint) {
		OclcTokenEndpoint = oclcTokenEndpoint;
	}

	@Value("${oclcKey}")
	private String oclcKey;

	public static String OclcKey;

	/**
	 * @param OclcKey to set
	 */
	public void setOclcKey(String oclcKey) {
		OclcKey = oclcKey;
	}

	@Value("${oclcKeySecret}")
	private String oclcKeySecret;

	public static String OclcKeySecret;

	/**
	 * @param OclcKeySecret to set
	 */
	public void setOclcKeySecret(String oclcKeySecret) {
		OclcKeySecret = oclcKeySecret;
	}

	@Value("${oclcScope}")
	private String oclcScope;

	public static String OclcScope;

	/**
	 * @param oclcScope to set
	 */
	public void setOclcScope(String oclcScope) {
		OclcScope = oclcScope;
	}

	@Override
	public String toString() {
		return "Red Bud App settings";
	}

	@Value("${almaloancsvfilepath}")
	private String almaloancsvfilepath;

	public static String Almaloancsvfilepath;

	/**
	 * @param alma Sft
	 */
	public void setAlmaloancsvfilepath(String almaloancsvfilepath) {
		Almaloancsvfilepath = almaloancsvfilepath;
	}

	@Value("${scheduleCornJobsRunStatus}")
	private boolean scheduleCornJobsRunStatus;

	public static boolean ScheduleCornJobsRunStatus;

	public void setScheduleCornJobsRunStatus(boolean scheduleCornJobsRunStatus) {
		
		ScheduleCornJobsRunStatus = scheduleCornJobsRunStatus;
	}
}