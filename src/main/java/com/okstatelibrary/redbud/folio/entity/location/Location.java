package com.okstatelibrary.redbud.folio.entity.location;

import java.util.ArrayList;
import com.okstatelibrary.redbud.folio.entity.Metadata;

public class Location {
	public String id;
	public String name;
	public String code;
	public String discoveryDisplayName;
	public boolean isActive;
	public String institutionId;
	public String campusId;
	public String libraryId;
	public String primaryServicePoint;
	public ArrayList<String> servicePointIds;
	public ArrayList<Object> servicePoints;
	public Metadata metadata;
}
