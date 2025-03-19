package com.okstatelibrary.redbud.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;

public class InfrastructureSetupProcess extends MainProcess {

	public InfrastructureSetupProcess() {

	}

	protected String startTime;

	public void manipulate(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService, ServicePointService servicePointService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		setupInstitution(institutionService);

		setupCampus(campusService);

		setupLibrary(libraryService);

		setupLocation(locationService);

		setupServicePoint(servicePointService);
	}

	public ArrayList<LocationModel> getLocations(InstitutionService institutionService, CampusService campusService,
			LibraryService libraryService, LocationService locationService)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {

		List<Library> libraries = libraryService.getLibraryList();

		List<Campus> campuses = campusService.getCampusList();

		List<Institution> institutions = institutionService.getInstitutionList();

		List<Location> locations = locationService.getLocationList();

		ArrayList<LocationModel> locationModels = new ArrayList<LocationModel>();

		for (Location location : locations) {

			LocationModel locationModel = new LocationModel();

			locationModel.location_id = location.getLocation_id();
			locationModel.location = location.getLocation_name();

			Library library = libraries.stream().filter(u -> u.getLibrary_id().equals(location.getLibrary_id()))
					.findFirst().get();

			locationModel.library_id = library.getLibrary_id();
			locationModel.library = library.getLibrary_name();

			Campus campus = campuses.stream().filter(u -> u.getCampus_id().equals(location.getCampus_id())).findFirst()
					.get();

			locationModel.campus_id = campus.getCampus_id();
			locationModel.campus = campus.getCampus_name();

			Institution institution = institutions.stream()
					.filter(u -> u.getInstitution_id().equals(location.getInstitution_id())).findFirst().get();

			locationModel.institution_id = institution.getInstitution_id();
			locationModel.institution = institution.getInstitution_name();

			locationModels.add(locationModel);
		}

		return locationModels;

	}

	private void setupLocation(LocationService locationService) {

		ArrayList<com.okstatelibrary.redbud.folio.entity.location.Location> folioLocations = folioService
				.getLocations();

		try {

			List<Location> locations = locationService.getLocationList();

			for (com.okstatelibrary.redbud.folio.entity.location.Location folioLocation : folioLocations) {

				Boolean isIn = false;

				for (Location location : locations) {

					if (folioLocation.id.equals(location.getLibrary_id())) {

						isIn = true;

						break;
					}

				}

				if (!isIn) {

					Location location = new Location();

					location.setInstitution_id(folioLocation.institutionId);
					location.setCampus_id(folioLocation.campusId);
					location.setLibrary_id(folioLocation.libraryId);
					location.setLocation_id(folioLocation.id);
					location.setLocation_code(folioLocation.code);
					location.setLocation_name(folioLocation.name);

					locationService.saveLocation(location);
				}

			}

		} catch (Exception e1) {
			printScreen("setupLibrary : " + e1.getMessage(), Constants.ErrorLevel.ERROR);
		}

	}

	private void setupLibrary(LibraryService libraryService) {

		ArrayList<com.okstatelibrary.redbud.folio.entity.library.Library> folioCampuses = folioService.getLibraries();

		try {

			List<Library> libraries = libraryService.getLibraryList();

			for (com.okstatelibrary.redbud.folio.entity.library.Library folioLibrary : folioCampuses) {

				Boolean isIn = false;

				for (Library library : libraries) {

					if (folioLibrary.id.equals(library.getLibrary_id())) {

						isIn = true;

						break;
					}

				}

				if (!isIn) {

					Library library = new Library();

					library.setCampus_id(folioLibrary.campusId);
					library.setLibrary_id(folioLibrary.id);
					library.setLibrary_code(folioLibrary.code);
					library.setLibrary_name(folioLibrary.name);

					libraryService.saveLibrary(library);
				}

			}

		} catch (Exception e1) {
			printScreen("setupLibrary : " + e1.getMessage(), Constants.ErrorLevel.ERROR);
		}

	}

	private void setupCampus(CampusService campusService) {

		ArrayList<com.okstatelibrary.redbud.folio.entity.campus.Campus> folioCampuses = folioService.getCampuses();

		try {

			List<Campus> campuses = campusService.getCampusList();

			for (com.okstatelibrary.redbud.folio.entity.campus.Campus folioCampus : folioCampuses) {

				Boolean isIn = false;

				for (Campus campus : campuses) {

					if (folioCampus.id.equals(campus.getCampus_id())) {

						isIn = true;

						break;
					}

				}

				if (!isIn) {

					Campus campus = new Campus();

					campus.setInstitution_id(folioCampus.institutionId);
					campus.setCampus_id(folioCampus.id);
					campus.setCampus_code(folioCampus.code);
					campus.setCampus_name(folioCampus.name);

					campusService.saveCampus(campus);
				}

			}

		} catch (Exception e1) {
			printScreen("setupCampus : " + e1.getMessage(), Constants.ErrorLevel.ERROR);
		}
	}

	private void setupInstitution(InstitutionService institutionService) {

		ArrayList<com.okstatelibrary.redbud.folio.entity.institution.Institution> folioInstitutions = folioService
				.getInstitutions();

		try {

			List<Institution> institutions = institutionService.getInstitutionList();

			for (com.okstatelibrary.redbud.folio.entity.institution.Institution folioInstute : folioInstitutions) {

				Boolean isIn = false;

				for (Institution institution : institutions) {

					if (folioInstute.id.equals(institution.getInstitution_id())) {

						isIn = true;

						break;
					}

				}

				if (!isIn) {

					Institution institution = new Institution();

					institution.setInstitution_id(folioInstute.id);
					institution.setInstitution_code(folioInstute.code);
					institution.setInstitution_name(folioInstute.name);

					institutionService.saveInstitution(institution);
				}

			}

		} catch (Exception e1) {
			printScreen("setupInstitution : " + e1.getMessage(), Constants.ErrorLevel.ERROR);
		}
	}

	private void setupServicePoint(ServicePointService servicePointService) {

		ArrayList<com.okstatelibrary.redbud.folio.entity.servicepoint.ServicePoint> folioServicePoints = folioService
				.getServicePoints();

		try {

			List<ServicePoint> servicePoints = servicePointService.getServicePointList();

			for (com.okstatelibrary.redbud.folio.entity.servicepoint.ServicePoint folioServicePoint : folioServicePoints) {

				Boolean isIn = false;

				for (ServicePoint servicePoint : servicePoints) {

					if (folioServicePoint.id.equals(servicePoint.getServicepoint_id())) {

						isIn = true;

						break;
					}

				}

				if (!isIn) {

					ServicePoint servicePoint = new ServicePoint();

					servicePoint.setCode(folioServicePoint.code);
					servicePoint.setName(folioServicePoint.name);
					servicePoint.setServicepoint_id(folioServicePoint.id);

					servicePointService.saveServicePoint(servicePoint);
				}

			}

		} catch (Exception e1) {
			printScreen("setupInstitution : " + e1.getMessage(), Constants.ErrorLevel.ERROR);
		}
	}

}
