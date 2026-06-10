package com.okstatelibrary.redbud.resource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "item")
public class Item {

    @JacksonXmlProperty(isAttribute = true)
    private String link;

    public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public HoldingData getHoldingData() {
		return holdingData;
	}

	public void setHoldingData(HoldingData holdingData) {
		this.holdingData = holdingData;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	@JacksonXmlProperty(localName = "bib_data")
    private BibData bibData;
	
    public BibData getBibData() {
		return bibData;
	}

	public void setBibData(BibData bibData) {
		this.bibData = bibData;
	}

	@JacksonXmlProperty(localName = "holding_data")
    private HoldingData holdingData;

    @JacksonXmlProperty(localName = "item_data")
    private ItemData itemData;

}