package edu.sru.group1.proj.junit;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.ClustersForSchool;
import edu.sru.group1.proj.dbscan.DBScan;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.SchoolInfoAndCluster;
import edu.sru.group1.proj.dbscan.SchoolsLongLat;
import edu.sru.group1.proj.domain.CountyDataset;
import edu.sru.group1.proj.domain.SchoolInfoDataset;
import edu.sru.group1.proj.domain.SchoolInformation;
import edu.sru.group1.proj.domain.StateCounty;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;
import edu.sru.group1.proj.shapefiles.Record;
/**
 * Main class used for DBScan Testing.
 * @author Connor
 *
 */
public class DBScanAndClustersTest {
	
	static Vector<StateCounty> countyInfoList = new Vector<StateCounty>();
	static String state = "Pennsylvania";
	static Vector <Vector< Vector<SchoolInformation> >> allCountyInfo = new Vector <Vector< Vector<SchoolInformation> >>();
	static List<DataPoint> datapoints = new ArrayList();
	static Vector<SchoolInformation> schools = new Vector<SchoolInformation>();
	/**
	 * Sets up and gets the data needed to test/create the clusters.
	 * @throws Exception throws exception.
	 */
	@BeforeClass
	public static void setUpOnce() throws Exception {
		Vector<String> counties = new Vector<String>();
		
		counties = CountyDataset.countyFile(state);
		
		//Takes vector of counties, sets state, countyName, and districts in each county.
		for(String countyName : counties) {
			StateCounty county = new StateCounty();
			
			county.setState(state);
			county.setCounty(countyName);
			county.setDistrict( CountyDataset.getDistricts(state, countyName));
			countyInfoList.add(county);
		}
		
		
		for(StateCounty county : countyInfoList) {
			Vector  <Vector<SchoolInformation> > countyInfo = new Vector <Vector<SchoolInformation> >();
			//Loop through every district in county. Generate schoolInfo. Add it to countyInfo
			for(String district : county.getDistrict()) {
				Vector<SchoolInformation> districtInfo = SchoolInfoDataset.schoolsDataNew(state, district, county.getCounty());
				int i=0;
				//Set school's ids.
				for(SchoolInformation school : districtInfo) {
					school.setId(i);
					i++;
				}
				countyInfo.add(districtInfo);
			}
			allCountyInfo.add(countyInfo);
		}
		//If county + district == slippery rock, save polygon and selected addresses.
		for(Vector<Vector<SchoolInformation>>  countyn : allCountyInfo) {
			List<Record> selectedAddrs = null;
				for(Vector<SchoolInformation> district : countyn) {
					for(SchoolInformation sch : district) {
						if(sch.getCountyName().contains("Butler County") ) {
							schools = district;
							
							if(sch.getDistName().toLowerCase().contains("slippery rock area")) {
								GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
								MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
								//Get district polygon and select addrs inside of polygon
								polygon = SelectPolygonAndAddrs.DistrictIndex(district.get(0).getDistName(), state);
								Coordinate[] districtPolygon = polygon.getCoordinates();
								//Selected all of the addresses inside of the given polygon.
								selectedAddrs = SelectPolygonAndAddrs.SelectAddresses(districtPolygon, district.get(0).getCountyName(), state.toLowerCase());
								for(Record record : selectedAddrs) {
									DataPoint p = new DataPoint(record.getLon(), record.getLat());
									datapoints.add(p);
								}
								break;
							}
						}
					}
				}
			}
		
	}
	
	
/**
 * Creates the clusters for each school in district and tests to ensure the number of 
 * clusters isn't 0 and each cluster's size isn't 0
 * @throws IOException throws IOException.
 */
	@Test
	public void testDBScan() throws IOException {
		
		int minClusterSize = (int) (datapoints.size() * .02);
		DBScan test = new DBScan(.029, minClusterSize);
		test.setPoints(datapoints);
		test.cluster();
		List<Cluster> largeClusters = test.getClusters();
		
		int totalAddrs =0;
		for(Cluster cluster : largeClusters) {
			cluster.calculateCentroid();
			//Test to see if Centroid was calculated
			Assert.assertNotEquals(null, cluster.getCentroid());
			totalAddrs += cluster.getPoints().size();
		}
		//Checks and make sure clusters were generated
		Assert.assertNotEquals(0, largeClusters.size());
		
		
		//Generate clusters for schools
		SchoolsLongLat generateSchoolCoords = new SchoolsLongLat();
		schools = (generateSchoolCoords.getSchoolsLongLat(schools, state.toLowerCase())); 
		
		for(SchoolInformation school: schools) {
			//Get the schools selected for clustering
	        Vector<SchoolInformation> schoolsData = new Vector<SchoolInformation>();
	        schoolsData.add(school);
			List< SchoolInfoAndCluster > fullSchoolsInfo = new ArrayList();
			fullSchoolsInfo = ClustersForSchool.generateSchoolClusters(largeClusters, schools, schoolsData);
			
			//Test to ensure clusters were generated for school
			Assert.assertNotEquals(0, fullSchoolsInfo.get(0).getCluster().size());
			//Test to make sure the clusters contain data
			for(Cluster clust : fullSchoolsInfo.get(0).getCluster()) {
				Assert.assertNotEquals(0, clust.getPoints().size());
			}
		}
		
	}
		
				
	
}
