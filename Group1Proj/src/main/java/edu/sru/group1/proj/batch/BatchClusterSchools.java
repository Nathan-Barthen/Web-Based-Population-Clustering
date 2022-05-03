package edu.sru.group1.proj.batch;

import java.util.*;

import org.locationtech.jts.geom.MultiPolygon;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.ClustersForSchool;
import edu.sru.group1.proj.dbscan.DBScan;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.SchoolInfoAndCluster;
import edu.sru.group1.proj.domain.AddrsPerCluster;
import edu.sru.group1.proj.domain.AllClusteringInfo;
import edu.sru.group1.proj.domain.SchoolInformation;
import edu.sru.group1.proj.shapefiles.Record;

/**
 * Main class for the Batch Clustering of Schools
 * @author Connor
 *
 */
public class BatchClusterSchools {

	/**
	 * Class for executing the batch clusters
	 * @param schoolsData data for schools
	 * @param districtWithLonLat Longitude and Latitude for districts
	 * @param polygon polygon information
	 * @param selectedAddrs address information
	 * @param state state name
	 * @param distNum number of district
	 * @param fileNum number of file
	 * @param schType type of school, elementary, middle, high
	 */
	public static void batchClusterSchs(Vector<SchoolInformation> schoolsData, Vector<SchoolInformation> districtWithLonLat, MultiPolygon polygon, List<Record> selectedAddrs, String state, int distNum, int fileNum, String schType) {
		//Do large clustering (largeClusters for given district)
		//Cluster must be at least 2% of all addresses in the district.
			List<DataPoint> datapoints = new ArrayList();
			for(Record record : selectedAddrs) {
				DataPoint p = new DataPoint(record.getLon(), record.getLat());
				datapoints.add(p);
			}
			int minClusterSize = (int) (datapoints.size() * .02);
			//Diameter = default ~2 miles
			double distArea = polygon.getArea();
			double clusterDiameter = ( (.03*distArea)/0.038982009358999856 );
			DBScan test = new DBScan(clusterDiameter, minClusterSize);
			test.setPoints(datapoints);
			test.cluster();
			List<Cluster> templargeClusters = test.getClusters();
			
			//If num clusters is less than 4. Regenerate clusters with smaller diameter.
			while(templargeClusters.size() < 2) {
				DBScan test2 = new DBScan(clusterDiameter, minClusterSize);
				test2.setPoints(datapoints);
				test2.cluster();
				templargeClusters = test2.getClusters();
				clusterDiameter = clusterDiameter-.001;
				if(clusterDiameter < 0) {
					break;
				}
			}
			for(Cluster cluster : templargeClusters) {
				cluster.calculateCentroid();
			}
			
		//Generate the clusters for the given school in the district
		
			int totalAddrs =0;
			//Skip school if number of students is greater than total addrs (insufficient addresses)
			for(Cluster cluster1 : templargeClusters) {
				totalAddrs += cluster1.getPoints().size();
			}
			
			double numStuToCluster=0;
			String schools = "";
			String distN = "";
			String countyN = "";
			for(SchoolInformation school : schoolsData) {
				schools += school.getSchoolName() + " - ";
				distN = school.getDistName();
				countyN = school.getCountyName();
				numStuToCluster += Double.parseDouble(school.getNumStudents());
			}
			if( (int) numStuToCluster > totalAddrs){
				System.out.println("Skipped School(s): '"+schools+"' in district- '"+ distN +"' (County:"+countyN+").   Reason: Insufficient Addresses");
				return;
			}
			
			List< SchoolInfoAndCluster > clusteredSchoolInfo = new ArrayList<SchoolInfoAndCluster>();
		
			clusteredSchoolInfo = ClustersForSchool.generateSchoolClusters(templargeClusters, districtWithLonLat, schoolsData);
		
			
			//Get total num of students at school (all addrs in clusters)
			int totalStuAddrs = 0;
			int i=0;
			Vector<AddrsPerCluster> addrsPerCluster = new Vector<>();
			for(Cluster cluster2 : clusteredSchoolInfo.get(0).getCluster()) {
				totalStuAddrs += cluster2.getPoints().size();
				AddrsPerCluster clust = new AddrsPerCluster(i, cluster2.getPoints().size(), cluster2.getSchoolName());
				addrsPerCluster.add(clust);
				i++;
			}
			

			AllClusteringInfo allInfo = new AllClusteringInfo(totalStuAddrs, schoolsData, clusteredSchoolInfo.get(0).getCluster(), addrsPerCluster);
			
			BatchRoutingExcelFiles.generateRoutingFile(allInfo.getSchoolsInfo(), allInfo.getClusterInfo(), distN, state, distNum, fileNum, schType);
		
		
	}
	
}
