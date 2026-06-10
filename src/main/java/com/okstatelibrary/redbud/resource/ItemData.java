package com.okstatelibrary.redbud.resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ItemData {

	private String enumeration;

	public String getEnumeration() {
		return enumeration;
	}

	public void setEnumeration(String enumeration) {
		this.enumeration = enumeration;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the chronology
	 */
	public String getChronology() {
		return chronology;
	}

	/**
	 * @param chronology the chronology to set
	 */
	public void setChronology(String chronology) {
		this.chronology = chronology;
	}

	private String chronology;
	private String description;

	@JacksonXmlProperty(localName = "library")
	private Library library;

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	@JacksonXmlProperty(localName = "location")
	private Location location;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@JacksonXmlProperty(localName = "location_gloss")
	private String locationGloss;

	public String getLocationGloss() {
		return locationGloss;
	}

	public void setLocationGloss(String locationGloss) {
		this.locationGloss = locationGloss;
	}

}
