package edu.sru.group1.proj.domain;

public class AddrsPerCluster {
	private int index;
	private int numAddr;
	private String schoolName;
	
	AddrsPerCluster(){
		index=0;
		numAddr=0;
	}
	
	public AddrsPerCluster(int index, int numAddr, String schoolName){
		this.index = index;
		this.numAddr = numAddr;
		this.schoolName = schoolName;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getNumAddr() {
		return numAddr;
	}
	public void setNumAddr(int numAddr) {
		this.numAddr = numAddr;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
}
