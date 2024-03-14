package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.Campus;

public interface CampusService {

	Campus saveCampus(Campus library);

	List<Campus> getCampusList();

	List<Campus> getCampusListByInstitutionId(String institutionId);
}