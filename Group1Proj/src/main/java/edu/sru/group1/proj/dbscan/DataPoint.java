package edu.sru.group1.proj.dbscan;

/**
 * Main class for creating the DataPoints.
 * @author Connor
 *
 */
public class DataPoint {
	
	double distance;
	private double lon;
	private double lat;
	private int clusterNumber;
	boolean visit;
	
	
/**
 * Method for setting the latitude and longitude points.
 * @param lon passes longitude.
 * @param lat passed latitude.
 */
	public DataPoint(double lon, double lat) {
		super();
		this.lon = lon;
		this.lat = lat;
		this.clusterNumber = -1;
		this.visit=false;
	}
/**
 * Getter method for the distance.
 * @return returns the given distance. 
 */
	public double getDistance() {
		return distance;
	}
/**
 * Setter method for the distance.
 * @param distance passed the given distance.
 */	
	public void setDistance(double distance) {
		this.distance = distance;
	}
/**
 * method for calculating the distance.	
 * @param datapoint passes the given datapoints.
 * @return returns the lon and lat distances. 
 */	
	public double calcDistance(DataPoint datapoint) {
		return Math.sqrt( Math.pow((  this.lon - datapoint.getLon() ), 2) + 
				   Math.pow(( this.lat - datapoint.getLat() ), 2));	
	}
	
/**
 * Getter method for longitude.
 * @return returns the longitude.
 */	
	public double getLon() {
		return lon;
	}
/**
 * Setter method for longitude.
 * @param lon passes longitude.
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
 * Setter method for the latitude.
 * @param lat passes the latitude.
 */
	public void setLat(double lat) {
		this.lat = lat;
	}
/**
 * Getter method for the cluster.
 * @return returns the cluster number.
 */	
	public int getCluster() {
		return clusterNumber;
	}
/**
 * Setter method for the cluster.
 * @param clusterNumber passed the cluster number.
 */
	public void setCluster(int clusterNumber) {
		this.clusterNumber = clusterNumber;
	}
/**
 * Checks to see if the location has already been visited.
 * @return returns the visit variable.
 */	
	public boolean isVisit() {
		return visit;
	}
/**
 * Setter method for visit.
 * @param visit passed the visit variable.
 */
	public void setVisit(boolean visit) {
		this.visit = visit;
	}

	@Override
	public String toString() {
		return "DataPoint [lon= " + lon + ", lat= " + lat + ", distance= " + distance + ", clusterNumber= "+ clusterNumber + "]";
	}
}
