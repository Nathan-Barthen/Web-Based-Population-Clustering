package edu.sru.group1.proj.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.lang.NonNull;

/**
 * Structure for the database to hold the information on the user.
 * Mainly for email so we can store the users email, and use SMTP protocol
 * to email them the routing file.
 * @author Connor
 */
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
	@NonNull
	private String title;
	@NonNull
	private String state;
	@NonNull
	private String county;
	@NonNull
	private String district;
	
	
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
	 * Getter method for first name
	 * @return returns the user's first name.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * Setter method for first name.
	 * @param firstName sets the users first name. 
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * Getter method for last name
	 * @return returns the user's last name.
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * Setter method for last name.
	 * @param lastName sets the users last name. 
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Getter method for email.
	 * @return returns the user's email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * Setter method for email.
	 * @param email sets the users email. 
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Getter method for title.
	 * @return returns the user's title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Setter method for the user's title.
	 * @param title sets the users title. 
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Getter method for state.
	 * @return returns the user's state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * Setter method for the user's state.
	 * @param state sets the users state. 
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Getter method for county.
	 * @return returns the user's county.
	 */
	public String getCounty() {
		return county;
	}
	/**
	 * Setter method for the user's county.
	 * @param county sets the users county. 
	 */
	public void setCounty(String county) {
		this.county = county;
	}
	
	/**
	 * Getter method for district.
	 * @return returns the user's district.
	 */
	public String getDistrict() {
		return district;
	}
	/**
	 * Setter method for the user's district.
	 * @param district sets the users district. 
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	
	
}


