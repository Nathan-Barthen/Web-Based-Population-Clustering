package edu.sru.group1.proj.domain;

import java.util.List;
import java.util.Vector;

import edu.sru.group1.proj.dbscan.Cluster;

public class AllClusteringInfo {
	private int totalStuAddr;
	
	private List< SchoolInformation > schoolsInfo;
	private List<Cluster> clusterInfo;
	private Vector<AddrsPerCluster> addrsPerCluster;
	private int setIndex;
	
	public AllClusteringInfo(int totalStuAddr, List< SchoolInformation > schoolsInfo, List<Cluster> clusterInfo, Vector<AddrsPerCluster> addrsPerCluster){
		this.totalStuAddr = totalStuAddr;
		this.schoolsInfo = schoolsInfo;
		this.clusterInfo = clusterInfo;
		this.addrsPerCluster = addrsPerCluster;
	}
	
	public int getTotalStuAddr() {
		return totalStuAddr;
	}
	public void setTotalStuAddr(int totalSruAddr) {
		this.totalStuAddr = totalSruAddr;
	}
	public Vector<AddrsPerCluster> getAddrsPerCluster() {
		return addrsPerCluster;
	}
	public void setAddrsPerCluster(Vector<AddrsPerCluster> addrsPerCluster) {
		this.addrsPerCluster = addrsPerCluster;
	}
	public List<SchoolInformation> getSchoolsInfo() {
		return schoolsInfo;
	}
	public void setSchoolsInfo(List<SchoolInformation> schoolsInfo) {
		this.schoolsInfo = schoolsInfo;
	}
	public List<Cluster> getClusterInfo() {
		return clusterInfo;
	}
	public void setClusterInfo(List<Cluster> clusterInfo) {
		this.clusterInfo = clusterInfo;
	}

	public int getSetIndex() {
		return setIndex;
	}

	public void setSetIndex(int setIndex) {
		this.setIndex = setIndex;
	}
	
}
