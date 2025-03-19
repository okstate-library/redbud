package com.okstatelibrary.redbud.folio.entity.holding;

// Remove some unwanted properties from
// the main object to reduce memory heap issue.
public class HoldingsRecord2 {

	private String instanceId;

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
