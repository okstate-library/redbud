package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.entity.Campus;
import com.okstatelibrary.redbud.entity.InstitutionalHoldings;

import java.util.List;

public interface InstitutionalHoldingsDao extends CrudRepository<InstitutionalHoldings, Integer> {

	List<InstitutionalHoldings> findAll();
}