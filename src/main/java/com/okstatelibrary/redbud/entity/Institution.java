package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "institution")
public class Institution {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private String institution_id;

	private String institution_name;

	private String institution_code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the institution_id
	 */
	public String getInstitution_id() {
		return institution_id;
	}

	/**
	 * @param institution_id the institution_id to set
	 */
	public void setInstitution_id(String institution_id) {
		this.institution_id = institution_id;
	}

	/**
	 * @return the institution_name
	 */
	public String getInstitution_name() {
		return institution_name;
	}

	/**
	 * @param institution_name the institution_name to set
	 */
	public void setInstitution_name(String institution_name) {
		this.institution_name = institution_name;
	}

	/**
	 * @return the institution_code
	 */
	public String getInstitution_code() {
		return institution_code;
	}

	/**
	 * @param institution_code the institution_code to set
	 */
	public void setInstitution_code(String institution_code) {
		this.institution_code = institution_code;
	}

	
}