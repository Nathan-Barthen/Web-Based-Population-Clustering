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

import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.domain.CountyDataset;
import edu.sru.group1.proj.domain.StateCounty;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;
import edu.sru.group1.proj.shapefiles.Record;


//J-Unit Test

/*Testing if DisplayShapefile.java is retrieving polygon and address data
 * Contains 2 successful runs
 */
public class CountyTest {
	
	//Test StateCounty POJO
	@Test
	public void testStateCounty() throws IOException {
		StateCounty object = new StateCounty();
		object.setState("Pennsylvania");
		Assert.assertEquals(object.getState() ,"Pennsylvania");
		
		object.setCounty("Butler");
		Assert.assertEquals(object.getCounty() ,"Butler");
		
		Vector<String> districts = new Vector<String>();
		districts.add("Slippery Rock Area School District");
		object.setDistrict(districts);
		Assert.assertEquals(object.getDistrict() ,districts);
	}
	
	
	//Test countySelectrion / storage
	@Test
	public void testCountySelection() throws IOException {
		String state = "Pennsylvania";
		Vector<String> counties = new Vector<String>();
		
		counties = CountyDataset.countyFile(state);
		//Check to make sure counties is not empty
		Assert.assertNotEquals(0, counties.size());
		
		Vector<StateCounty> countyInfoList = new Vector<StateCounty>();
		//Takes vector of counties, sets state, countyName, and districts in each county.
		for(String countyName : counties) {
			StateCounty county = new StateCounty();
			
			county.setState(state);
			county.setCounty(countyName);
			county.setDistrict( CountyDataset.getDistricts(state, countyName));
			countyInfoList.add(county);
		}
		
		//Make sure countyInfoList is not empty
		Assert.assertNotEquals(0, countyInfoList.size());
		
	}
}
