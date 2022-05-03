package edu.sru.group1.proj.dbscan;


import edu.sru.group1.proj.domain.SchoolInformation;

import java.util.List;

import edu.sru.group1.proj.dbscan.Cluster;

/**
 * Main class for getting the school information through the use of clustering.
 * @author Connor
 *
 */
public class SchoolInfoAndCluster {
	private SchoolInformation schoolInfo;
	private List<Cluster> cluster;
	
	SchoolInfoAndCluster(){
	}
/**
 * Gets the school information for a given cluster.
 * @param schoolInfo passes the given School Information.
 * @param cluster passes the given cluter used.
 */
	SchoolInfoAndCluster(SchoolInformation schoolInfo, List<Cluster> cluster){
		this.schoolInfo = schoolInfo;
		this.cluster = cluster;
	}
/**
 * Getter method for School Information.
 * @return returns the school information.
 */
	public SchoolInformation getSchoolInfo() {
		return schoolInfo;
	}
/**
 * Setter method for the school information.
 * @param schoolInfo passes the school information.
 */
	public void setSchoolInfo(SchoolInformation schoolInfo) {
		this.schoolInfo = schoolInfo;
	}
/**
 * Getter method for the cluster.
 * @return returns the given cluster.
 */
	public List<Cluster> getCluster() {
		return cluster;
	}
/**
 * Setter method for the cluster.
 * @param cluster passes the given cluster.
 */
	public void setCluster(List<Cluster> cluster) {
		this.cluster = cluster;
	}
	
}
