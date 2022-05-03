package edu.sru.group1.proj.shapefiles;

/**
 * Main class holding the record of the lon, lat, and cluster number.
 * @author Connor
 *
 */
public class Record {
	
	private double lon;
	private double lat;
	private int clusterNumber;
	
	
	
/**
 * method holding the lon and lat records.
 * @param lon passes the longitude.
 * @param lat passes the latitude.
 */
	public Record(double lon, double lat) {
		super();
		this.lon = lon;
		this.lat = lat;
	}
/**
 * Getter method for longitude.	
 * @return returns longitude.
 */
	public double getLon() {
		return lon;
	}
/**
 * Setter method for the longitude.
 * @param lon passes the longitude.
 */
	public void setLon(double lon) {
		this.lon = lon;
	}
/**
 * Getter method for latitude.
 * @return returns the latitude.
 */
	public double getLat() {
		return lat;
	}
/**
 * Setter method for latitude.
 * @param lat passes the latitude.
 */
	public void setLat(double lat) {
		this.lat = lat;
	}
/**
 * Getter method for cluster number.
 * @return returns the cluster number.
 */
	public int getClusterNumber() {
		return clusterNumber;
	}
/**
 * Setter method for the cluster number.
 * @param clusterNumber passes the cluster number.
 */
	public void setClusterNumber(int clusterNumber) {
		this.clusterNumber = clusterNumber;
	}
/**
 * returns a string of the three variables.
 */
	@Override
	public String toString() {
		return "Record [lon= " + lon + ", lat= " + lat + ", clusterNumber= "+ clusterNumber + "]";
	}
	

}