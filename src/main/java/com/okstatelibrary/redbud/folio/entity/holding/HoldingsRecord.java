package com.okstatelibrary.redbud.folio.entity.holding;

import java.util.ArrayList;

public class HoldingsRecord {
	
	public String instanceId;
	public boolean discoverySuppress;
	public String permanentLocationId;
	public String effectiveLocationId;
	public String id;
	public ArrayList<HoldingsStatement> holdingsStatements;
}
