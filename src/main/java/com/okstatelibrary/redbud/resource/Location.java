package com.okstatelibrary.redbud.resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Location {

    @JacksonXmlProperty(isAttribute = true)
    private String desc;

    private String value;

    public String getValue() {
        return value;
    }

    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@JacksonXmlProperty(isAttribute = false)
    public void setValue(String value) {
        this.value = value;
    }

    // Getters and Setters
}