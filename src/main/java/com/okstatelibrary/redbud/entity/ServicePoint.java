package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_point")
public class ServicePoint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private String servicepoint_id;

	private String name;

	private String code;

	/**
	 * @return the servicepoint_id
	 */
	public String getServicepoint_id() {
		return servicepoint_id;
	}

	/**
	 * @param servicepoint_id the servicepoint_id to set
	 */
	public void setServicepoint_id(String servicepoint_id) {
		this.servicepoint_id = servicepoint_id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

}