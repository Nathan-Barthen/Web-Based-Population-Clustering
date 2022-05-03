package edu.sru.group1.proj.dbscan;

import java.util.*;

import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.Algorithm;
import edu.sru.group1.proj.dbscan.Cluster;

/**
 * Resources:
 * Incorrect example: https://www.dataonfocus.com/dbscan-java-code/
 * Pseudocode example: https://www.researchgate.net/publication/325059373/figure/fig2/AS:624653831790593@1525940487951/Pseudocode-of-the-DBSCAN-algorithm.png
 * @author Connor
 *
 */
public class DBScan implements Algorithm {
	/**
	 * variable for the list of DataPoints.
	 */
	public List<DataPoint> points;
	/**
	 * variable for the list of clusters.
	 */
    private List<Cluster> clusters;
    /**
     * variable for max distance.
     */
	public double max_distance;
	/**
	 * variable for the minimum number of points to control cluster size.
	 */
	public int min_points;
	/**
	 * visited variable, to check if a point has been accessed.	
	 */
	public boolean[] visited;
/**
 * Uses DBScane to scan the points, distances, and clusters.	
 * @param max_distance passes the maximum distance.
 * @param min_points passes the minimum points needed.
 */
	public DBScan(double max_distance, int min_points) {
		this.points = new ArrayList();
		this.clusters = new ArrayList();
		this.max_distance = max_distance;
		this.min_points = min_points;
	}
/**
 * Iterates through the points in the cluster checking neighbors and visited points.
 */
	public void cluster() {
		Iterator<DataPoint> it = points.iterator();
		int n = 0;
		while(it.hasNext()) {
			DataPoint d = it.next();
			if(d.isVisit() == false) {
				d.setVisit(true);
				visited[n] = true;
				List<DataPoint> neighbors = getNeighbors(d);
				if(neighbors.size() >= min_points) {
					Cluster c = new Cluster(clusters.size());
					buildCluster(d,c,neighbors);
				}
			}
			n++;
			
		}
	}
 /**
  * Builds the clusters at the points accounting for neighbors and visited points.
  * @param d passes the DataPoint d.
  * @param c passes the given Cluster c.
  * @param neighbors passes a list of the neighbor DataPoints.
  */
	private void buildCluster(DataPoint d, Cluster c, List<DataPoint> neighbors) {
		c.addPoint(d);		
		int j =0;
		
		List<DataPoint> newNeighbors = new ArrayList();
		for (DataPoint point : neighbors) {
			if(point.isVisit() == false) {
				point.setVisit(true);
				List<DataPoint> temp = getNeighbors(point);
				if(newNeighbors.size() >= min_points) {
					newNeighbors.addAll(temp);
				}
			}
			if( point.getCluster() == -1) {
				c.addPoint(point);
			}
		}
		if(c.getPoints().size() > min_points) {
			clusters.add(c);
		}
	}
 /**
  * Method for the datapoint lists and getting the neighbor datapoints.
  * @param d passes the DataPoint d.
  * @return returns the neighbors.
  */
	private List<DataPoint> getNeighbors(DataPoint d) {
		List<DataPoint> neighbors = new ArrayList();
		for (DataPoint point : points) {
			//Was- double distance = d.distance(point);
			double distance = point.calcDistance(d);
			if(distance <= max_distance) {
				point.setDistance(distance);
				neighbors.add(point);
			}
		}
		return neighbors;
	}
/**
 * Setter method for the list of points. 
 */
	public void setPoints(List<DataPoint> points) {
		this.points = points;
		this.visited = new boolean[points.size()];
	}
	/**
	 * Getter method for clusters.
	 * @return returns the given clusters.
	 */	
	public List<Cluster> getClusters(){
		return clusters;
	}
	
}
