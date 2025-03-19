package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.InstitutionRecord;
import com.okstatelibrary.redbud.repository.InstitutionalRecordDao;
import com.okstatelibrary.redbud.service.InstitutionRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstitutionalRecordServiceImpl implements InstitutionRecordService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private InstitutionalRecordDao institutionalRecordDao;

	@Override
	public InstitutionRecord saveInstitutionRecordCounts(InstitutionRecord institutionalHoldings) {

		return institutionalRecordDao.save(institutionalHoldings);
	}

	@Override
	public List<InstitutionRecord> findAll() {

		return institutionalRecordDao.findAll();
	}

	@Override
	public void truncate() {

		institutionalRecordDao.truncate();
	}

	@Override
	public List<InstitutionRecord> findAllbyInstitutionId(String institutionId) {
		return institutionalRecordDao.findAll().stream()
				.filter(record -> record.getInstitutionId().equals(institutionId)).collect(Collectors.toList());

	}

}