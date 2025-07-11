package com.okstatelibrary.redbud.folio.entity.holding;

import java.util.ArrayList;

public class HoldingsRecord {
	
	private String instanceId;
	public boolean discoverySuppress;
	public String permanentLocationId;
	public String effectiveLocationId;
	public String id;
	public ArrayList<HoldingsStatement> holdingsStatements;
	/**
	 * @return the instanceId
	 */
	public String getInstanceId() {
		return instanceId;
	}
	/**
	 * @param instanceId the instanceId to set
	 */
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}
