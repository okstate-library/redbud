package com.okstatelibrary.redbud.resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class HoldingData {

    @JacksonXmlProperty(isAttribute = true)
    private String link;

    public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCallNumberPrefix() {
		return callNumberPrefix;
	}

	public void setCallNumberPrefix(String callNumberPrefix) {
		this.callNumberPrefix = callNumberPrefix;
	}

	public String getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}
	
	@JacksonXmlProperty(localName = "call_number_prefix")
    private String callNumberPrefix;

    @JacksonXmlProperty(localName = "call_number")
    private String callNumber;

    @JacksonXmlProperty(localName = "call_number_type")
    private CallNumberType callNumberType;

	public CallNumberType getCallNumberType() {
		return callNumberType;
	}

	public void setCallNumberType(CallNumberType callNumberType) {
		this.callNumberType = callNumberType;
	}
}