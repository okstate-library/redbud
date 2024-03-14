package com.okstatelibrary.redbud.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource("classpath:application.properties")
public class AppSystemProperties {

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

	@Value("${folioToken}")
	private String folioToken;

	public static String FolioToken;

	/**
	 * @param folioToken the folioToken to set
	 */
	public void setFolioToken(String folioToken) {
		FolioToken = folioToken;
	}

}