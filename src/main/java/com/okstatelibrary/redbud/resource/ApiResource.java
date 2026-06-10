package com.okstatelibrary.redbud.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.folio.entity.inventory.EffectiveCallNumberComponents;
import com.okstatelibrary.redbud.service.external.FolioService;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
public class ApiResource {

	@Autowired
	protected FolioService folioService;

	@GetMapping(value = "/getItemDetails", produces = MediaType.APPLICATION_XML_VALUE)
	public Item getXmlResponse(@RequestParam(required = false) String barcode)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		ArrayList<com.okstatelibrary.redbud.folio.entity.inventory.Item> items = folioService.getItembyBarcode(barcode);

		System.out.println("barcode ==" + barcode);

		Item response = new Item();

		if (items != null && items.size() > 0) {

			com.okstatelibrary.redbud.folio.entity.inventory.Item selectedItem = items.get(0);

			EffectiveCallNumberComponents effectiveEnum = selectedItem.effectiveCallNumberComponents;

			BibData bibData = new BibData();
			bibData.setTitle(selectedItem.title);

			CallNumberType callNumberType = new CallNumberType();
			callNumberType.setDesc("");
			
			HoldingData holdingData = new HoldingData();
			holdingData.setCallNumber(effectiveEnum.callNumber);
			holdingData.setCallNumberType(callNumberType);
			holdingData.setLink("");

			if (effectiveEnum.prefix != null) {
				holdingData.setCallNumberPrefix(effectiveEnum.prefix.toString());
			}

			Library lib = new Library();
			lib.setDesc("-- Library Description --");
			lib.setValue("-- Library Code --");

			Location loc = new Location();
			loc.setDesc("-- Location Name --");
			loc.setValue("-- Location Code --");

			ItemData itemData = new ItemData();
			itemData.setEnumeration(selectedItem.enumeration);
			itemData.setChronology("");
			itemData.setDescription("");
			itemData.setLibrary(lib);
			itemData.setLocation(loc);
			itemData.setLocationGloss("");

			response.setLink("");
			response.setBibData(bibData);
			response.setHoldingData(holdingData);
			response.setItemData(itemData);

		}

		return response;
	}

}