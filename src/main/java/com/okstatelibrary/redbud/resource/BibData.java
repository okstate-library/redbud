package com.okstatelibrary.redbud.resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class BibData {

    @JacksonXmlProperty(localName = "title")
    private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    // Getters and Setters
}