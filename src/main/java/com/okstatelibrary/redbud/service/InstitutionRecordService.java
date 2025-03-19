package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.InstitutionRecord;

public interface InstitutionRecordService {

	InstitutionRecord saveInstitutionRecordCounts(InstitutionRecord institutionalHoldings);

	List<InstitutionRecord> findAllbyInstitutionId(String institutionId);

	List<InstitutionRecord> findAll();

	void truncate();

}