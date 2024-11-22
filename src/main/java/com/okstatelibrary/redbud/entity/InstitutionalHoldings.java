package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "institutional_holdings")
public class InstitutionalHoldings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "institutional_holdings_id", nullable = false, updatable = false)
	private Integer institutionalHoldingsId;

	private String institutionalHoldingsName;
	private Integer recordCount;

	public Integer getInstitutionalHoldingsId() {
		return institutionalHoldingsId;
	}

	public void setInstitutionalHoldingsId(Integer institutionalHoldingsId) {
		this.institutionalHoldingsId = institutionalHoldingsId;
	}

	public String getInstitutionalHoldingsName() {
		return institutionalHoldingsName;
	}

	public void setInstitutionalHoldingsName(String institutionalHoldingsName) {
		this.institutionalHoldingsName = institutionalHoldingsName;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

}