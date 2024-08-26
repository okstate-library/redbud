package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.PatronGroup;
import com.okstatelibrary.redbud.repository.GroupDao;
import com.okstatelibrary.redbud.service.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private GroupDao groupDao;

	@Override
	public PatronGroup saveGroup(PatronGroup group) {
		return groupDao.save(group);
	}

	@Cacheable(cacheNames = "getGroupList")
	@Override
	public List<PatronGroup> getGroupList() {
		return groupDao.findAll();
	}

	@Override
	public List<PatronGroup> getGroupListByInstituteCode(String instituteCode) {

		return getGroupList().stream().filter(p -> p.getInstitutionCode().equals(instituteCode))
				.collect(Collectors.toList());
	}

}