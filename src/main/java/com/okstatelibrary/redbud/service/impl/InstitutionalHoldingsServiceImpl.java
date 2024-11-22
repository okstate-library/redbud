package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.InstitutionalHoldings;
import com.okstatelibrary.redbud.repository.InstitutionalHoldingsDao;
import com.okstatelibrary.redbud.service.InstitutionalHoldingsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InstitutionalHoldingsServiceImpl implements InstitutionalHoldingsService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private InstitutionalHoldingsDao institutionalHoldingsDao;

	@Override
	public InstitutionalHoldings saveInstitutionalHoldings(InstitutionalHoldings institutionalHoldings) {

		return institutionalHoldingsDao.save(institutionalHoldings);
	}

	@Override
	public List<InstitutionalHoldings> findAll() {

		return institutionalHoldingsDao.findAll();
	}

}