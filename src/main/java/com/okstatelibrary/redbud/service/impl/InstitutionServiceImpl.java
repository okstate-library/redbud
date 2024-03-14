package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.Institution;
import com.okstatelibrary.redbud.repository.InstitutionDao;
import com.okstatelibrary.redbud.service.InstitutionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InstitutionServiceImpl implements InstitutionService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private InstitutionDao institutionDao;

	@Override
	public Institution saveInstitution(Institution institution) {
		return institutionDao.save(institution);
	}

	@Cacheable(cacheNames = "getInstitutionList")
	@Override
	public List<Institution> getInstitutionList() {
		return institutionDao.findAll();
	}

}