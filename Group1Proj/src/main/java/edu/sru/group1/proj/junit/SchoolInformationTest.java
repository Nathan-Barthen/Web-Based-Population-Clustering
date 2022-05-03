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
import edu.sru.group1.proj.dbscan.DBScan;
import edu.sru.group1.proj.dbscan.DataPoint;
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
public class SchoolInformationTest {
	
	static Vector<StateCounty> countyInfoList = new Vector<StateCounty>();
	static String state = "Pennsylvania";
	
	/**
	 * Sets up and gets the data needed to test/create the clusters.
	 * @throws Exception throws exception.
	 */
	@BeforeClass
	public static void setUpOnce() throws Exception {
		
		Vector<String> counties = new Vector<String>();
		
		counties = CountyDataset.countyFile(state);
		//Check to make sure counties is not empty
		Assert.assertNotEquals(0, counties.size());
		
		
		//Takes vector of counties, sets state, countyName, and districts in each county.
		for(String countyName : counties) {
			StateCounty county = new StateCounty();
			
			county.setState(state);
			county.setCounty(countyName);
			county.setDistrict( CountyDataset.getDistricts(state, countyName));
			countyInfoList.add(county);
		}
	}
	
	
	//Test SchoolInformation POJO
		/**
		 * Class for testing the School Information
		 */
		@Test
		public void SchoolInformationPOJOTest() {
			SchoolInformation school = new SchoolInformation();
			
			//Test default constructor values
			Assert.assertEquals(school.getBusCapacity(), 50);
			Assert.assertEquals(school.getNumBuses(), 20);
			Assert.assertEquals(school.getTravelTime(), 50);
			Assert.assertEquals(school.getStuPercent(), 90);
		}
		/**
		 * Class for collectin the test.
		 */
		@Test
		public void SchoolInformationCollectionTest() {
			
			Vector <Vector< Vector<SchoolInformation> >> allCountyInfo = new Vector <Vector< Vector<SchoolInformation> >>();
			
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
					Assert.assertNotEquals(0, districtInfo);
					countyInfo.add(districtInfo);
				}
				
				//Add countyInfo to allCountyInfo
				allCountyInfo.add(countyInfo);
			}
		}		
	
}
