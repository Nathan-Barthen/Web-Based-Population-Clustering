package edu.sru.group1.proj.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.lang.NonNull;

//Class that stores school information (name, address, numStudents, lowGrade, highGrade)
//Used in SchoolInfoDataset.java

/**
 * Main class containing all of the private variables and information from the excel file. Also holds the setter and getter methods. 
 * @author Connor
 *
 */
@Entity
public class SchoolInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NonNull
	private String schoolName;
	@NonNull
	private String address;
	@NonNull
	private String zip;
	@NonNull
	private String city;
	@NonNull
	private String numStudents;
	@NonNull
	private String lowGrade;
	@NonNull
	private String highGrade;
	@NonNull
	private String schoolType;
	@NonNull
	private double schoolLon;
	@NonNull
	private double schoolLat;
	@NonNull
	private int busCapacity;
	@NonNull
	private int numBuses;
	@NonNull
	private int travelTime;
	@NonNull
	private String distName;
	@NonNull
	private String countyName;
	@NonNull
	private int stuPercent;
	
	//Constructor
	/**
	 * Constructor for School Information.
	 */
	public SchoolInformation() {
		busCapacity = 50;
		numBuses = 20;
		travelTime = 50;
		stuPercent = 90;
	}
	//Constructor for testing
	/**
	 * Constructor for testing
	 * @param lowGrade passes low grade level.
	 * @param highGrade passes high grade level.
	 * @param numStudents passes the number of students.
	 * @param schoolName passes the school name.
	 * @param address passes the address name.
	 * @param zip passes the zip code.
	 * @param city passes the city name.
	 * @param schoolType passes the school type.
	 */
	public SchoolInformation(String lowGrade, String highGrade, String numStudents, String schoolName, String address, String zip, String city, String schoolType){
		this.lowGrade = lowGrade;
		this.highGrade = highGrade;
		this.numStudents = numStudents;
		this.schoolName = schoolName;
		this.address = address;
		this.zip = zip;
		this.city = city;
		this.schoolType = schoolType;
	}
	//Setters and Getters
	/**
	 * Getter method for the id.
	 * @return returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * Setter method for the id.
	 * @param id sets the id.
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * Getter method for the school name.
	 * @return returns the name of the school.
	 */
	public String getSchoolName() {
		return schoolName;
	}
	/**
	 * Setter method for the school name.
	 * @param schoolName passes schoolName and sets it equal to the selected name.
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	/**
	 * Getter method for the address.
	 * @return returns the address.
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * Setter method for the address.
	 * @param address passes address and sets it to the selected address.
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Getter method for the zip code.
	 * @return returns the zip.
	 */
	public String getZip() {
		return zip;
	}
	/**
	 * Setter method for the zip code.
	 * @param zip passes zip code and sets it to the selected zip.
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	/**
	 * Getter method for the city name.
	 * @return returns the city name.
	 */
	public String getCity() {
		return city;
	}
	/**
	 * Setter method for the city name.
	 * @param city passes city and sets it to the selected city.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Getter method for the number of students.
	 * @return returns the number of students in a school.
	 */
	public String getNumStudents() {
		return numStudents;
	}
	/**
	 * Setter method for the number of students.
	 * @param numStudents passes numStudents and sets it equal to the correct number.
	 */
	public void setNumStudents(String numStudents) {
		this.numStudents = numStudents;
	}
	/**
	 * Getter method for the low grade level.
	 * @return returns the low grade level students.
	 */
	public String getLowGrade() {
		return lowGrade;
	}
	/**
	 * Setter method for low grade level.
	 * @param lowGrade passes low grade and sets it.
	 */
	public void setLowGrade(String lowGrade) {
		this.lowGrade = lowGrade;
	}
	/**
	 * Getter method for the high grade level.
	 * @return returns the high grade students.
	 */
	public String getHighGrade() {
		return highGrade;
	}
	/**
	 * Setter method for high grade level.
	 * @param highGrade passes highGrade and sets it.
	 */
	public void setHighGrade(String highGrade) {
		this.highGrade = highGrade;
	}
	
	/**
	 * Setter method for school type (High, Middle or Elementary)
	 */
	public void setSchoolType() {
		 if(highGrade.contains("12")) {
			 schoolType = "High School";
		 }
		 else if(highGrade.contains("08") || (highGrade.contains("12") && lowGrade.contains("05") )) {
			 schoolType = "Middle School";
		 }
		 else {
			 schoolType = "Elementary School";
		 }
	}
	/**
	 * Getter method for high grade level.
	 * @return returns the schools type (High, Middle or Elementary).
	 */
	public String getSchoolType() {
		return schoolType;
	}
	
	
	
	/**
	 * Getter method for the schools longitude.
	 * @return returns the schools longitude.
	 */
	public double getSchoolLon() {
		return schoolLon;
	}
	/**
	 * Setter method for schools longitude.
	 * @param schoolLon passes schools longitude and sets it.
	 */
	public void setSchoolLon(double schoolLon) {
		this.schoolLon = schoolLon;
	}
	
	/**
	 * Getter method for the schools latitude.
	 * @return returns the schools latitude.
	 */
	public double getSchoolLat() {
		return schoolLat;
	}
	/**
	 * Setter method for schools latitude.
	 * @param schoolLat passes schools latitude and sets it.
	 */
	public void setSchoolLat(double schoolLat) {
		this.schoolLat = schoolLat;
	}
	
	/**
	 * Getter method for schools bus capacity.
	 * @return returns the schools bus capacity.
	 */
	public int getBusCapacity() {
		return busCapacity;
	}
	/**
	 * Setter method for schools bus capacity.
	 * @param busCapacity passes schools bus capacity and sets it.
	 */
	public void setBusCapacity(int busCapacity) {
		this.busCapacity = busCapacity;
	}
	/**
	 * Getter method for schools number of buses.
	 * @return returns the schools number of buses.
	 */
	public int getNumBuses() {
		return numBuses;
	}
	/**
	 * Setter method for schools numBuses.
	 * @param numBuses passes schools number of buses and sets it.
	 */
	public void setNumBuses(int numBuses) {
		this.numBuses = numBuses;
	}
	/**
	 * Getter method for schools travel time for bus routes.
	 * @return returns the max travel time for bus routes.
	 */
	public int getTravelTime() {
		return travelTime;
	}
	/**
	 * Setter method for schools travel time for bus routes (in minutes).
	 * @param travelTime passes school bus travel time in minutes.
	 */
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}
	/**
	 * Getter method for schools district name.
	 * @return returns the district name.
	 */
	public String getDistName() {
		return distName;
	}
	/**
	 * Setter method to set school district name
	 * @param distName passes school district name
	 */
	public void setDistName(String distName) {
		this.distName = distName;
	}
	/**
	 * Getter method for schools county name.
	 * @return returns the county name.
	 */
	public String getCountyName() {
		return countyName;
	}
	/**
	 * Setter method to set school county name
	 * @param countyName passes school county name
	 */
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	
	
	/**
	 * Getter method for percent of student addresses to cluster
	 * @return returns the percent of student addresses.
	 */
	public int getStuPercent() {
		return stuPercent;
	}
	/**
	 * Setter method to set percent of student addresses to cluster
	 * @param stuPercent passes percent of addresses
	 */
	public void setStuPercent(int stuPercent) {
		this.stuPercent = stuPercent;
	}
	
}
