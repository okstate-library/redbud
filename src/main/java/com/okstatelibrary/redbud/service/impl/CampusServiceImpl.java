package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.Campus;
import com.okstatelibrary.redbud.repository.CampusDao;
import com.okstatelibrary.redbud.service.CampusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CampusServiceImpl implements CampusService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private CampusDao campusDao;

	@Override
	public Campus saveCampus(Campus campus) {
		return campusDao.save(campus);
	}

	@Cacheable(cacheNames = "getCampusList")
	@Override
	public List<Campus> getCampusList() {
		return campusDao.findAll();
	}

	@Override
	public List<Campus> getCampusListByInstitutionId(String institutionId) {

		return getCampusList().stream().filter(u -> u.getInstitution_id().equals(institutionId))
				.collect(Collectors.toList());
	}

}