package edu.sru.group1.proj.dbscan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import edu.sru.group1.proj.shapefiles.Record;

/**
 * Class containing the method to get addresses for the students at a school.
 * @author Connor
 *
 */
public class SelectAddressesByCluster {
	
/**
 * Returns a list of clusters containing unique addresses proportional to the number of students in the school. 
 * Requires two parameters: the clustered list of all the addresses and the number of students at the school.
 * @param clusters passes the clusters.
 * @param numStudents passes the number of students.
 * @return returns the student addresses in the cluster.
 */
	public List<Cluster> breakUpAddrsByCluster(List<Cluster> largeClusters, int numStudents) {
		//Adding 1 top total students, fixes address selection to select the right amount of addrs.
		List<Cluster> clusters = largeClusters;
		//numStudents = numStudents+1;
		List<Cluster> studentAddrClusters = new ArrayList();
		
		double totalAddresses = 0;
		
		
		//Gets total number of addresses in ALL of the clusters
		for(Cluster cluster : clusters) {
			totalAddresses = totalAddresses + cluster.getPoints().size();
		}
		int clusterNum = 0;
		//Loops through every cluster, selected random addresses proportional to cluster size and number of students.
		for(Cluster cluster : clusters) {
			Cluster clust = new Cluster(clusterNum);
			List<DataPoint> points = cluster.getPoints();
			List<DataPoint> studentAddrs = new ArrayList();
			
			double sizeofCluster = points.size();
			double numAddrsToSelect = sizeofCluster/totalAddresses;
			numAddrsToSelect = Math.round(numAddrsToSelect * numStudents);
			//Added this if: 3/24/22
			if(numAddrsToSelect > cluster.getPoints().size()) {
				numAddrsToSelect = cluster.getPoints().size();
			}
			int j = 0;
			//Selects random addresses from given cluster until it has proportionally selected enough addresses.
			while(j<numAddrsToSelect) {
				if(points.size()>0) {
					Random rand = new Random();
					int randomNumber;
					//Random number used to randomly select an index to store
					randomNumber = (int) (Math.abs(rand.nextInt()) % sizeofCluster);
					DataPoint addr = points.get(randomNumber);
					//After record has been randomly selected, remove record.
					points.remove(randomNumber);
					clusters.get(clusterNum).setPoints(points);
					studentAddrs.add(addr);
					sizeofCluster--;
					j++;
				}
			}
			clust.setPoints(studentAddrs);
			studentAddrClusters.add(clust);
				
			clusterNum++;
		}
		
		int newTotalAddrs = 0;
		for(Cluster cluster : studentAddrClusters) {
			newTotalAddrs = newTotalAddrs + cluster.getPoints().size();
		}
		/*
		 * If for some reason the total number of addresses in the cluster does not match the number of Students...
		 * Remove elements randomly until it matches the number of students
		 */
		if(newTotalAddrs > numStudents) {
			while(newTotalAddrs != numStudents) {
				Random rand = new Random();
				int randomCluster;
				int randomAddr;
				
				randomCluster = (int) (Math.abs(rand.nextInt()) % studentAddrClusters.size());
				randomAddr = (int) (Math.abs(rand.nextInt()) % ( studentAddrClusters.get(randomCluster).getPoints().size() ));
				
				List<DataPoint> points = studentAddrClusters.get(randomCluster).getPoints();
				points.remove(randomAddr);
				
				studentAddrClusters.get(randomCluster).setPoints(points);
				newTotalAddrs--;
			}
		}
		
		/*
		 * If for some reason the total number of addresses in the cluster does not match the number of Students...
		 * Add elements randomly until it matches the number of students
		 */
		if(newTotalAddrs < numStudents) {
			while(newTotalAddrs < numStudents) {
				Random rand = new Random();
				int randomCluster;
				int randomAddr;
				Cluster clust = new Cluster(clusterNum);
				randomCluster = (int) (Math.abs(rand.nextInt()) % clusters.size());
				
				randomAddr = (int) (Math.abs(rand.nextInt()) % ( clusters.get(randomCluster).getPoints().size() ));
				
				clust = studentAddrClusters.get(randomCluster);
				studentAddrClusters.remove(randomCluster);
				
				clust.addPoint(clusters.get(randomCluster).getPoints().get(randomAddr));
				
				List<DataPoint> oldPoints = clusters.get(randomCluster).getPoints();
				oldPoints.remove(randomAddr);
				clusters.get(randomCluster).setPoints(oldPoints);
				
				studentAddrClusters.add(randomCluster, clust);
				newTotalAddrs++;
			}
		}
		
	
		return studentAddrClusters;
	}
	
	
}
