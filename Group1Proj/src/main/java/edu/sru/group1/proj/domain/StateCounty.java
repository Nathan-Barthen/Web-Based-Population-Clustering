package edu.sru.group1.proj.domain;


import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.lang.NonNull;

/*
 * Structure for the database to hold the information on the user
 * 
 * @Entity, @Id and @Generated value should be from the javax.persistence library
 * @NonNull is from the org.springwork library 
 * 
 * As the User class is designated as a @Entity, the JPA (Java Persistence API), which is Hibernate, will be able to perform CRUD 
 * (Create, Read, Update, Delete) operations on the domain entities.
 * 
 * The name and e-mail have been constrained to @NoNull values and allows the Hibernate Validator for validating the constrained
 * fields before persisting or updating an entity to the database.
 */

//Class that stores the state, county, and district information.
//Used in CountyDataset.java

/**
 * Structure for the database to hold the information on the user.
 * Main Class that stores the state, county, and district information.
 * Used in CountyDataset.java
 * @author Connor
 */
@Entity
public class StateCounty {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NonNull
	private String state;
	@NonNull
	private String county;
	@javax.persistence.Lob  
	private Vector<String> district;
	
	/**
	 * Getter method for the id.
	 * @return returns the id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setter method for the id.
	 * @param id sets the appropriate id. 
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Getter method for State.
	 * @return returns the state name.
	 */
	public String getState() {
		return state;
	}

	/**
	 * Setter method for State.
	 * @param state sets the state to the correct name.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Getter method for the county.
	 * @return returns the county.
	 */
	public String getCounty() {
		return county;
	}

	/**
	 * Setter method for the county.
	 * @param county sets the county name.
	 */
	public void setCounty(String county) {
		this.county = county;
	}

	/**
	 * Getter method for the district.
	 * @return returns the district.
	 */
	public Vector<String> getDistrict() {
		return district;
	}

	/**
	 * Setter method for the district.
	 * @param district sets the district to the appropriate district.
	 */
	public void setDistrict(Vector<String> district) {
		this.district = district;
	}
	
	
}
