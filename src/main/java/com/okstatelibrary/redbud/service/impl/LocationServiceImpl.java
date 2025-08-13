package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.Location;
import com.okstatelibrary.redbud.repository.LocationDao;
import com.okstatelibrary.redbud.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private LocationDao locationDao;

	@Override
	public Location saveLocation(Location location) {
		return locationDao.save(location);
	}

	@Override
	public Location getLocationById(String locationId) {
		return getLocationList().stream().filter(location -> location.getLocation_id().equals(locationId)).findFirst()
				.orElse(null);
	}

	@Cacheable(cacheNames = "getLocationList")
	@Override
	public List<Location> getLocationList() {
		return locationDao.findAll();
	}

	@Override
	public List<Location> getLocationListByLibraryId(String libraryId) {

		return getLocationList().stream().filter(u -> u.getLibrary_id().equals(libraryId)).collect(Collectors.toList());
	}

	@Override
	public List<Location> getLocationListByCampusId(String campusId) {
		return getLocationList().stream().filter(u -> u.getCampus_id().equals(campusId)).collect(Collectors.toList());
	}

	@Override
	public List<Location> getLocationListByInstitutionId(String institution) {
		return getLocationList().stream().filter(u -> u.getInstitution_id().equals(institution))
				.collect(Collectors.toList());
	}

}