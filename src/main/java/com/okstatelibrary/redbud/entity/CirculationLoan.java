package com.okstatelibrary.redbud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "circulation_loan")
public class CirculationLoan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private Integer circulationLogId;
	private boolean isOpen;
	private short action;
	private Integer renewalCount;
	private Date date;
	private String rowId;

	@ManyToOne
	@JoinColumn(name = "circulationLogId", insertable = false, updatable = false)
	private CirculationLog circulationLog;

	@JsonBackReference
	public CirculationLog getCirculationLog() {
		return this.circulationLog;
	}

	public void setCirculationLog(CirculationLog value) {
		this.circulationLog = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCirculationLogId() {
		return circulationLogId;
	}

	public void setCirculationLogId(Integer circulationLogId) {
		this.circulationLogId = circulationLogId;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public short getAction() {
		return action;
	}

	public void setAction(short action) {
		this.action = action;
	}

	public Integer getRenewalCount() {
		return renewalCount;
	}

	public void setRenewalCount(Integer renewalCount) {
		this.renewalCount = renewalCount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

}
