package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "campus")
public class Campus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private String institution_id;

	private String campus_id;

	private String campus_name;
	
	private String campus_code;

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
	 * @return the campus_id
	 */
	public String getCampus_id() {
		return campus_id;
	}

	/**
	 * @param campus_id the campus_id to set
	 */
	public void setCampus_id(String campus_id) {
		this.campus_id = campus_id;
	}

	/**
	 * @return the campus_name
	 */
	public String getCampus_name() {
		return campus_name;
	}

	/**
	 * @param campus_name the campus_name to set
	 */
	public void setCampus_name(String campus_name) {
		this.campus_name = campus_name;
	}

	/**
	 * @return the campus_code
	 */
	public String getCampus_code() {
		return campus_code;
	}

	/**
	 * @param campus_code the campus_code to set
	 */
	public void setCampus_code(String campus_code) {
		this.campus_code = campus_code;
	}

	
	

}