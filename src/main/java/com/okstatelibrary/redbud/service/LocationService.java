package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.Location;

public interface LocationService {

	Location saveLocation(Location location);

	Location getLocationById(String locationId);

	List<Location> getLocationList();

	List<Location> getLocationListByLibraryId(String libraryId);

	List<Location> getLocationListByCampusId(String campusId);

	List<Location> getLocationListByInstitutionId(String institution);

}