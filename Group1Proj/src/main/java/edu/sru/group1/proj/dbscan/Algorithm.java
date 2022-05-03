package edu.sru.group1.proj.dbscan;

import java.util.List;

import edu.sru.group1.proj.dbscan.DataPoint;

/**
 * Interface class that calls the needed methods for the algorithm.
 * @author Connor
 *
 */
public interface Algorithm {
/**
 * calls the setPoints method. 
 * @param points passes the given points.
 */
public void setPoints(List<DataPoint> points);
/**
 * calls the cluster method.
 */
public void cluster();
 
}

