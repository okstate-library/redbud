package com.okstatelibrary.redbud.resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CallNumberType {

    @JacksonXmlProperty(isAttribute = true)
    private String desc;

    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private String value;

    // Getter for value (text content)
    public String getValue() {
        return value;
    }

    @JacksonXmlProperty(isAttribute = false)
    public void setValue(String value) {
        this.value = value;
    }

    // Getters and Setters for desc
}
