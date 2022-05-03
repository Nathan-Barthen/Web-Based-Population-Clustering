package edu.sru.group1.proj.dbscan;

import java.util.*;

import edu.sru.group1.proj.dbscan.DataPoint;

/**
 * 
 * Main Class that creates the clusters with the get and set methods. 
 *
 */
public class Cluster {
	/**
	 * List of data points variable.
	 */
	private List<DataPoint> points;
	/**
	 * Variable for the center point of the cluster.
	 */
	private DataPoint centroid;
	/**
	 * variable to hold the cluster id.
	 */
	private int id;
	private String schoolName;
/**
 * creates an array for the cluster.
 * @param id passed the cluster id.
 */	
	Cluster(int id) {
		this.id = id;
		this.points = new ArrayList();
		this.centroid = null;
	}
/**
 	* Getter method for DataPoints.
	* @return returns points.
 */
	public List<DataPoint> getPoints() {
		return points;
	}
/**
 * Setter method for DataPoints, also adds points.
 * @param point passes the specified point.
 */	
	public void addPoint(DataPoint point) {
		point.setCluster(id);
		points.add(point);
	}
 /**
  * Setter method for DataPoints.
  * @param points passed the specified point.
  */
	public void setPoints(List<DataPoint> points) {
		this.points = points;
	}
/**
 * Getter method for the central point.
 * @return returns the centroid or middle point.
 */
	public DataPoint getCentroid() {
		return centroid;
	}
/**
 * Setter method for the central point.
 * @param centroid passes the center point.
 */
	public void setCentroid(DataPoint centroid) {
		this.centroid = centroid;
	}
/**
 * calculates and gets the center point in a cluster.	
 */
	public void calculateCentroid() {
		double lonTotal = 0;
		double latTotal = 0;
		int size = points.size();
		for(DataPoint point : points) {
			lonTotal = lonTotal + point.getLon();
			latTotal = latTotal + point.getLat();
		}
		DataPoint centroid = new DataPoint(lonTotal/size, latTotal/size);
		centroid.setCluster(this.id);
		this.centroid = centroid;
		
	}
/**
 * Getter method for the id.
 * @return returns the id.
 */
	public int getId() {
		return id;
	}
public String getSchoolName() {
	return schoolName;
}
public void setSchoolName(String schoolName) {
	this.schoolName = schoolName;
}
/**
 * Method for clearing the points.
 */	
	public void clear() {
		points.clear();
	}
/**
 * plots the points for the given cluster.
 */	
	public void plotCluster() {
		System.out.println("[Cluster: " + id+"]");
		//System.out.println("[Centroid: Lon= " + centroid.getLon()+ " Lat= " + centroid.getLat()+ "]");
		System.out.println("[Points:");
		for(DataPoint p : points) {
			System.out.println(p);
		}
		System.out.println("Size: " + points.size());
		System.out.println("]");
	}
	
}
