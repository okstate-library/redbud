package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "library")
public class Library {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private String campus_id;

	private String library_id;

	private String library_name;

	private String library_code;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	 * @return the library_name
	 */
	public String getLibrary_name() {
		return library_name;
	}

	/**
	 * @param library_name the library_name to set
	 */
	public void setLibrary_name(String library_name) {
		this.library_name = library_name;
	}

	/**
	 * @return the library_code
	 */
	public String getLibrary_code() {
		return library_code;
	}

	/**
	 * @param library_code the library_code to set
	 */
	public void setLibrary_code(String library_code) {
		this.library_code = library_code;
	}
	

}