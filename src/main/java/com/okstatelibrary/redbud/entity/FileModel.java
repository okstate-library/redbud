package com.okstatelibrary.redbud.entity;

public class FileModel {

	private String fileName;

	private String path;
	
	private String lastModifiedDate;
	
	private boolean isImported;

	/**
	 * @return the lastModifiedName
	 */
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the isImported
	 */
	public boolean isImported() {
		return isImported;
	}

	/**
	 * @param isImported the isImported to set
	 */
	public void setImported(boolean isImported) {
		this.isImported = isImported;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}


}
