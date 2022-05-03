package edu.sru.group1.proj.junit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.ClustersForSchool;
import edu.sru.group1.proj.dbscan.DBScan;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.SchoolInfoAndCluster;
import edu.sru.group1.proj.dbscan.SchoolsLongLat;
import edu.sru.group1.proj.domain.SchoolInformation;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;
import edu.sru.group1.proj.shapefiles.Record;
/**
 * Main class used to testing the school clustering.
 * @author Connor
 *
 */
public class SchoolClusteringTest {
	static SelectPolygonAndAddrs shapefile = new SelectPolygonAndAddrs();
	static Coordinate[] coords = null;
	static List<Record> selected = new ArrayList();
	static List<DataPoint> datapoints = new ArrayList();
	static List<Cluster> largeClusters = new ArrayList();
	static int minClusterSize = 300;
	static DBScan test = new DBScan(.03, minClusterSize);
	
/**
 * '@BeforeClass' Gathers data needed to run individual school clustering
 * @throws Exception throws Exception.
 */
	@BeforeClass
	public static void setUpOnce() throws Exception {
		SelectPolygonAndAddrs shapefile = new SelectPolygonAndAddrs();
		//Gets the polygon of the given district stores in an array of coordinates.
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
		polygon = SelectPolygonAndAddrs.DistrictIndex("slippery rock area school district", "pennsylvania");
		Coordinate[] coords = polygon.getCoordinates();
		
		//Selected all of the addresses inside of the given polygon.
		List<Record> selected = SelectPolygonAndAddrs.SelectAddresses(coords, "butler", "pennsylvania");
		
		//Convert addresses from type Record to DataPoint (DataPoint is used for clustering)
		for(Record record : selected) {
			DataPoint p = new DataPoint(record.getLon(), record.getLat());
			datapoints.add(p);
		}
		
		test.setPoints(datapoints);
		test.cluster();
		largeClusters = test.getClusters();
		
		for(Cluster cluster : largeClusters) {
			cluster.calculateCentroid();
		}
	}
	
/**
 * Creates the cluster lists for every school in slippery rock area SD.
 *TESTS each cluster list for every school to make sure the total size of the cluster list == # of students at the school
 *If schools are merged, it will use the sum of students from the merged school.
 * @throws IOException throws IOException.
 */
	@Test
	public void testSchoolClustering() throws IOException {
		//will be set by Controller / html
		boolean mergeMiddleAndHigh = true;
		//will be set by Controller / html
		boolean mergeElementarySchools = false;
		
		//SAMPLE DATA - used for testing
		Vector<SchoolInformation> districtSchools = new Vector<SchoolInformation>();
		SchoolInformation moraine = new SchoolInformation("KG", "05", "333","Moraine El Sch", "350 Main St", "16052", "Prospect", "Elementary School");
		districtSchools.add(moraine);
		SchoolInformation srEle = new SchoolInformation("KG", "05", "438","Slippery Rock Area El Sch", "470 N Main St", "16057", "Slippery Rock", "Elementary School");
		districtSchools.add(srEle);
		SchoolInformation srHigh = new SchoolInformation("09", "12", "611","Slippery Rock Area HS", "201 Kiester Rd", "16057", "Slippery Rock", "High School");
		districtSchools.add(srHigh);
		SchoolInformation srMiddle = new SchoolInformation("06", "08", "463","Slippery Rock Area MS", "201 Kiester Rd", "16057", "Slippery Rock", "Middle School");
		districtSchools.add(srMiddle);
	
		
		SchoolsLongLat generateSchoolCoords = new SchoolsLongLat();
		districtSchools = generateSchoolCoords.getSchoolsLongLat(districtSchools, "pennsylvania"); 
		
		for(SchoolInformation school : districtSchools) {
//Test - Check to see if getSchoolsLongLat is getting the lon and lat location for the given schools.
			Assert.assertTrue("School's longitude value is empty", school.getSchoolLon() != 0 );
			Assert.assertTrue("School's latitude value is empty", school.getSchoolLat() != 0 );
		}
		
		List< SchoolInfoAndCluster > everySchoolsInfo = new ArrayList();
		ClustersForSchool clustersForSchoolFile = new ClustersForSchool();
		
		everySchoolsInfo = clustersForSchoolFile.generateSchoolsClusters(largeClusters, districtSchools, mergeMiddleAndHigh, mergeElementarySchools);
		
		int mergedMiddleHighAddrs = 0;
		int mergedElementaryAddrs = 0;
		int timesExe = 0;
		//Loops through every school and get size of clusters in case schools are merged.
		for(SchoolInfoAndCluster school : everySchoolsInfo) {
			for(Cluster clust : school.getCluster()) {
				if(school.getSchoolInfo().getSchoolType() == "High School") {
					mergedMiddleHighAddrs = mergedMiddleHighAddrs + clust.getPoints().size();
				}
				if(school.getSchoolInfo().getSchoolType() == "Elementary School" && timesExe == 0) { 
					mergedElementaryAddrs = mergedElementaryAddrs + clust.getPoints().size();
				}
			}
			if(school.getSchoolInfo().getSchoolType() == "Elementary School") {
				timesExe = 1;
			}
		}
		
		for(SchoolInfoAndCluster school : everySchoolsInfo) {
			int totalAddrs = 0;
			for(Cluster clust : school.getCluster()) {
				totalAddrs = totalAddrs + clust.getPoints().size();
			}
			
//Test - to see if number of points in the clusters is equal to the number of students. 
			//If middle and high school bussing is NOT merged
			if(!mergeMiddleAndHigh && (school.getSchoolInfo().getSchoolType() == "High School" || school.getSchoolInfo().getSchoolType() == "Middle School")) {
				Assert.assertTrue("Total addrs in clusters does not match # of Students at the school", Integer.parseInt(school.getSchoolInfo().getNumStudents()) == totalAddrs );
			}
			//If elementary school bussing is not merged
			if(!mergeElementarySchools && school.getSchoolInfo().getSchoolType() == "Elementary School") {
				Assert.assertTrue("Total addrs in clusters does not match # of Students at the school", Integer.parseInt(school.getSchoolInfo().getNumStudents()) == totalAddrs );
			}
			//If middle and high schools' bussing is merged 
			if(mergeMiddleAndHigh && (school.getSchoolInfo().getSchoolType() == "High School" || school.getSchoolInfo().getSchoolType() == "Middle School")) {
				Assert.assertTrue("Total addrs in clusters does not match # of Students at the school(s)", mergedMiddleHighAddrs == totalAddrs );
			}
			//If the elementary schools' bussing is merged 
			if(mergeElementarySchools && school.getSchoolInfo().getSchoolType() == "Elementary School") {
				Assert.assertTrue("Total addrs in clusters does not match # of Students at the school(s)", mergedElementaryAddrs == totalAddrs );
			}
			
		}
		
		
		
	}
}
