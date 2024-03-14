package com.okstatelibrary.redbud.service;

import com.okstatelibrary.redbud.entity.Institution;
import java.util.List;

public interface InstitutionService {

	Institution saveInstitution(Institution institution);

	List<Institution> getInstitutionList();
}