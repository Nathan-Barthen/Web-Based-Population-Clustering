package edu.sru.group1.proj.junit;

import org.junit.Test;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;

import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;
import edu.sru.group1.proj.shapefiles.Record;


//J-Unit Test

/*Testing if DisplayShapefile.java is retrieving polygon and address data
 * Contains 2 successful runs
 */
public class ShapefileTest {
	
	@Test
	public void testShapefile() throws IOException {
		List<Record> selected = new ArrayList();
		List<String[]> dataList = new ArrayList<String[]>();
		dataList.add(new String[] { "franklin area school district", "pennsylvania", "venango"});
		dataList.add(new String[] { "slippery rock area school district", "pennsylvania", "butler"});
		
		for(String[] data : dataList) {
			List<DataPoint> datapoints = new ArrayList<DataPoint>();
			
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
			MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
			polygon = SelectPolygonAndAddrs.DistrictIndex(data[0], data[1]);
			Coordinate[] coords = polygon.getCoordinates();
			selected = SelectPolygonAndAddrs.SelectAddresses(coords, data[2], data[1]);
			
			Assert.assertNotEquals(0, coords.length);
			Assert.assertNotEquals(0, selected.size());
			
			for(Record record : selected) {
				DataPoint p = new DataPoint(record.getLon(), record.getLat());
				datapoints.add(p);
			}
			Assert.assertNotEquals(0, datapoints.size());
		}
	}
}
