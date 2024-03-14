package com.okstatelibrary.redbud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.okstatelibrary.redbud.entity.Campus;
import com.okstatelibrary.redbud.entity.Library;
import com.okstatelibrary.redbud.entity.Location;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;

@Controller
@RequestMapping("/")
public class MasterDataController {

	@Autowired
	private CampusService campusService;

	@Autowired
	private LibraryService libraryService;

	@Autowired
	private LocationService locationService;
	
	@RequestMapping(value = "/getCampuses", method = RequestMethod.GET)
	public @ResponseBody List<Campus> getCampus(@RequestParam(value = "institutionId") String institutionId) {

		System.out.println("institutionId " + institutionId);

		return campusService.getCampusListByInstitutionId(institutionId);
	}

	@RequestMapping(value = "/getLibraries", method = RequestMethod.GET)
	public @ResponseBody List<Library> getLibraries(@RequestParam(value = "campusId") String campusId) {

		return libraryService.getLibraryListByCampusId(campusId);
	}

	@RequestMapping(value = "/getLocations", method = RequestMethod.GET)
	public @ResponseBody List<Location> getLocations(@RequestParam(value = "libraryId") String libraryId) {

		return locationService.getLocationListByLibraryId(libraryId);
	}

}