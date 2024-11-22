package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.InstitutionalHoldings;

public interface InstitutionalHoldingsService {

	InstitutionalHoldings saveInstitutionalHoldings(InstitutionalHoldings institutionalHoldings);

	List<InstitutionalHoldings> findAll();

}