package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "location")
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private String institution_id;

	private String campus_id;

	private String library_id;

	private String location_id;

	private String location_name;

	private String location_code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the location_id
	 */
	public String getLocation_id() {
		return location_id;
	}

	/**
	 * @param location_id the location_id to set
	 */
	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}

	/**
	 * @return the location_name
	 */
	public String getLocation_name() {
		return location_name;
	}

	/**
	 * @param location_name the location_name to set
	 */
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	/**
	 * @return the library_id
	 */
	public String getLibrary_id() {
		return library_id;
	}

	/**
	 * @param library_id the library_id to set
	 */
	public void setLibrary_id(String library_id) {
		this.library_id = library_id;
	}

	/**
	 * @return the location_code
	 */
	public String getLocation_code() {
		return location_code;
	}

	/**
	 * @param location_code the location_code to set
	 */
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
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

}