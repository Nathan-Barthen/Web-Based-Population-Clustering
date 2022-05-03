package edu.sru.group1.proj.dbscan;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.sru.group1.proj.domain.SchoolInformation;

/**
 * Uses large clusters and randomly gets addresses proportional to the size of the clusters and number of students.
 * @author Connor
 *
 */
public class ClustersForSchool {
/**
 * Class for generating school clusters
 * @param largeClusters passes the large clusters
 * @param allSchools passes all of the schools given
 * @param schoolsToCluster passes the schools that are to be clustered
 * @return returns every schools information
 */
	public static List< SchoolInfoAndCluster > generateSchoolClusters(List<Cluster> largeClusters, Vector<SchoolInformation> allSchools, Vector<SchoolInformation> schoolsToCluster) {
		List< SchoolInfoAndCluster > everySchoolsInfo = new ArrayList();
		//Take largeClusters and randomly select addresses proportional to cluster size / number of students.
		
		boolean middleSchoolExists = false;
		boolean highSchoolExists = false;
		boolean elementarySchoolExists = false;
		double mergedEleSchSize = 0;
		double mergedMiddleSchSize = 0;
		double totalStudents = 0;
		
		SelectAddressesByCluster selectAddr = new SelectAddressesByCluster();

		SchoolInformation highSchool = new SchoolInformation();
		Vector<SchoolInformation> middleSchools = new Vector<SchoolInformation>();
		Vector<SchoolInformation> elementarySchools = new Vector<SchoolInformation>();
		
		//Check first if a middle school exists (some schools have Middle/High merged by default) AND stores ALL schools.
		for(SchoolInformation school : schoolsToCluster) {
			if(school.getSchoolType() == "High School") {
				highSchoolExists = true;
				highSchool = school;
			}
			if(school.getSchoolType() == "Middle School") {
				double midStudents = Double.parseDouble(school.getNumStudents());
				midStudents = midStudents * ((double)(school.getStuPercent()) / 100);
				mergedMiddleSchSize = mergedMiddleSchSize + midStudents;
				middleSchools.add(school);
				middleSchoolExists = true;
			}
			if(school.getSchoolType() == "Elementary School") {
				elementarySchoolExists = true;
				double eleStudents = Double.parseDouble(school.getNumStudents());
				eleStudents = eleStudents * (school.getStuPercent() / 100);
				mergedEleSchSize = mergedEleSchSize + eleStudents;
				elementarySchools.add(school);
			}
			totalStudents = totalStudents + ( Double.parseDouble(school.getNumStudents()) * (((double) school.getStuPercent()) / 100));
		}
		
		//HIGH/MIDDLE SCHOOL Clustering
		//If no elementary schools were passed for clustering
		if(!elementarySchoolExists) {
				if(middleSchoolExists && highSchoolExists) {
					double highSchStudents = Double.parseDouble(highSchool.getNumStudents());
					highSchStudents = highSchStudents * (((double)highSchool.getStuPercent())/100);
					List<Cluster> highAndMiddleClusters = new ArrayList();
					
					//Note: this means everySchoolsInfo will have duplicate data, if high school and middle are merged (affects later work)
					//Generates the cluster list containing student addresses.
					highAndMiddleClusters = selectAddr.breakUpAddrsByCluster(largeClusters, (int) (highSchStudents+mergedMiddleSchSize));
					for(SchoolInformation midSchool : middleSchools) {
						//Makes and sets instance of SchoolInfoAndCluster (contains school Information and cluster Information)
						SchoolInfoAndCluster mergedMiddleSchool = new SchoolInfoAndCluster(midSchool, highAndMiddleClusters);
						//Adds school w/ clustering information to everySchoolsInfo
						everySchoolsInfo.add(mergedMiddleSchool);
					}
					SchoolInfoAndCluster mergedHighSchool = new SchoolInfoAndCluster(highSchool, highAndMiddleClusters);
					for(Cluster clust : mergedHighSchool.getCluster()) {
						clust.setSchoolName(highSchool.getSchoolName());
					}
					everySchoolsInfo.add(mergedHighSchool);
					
					return everySchoolsInfo;
				}
				else { 
					if(middleSchoolExists) {
						List<Cluster> middleClusters = new ArrayList();
						//Generates the cluster list containing student addresses.
						middleClusters = selectAddr.breakUpAddrsByCluster(largeClusters, (int) mergedMiddleSchSize);
						for(SchoolInformation midSchool : middleSchools) {
							//Makes and sets instance of SchoolInfoAndCluster (contains school Information and cluster Information)
							SchoolInfoAndCluster mergedMiddleSchool = new SchoolInfoAndCluster(midSchool, middleClusters);
							//Adds school w/ clustering information to everySchoolsInfo
							if(middleSchools.size() ==1) {
								for(Cluster clust : mergedMiddleSchool.getCluster()) {
									clust.setSchoolName(midSchool.getSchoolName());
								}
							}
							else {
								for(Cluster clust : mergedMiddleSchool.getCluster()) {
									clust.setSchoolName("Entire District");
								}
							}
							everySchoolsInfo.add(mergedMiddleSchool);
						}
						
						
						return everySchoolsInfo;
					}
				 	if(highSchoolExists) {
						double highSchStudents = Double.parseDouble(highSchool.getNumStudents());
						highSchStudents = highSchStudents * (((double)highSchool.getStuPercent())/100);
						List<Cluster> highClusters = new ArrayList();
						//Generates the cluster list containing student addresses.
						highClusters = selectAddr.breakUpAddrsByCluster(largeClusters, (int) highSchStudents);
						//Makes and sets instance of SchoolInfoAndCluster (contains school Information and cluster Information)
						SchoolInfoAndCluster highSchoolInfo = new SchoolInfoAndCluster(highSchool, highClusters);
						//Adds school w/ clustering information to everySchoolsInfo
						for(Cluster clust : highSchoolInfo.getCluster()) {
							clust.setSchoolName(highSchool.getSchoolName());
						}
						everySchoolsInfo.add(highSchoolInfo);
						
						return everySchoolsInfo;
				 	}
				}
				
		}
		

		//ELEMENTARY SCHOOL CLUSTERING (High School and/or Middle School was not passed)
		if(!middleSchoolExists && !highSchoolExists) {
			
			List< SchoolInfoAndCluster > tempEverySchoolsInfo = new ArrayList();
			
			//Create everySchoolsInfo instances for elementary school to cluster 
			for(SchoolInformation eleSchool : elementarySchools) {
				List<Cluster> blankClusters = new ArrayList();
				SchoolInfoAndCluster eleSchoolInfo = new SchoolInfoAndCluster(eleSchool, blankClusters);
				everySchoolsInfo.add(eleSchoolInfo);
			}
			//Create tempEverySchoolsInfo instances for every elementary school (even if its not clustered)
			for(SchoolInformation eleSchool : allSchools) {
				if(eleSchool.getSchoolType().contains("Elementary School")) {
					List<Cluster> blankClusters = new ArrayList();
					SchoolInfoAndCluster eleSchoolInfo = new SchoolInfoAndCluster(eleSchool, blankClusters);
					tempEverySchoolsInfo.add(eleSchoolInfo);
				}
			}
			
			List< List<Cluster> > splitUpClusters = new ArrayList();
			//Loop throught ever cluster in largeClusters
			for(Cluster cluster : largeClusters) {
				double stortestDistance = 2147483647; //Max integer value.
				SchoolInformation closestSchool = new SchoolInformation();
				//Loop through every elementary school - determine which school closest to the given cluster.
				double distancetoCluster = 0;
				for(SchoolInformation school : allSchools) {
					if(school.getSchoolType().contains("Elementary School")) {
						distancetoCluster = ( Math.sqrt( Math.pow(( school.getSchoolLon() - cluster.getCentroid().getLon() ), 2) + 
								   			  			 Math.pow(( school.getSchoolLat() - cluster.getCentroid().getLat() ), 2)) );	
						if(distancetoCluster < stortestDistance) {
							stortestDistance = distancetoCluster;
							closestSchool = school;
						}
					}
				}
				//Loop through all elementary schools in tempEverySchoolsInfo add cluster to closest school.
				for(SchoolInfoAndCluster schInfoAndCluster : tempEverySchoolsInfo) {
					//Update elementary schools cluster for ALL elementary schools
					if(schInfoAndCluster.getSchoolInfo() == closestSchool) {
						List<Cluster> clust = schInfoAndCluster.getCluster();
						clust.add(cluster);
						schInfoAndCluster.setCluster(clust);
					}
				}
				//Update elementary schools cluster for SELECTED elementary schools
				for(SchoolInfoAndCluster selectedSchInfoAndCluster : everySchoolsInfo) {
					for(SchoolInfoAndCluster schInfoAndCluster : tempEverySchoolsInfo) {
						if(selectedSchInfoAndCluster.getSchoolInfo() == schInfoAndCluster.getSchoolInfo()) {
							List<Cluster> clust1 = schInfoAndCluster.getCluster();
							selectedSchInfoAndCluster.setCluster(clust1);
						}
					}
				}
			}
			
			List<Cluster> allElementaryClusteredAddrs = new ArrayList();
			//Randomly select address for each elementary school
			for(SchoolInfoAndCluster schInfoAndCluster : everySchoolsInfo) {
				if(schInfoAndCluster.getSchoolInfo().getSchoolType().contains("Elementary School")) {
					List<Cluster> elementaryClusteredAddrs = new ArrayList();
					//Num of students that go to the elementary school
					double numStudentsInSchool = Double.parseDouble(schInfoAndCluster.getSchoolInfo().getNumStudents());

					numStudentsInSchool = numStudentsInSchool * (((double) schInfoAndCluster.getSchoolInfo().getStuPercent()) / 100);
					//Randomly selected addresses, stores a list of clusters in elementaryClusteredAddrs

					int addrsInClusters = 0;
					for(Cluster cluster : schInfoAndCluster.getCluster()) {
						addrsInClusters += cluster.getPoints().size();
					}
					
					//If clustering has inadequate number of addresses, just use largeClusters to pick addresses.
					if(addrsInClusters < numStudentsInSchool) {
						SchoolInfoAndCluster schoolduplicate = new SchoolInfoAndCluster();
						schoolduplicate.setCluster(largeClusters);
						schoolduplicate.setSchoolInfo(schInfoAndCluster.getSchoolInfo());
						elementaryClusteredAddrs = selectAddr.breakUpAddrsByCluster(schoolduplicate.getCluster(), (int) numStudentsInSchool);
					}
					else {
						elementaryClusteredAddrs = selectAddr.breakUpAddrsByCluster(schInfoAndCluster.getCluster(), (int) numStudentsInSchool);
					}
					for(Cluster clust : elementaryClusteredAddrs) {
						clust.setSchoolName(schInfoAndCluster.getSchoolInfo().getSchoolName());
					}
					//Add the randomly selected address FROM THAT SCHOOL to allElementaryClusteredAddrs 
					allElementaryClusteredAddrs.addAll(elementaryClusteredAddrs);
				}
			}
			//Loop through every elementary school and setCluster to allElementaryClusteredAddrs
			for(SchoolInfoAndCluster schInfoAndCluster : everySchoolsInfo) {
				schInfoAndCluster.setCluster(allElementaryClusteredAddrs);
			}
			return everySchoolsInfo;
		}
		
		
		//If they passed BOTH ELEMENTARY schools AND MIDDLE/HIGH school.
		List<Cluster> allSchoolsClusteredAddrs = new ArrayList();
		allSchoolsClusteredAddrs = selectAddr.breakUpAddrsByCluster(largeClusters, (int) totalStudents);
		//Add schoolsInfo + clusters of all schools passed combined to everySchoolsInfo
		for(SchoolInformation school : schoolsToCluster) {
			List<Cluster> clusters = new ArrayList();
			SchoolInfoAndCluster allSchoolInfo = new SchoolInfoAndCluster(school, allSchoolsClusteredAddrs);
			for(Cluster clust : allSchoolInfo.getCluster()) {
				clust.setSchoolName("High/Middle School");
			}
			everySchoolsInfo.add(allSchoolInfo);
		}
		
		return everySchoolsInfo;
	}
	
	
	
	
	
	
	
	
	/**
	 * Class for generating school clusters.
	 * @param largeClusters passes the large clusters
	 * @param districtSchools passes the districts schools
	 * @param mergeMiddleAndHigh passes the middle and high schools
	 * @param mergeElementarySchools passes the merge for elementary schools
	 * @return returns every schools information
	 */
	public List< SchoolInfoAndCluster > generateSchoolsClusters(List<Cluster> largeClusters, Vector<SchoolInformation> districtSchools, boolean mergeMiddleAndHigh, boolean mergeElementarySchools) {
		List< SchoolInfoAndCluster > everySchoolsInfo = new ArrayList();
		//Take largeClusters and randomly select addresses proportional to cluster size / number of students.
		
		boolean middleSchoolExists = false;
		int mergedEleSchSize = 0;
		
		SelectAddressesByCluster selectAddr = new SelectAddressesByCluster();
		
		
		
		SchoolInformation highSchool = new SchoolInformation();
		SchoolInformation middleSchool = new SchoolInformation();
		Vector<SchoolInformation> elementarySchools = new Vector<SchoolInformation>();
		
		//Check first if a middle school exists (some schools have Middle/High merged by default) AND stores ALL schools.
		for(SchoolInformation school : districtSchools) {
			if(school.getSchoolType() == "High School") {
				highSchool = school;
			}
			if(school.getSchoolType() == "Middle School") {
				middleSchool = school;
				middleSchoolExists = true;
			}
			if(school.getSchoolType() == "Elementary School") {
				int eleStudents = Integer.parseInt(school.getNumStudents());
				mergedEleSchSize = mergedEleSchSize + eleStudents;
				elementarySchools.add(school);
			}
		}
		
		//HIGH/MIDDLE SCHOOL Clustering
		//If there district has a middle school and they want to merge middle and high school clustering
		if(middleSchoolExists && mergeMiddleAndHigh) {
				int highSchStudents = Integer.parseInt(highSchool.getNumStudents());
				int middleSchStudents = Integer.parseInt(middleSchool.getNumStudents());
				List<Cluster> highAndMiddleClusters = new ArrayList();
				
				//Note: this means everySchoolsInfo will have duplicate data, if high school and middle are merged (affects later work)
				highAndMiddleClusters = selectAddr.breakUpAddrsByCluster(largeClusters, (highSchStudents+middleSchStudents));
				SchoolInfoAndCluster mergedMiddleSchool = new SchoolInfoAndCluster(middleSchool, highAndMiddleClusters);
				everySchoolsInfo.add(mergedMiddleSchool);
				SchoolInfoAndCluster mergedHighSchool = new SchoolInfoAndCluster(highSchool, highAndMiddleClusters);
				everySchoolsInfo.add(mergedHighSchool);
		}
		//Want to cluster middle/high separately
		else {
			//Do middle school clustering
			if(middleSchoolExists) {
				int middleSchStudents = Integer.parseInt(middleSchool.getNumStudents());
				List<Cluster> middleSchClusters = new ArrayList();
				
				middleSchClusters = selectAddr.breakUpAddrsByCluster(largeClusters, middleSchStudents);
				SchoolInfoAndCluster middleSchoolInfo = new SchoolInfoAndCluster(middleSchool, middleSchClusters);
				
				everySchoolsInfo.add(middleSchoolInfo);
			}
			//Do high school clustering
			int highSchStudents = Integer.parseInt(highSchool.getNumStudents());
			List<Cluster> highSchClusters = new ArrayList();
			
			highSchClusters = selectAddr.breakUpAddrsByCluster(largeClusters, highSchStudents);
			SchoolInfoAndCluster highSchoolInfo = new SchoolInfoAndCluster(highSchool, highSchClusters);
			
			everySchoolsInfo.add(highSchoolInfo);
		}
		
		
		//ELEMENTARY SCHOOL Clustering
		//If they want the elementary school bussing merged into one.
		if(mergeElementarySchools) {
			List<Cluster> mergedEleSchClusters = new ArrayList();
			mergedEleSchClusters = selectAddr.breakUpAddrsByCluster(largeClusters, mergedEleSchSize);
			for(SchoolInformation school : elementarySchools) {
				SchoolInfoAndCluster eleSchoolInfo = new SchoolInfoAndCluster(school, mergedEleSchClusters);
				everySchoolsInfo.add(eleSchoolInfo);
			}
		}
		//They want elementary school bussing done separately.
		else {
			//First add the schools to everySchoolsInfo with blank cluster.
			for(SchoolInformation eleSchool : elementarySchools) {
				List<Cluster> blankClusters = new ArrayList();
				SchoolInfoAndCluster eleSchoolInfo = new SchoolInfoAndCluster(eleSchool, blankClusters);
				everySchoolsInfo.add(eleSchoolInfo);
			}
			
			List< List<Cluster> > splitUpClusters = new ArrayList();
			//Loop throught ever cluster in largeClusters
			for(Cluster cluster : largeClusters) {
				double stortestDistance = 2147483647; //Max integer value.
				SchoolInformation closestSchool = new SchoolInformation();
				//Loop through every elementary school - determine which school closest to cluster.
				double distancetoCluster = 0;
				for(SchoolInformation school : elementarySchools) {
					distancetoCluster = ( Math.sqrt( Math.pow(( school.getSchoolLon() - cluster.getCentroid().getLon() ), 2) + 
							   			  			 Math.pow(( school.getSchoolLat() - cluster.getCentroid().getLat() ), 2)) );	
					if(distancetoCluster < stortestDistance) {
						stortestDistance = distancetoCluster;
						closestSchool = school;
					}
				}
				//Find the school that is closest to the cluster in everySchoolsInfo
				for(SchoolInfoAndCluster schInfoAndCluster : everySchoolsInfo) {
					//Update elementary schools cluster 
					if(schInfoAndCluster.getSchoolInfo() == closestSchool) {
						List<Cluster> clust = schInfoAndCluster.getCluster();
						clust.add(cluster);
						schInfoAndCluster.setCluster(clust);
					}
				}
			}
			
			for(SchoolInfoAndCluster schInfoAndCluster : everySchoolsInfo) {
				if(schInfoAndCluster.getSchoolInfo().getSchoolType() == "Elementary School") {
					List<Cluster> elementaryClusteredAddrs = new ArrayList();
					int numStudentsInSchool = Integer.parseInt(schInfoAndCluster.getSchoolInfo().getNumStudents());
					//System.out.println("School name- "+ schInfoAndCluster.getSchoolInfo().getSchoolName() + "NumStu- "+ numStudentsInSchool);
					elementaryClusteredAddrs = selectAddr.breakUpAddrsByCluster(schInfoAndCluster.getCluster(), numStudentsInSchool);
					
					//Stores randomly selected/clustered address
					schInfoAndCluster.setCluster(elementaryClusteredAddrs);
				}
				
			
			}
		}
		
		return everySchoolsInfo;
	}
}


