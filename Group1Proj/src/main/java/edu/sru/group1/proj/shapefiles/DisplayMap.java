package edu.sru.group1.proj.shapefiles;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.geotools.filter.Filters;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.Geometry;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.util.factory.GeoTools;

/**
 * Displays the visual map for the shapefile.
 * @author Connor
 *
 */
public class DisplayMap {
	/**
	 * Displays a data store file chooser dialog for shapefiles, and creates a map content to add the shapefile to it.
	 * @param args passes args for the shapefile.
	 * @throws IOException throws exception for IO.
	 * @throws CQLException throws exception for CQL.
	 */
	public static void main(String[] args) throws IOException, CQLException {
	      
			// display a data store file chooser dialog for shapefiles
	        File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }

	        FileDataStore store = FileDataStoreFinder.getDataStore(file);
	        SimpleFeatureSource featureSource = store.getFeatureSource();

	        // Create a map content and add our shapefile to it
	        MapContent map = new MapContent();
	        map.setTitle("Map");

	        Style style = SLD.createSimpleStyle(featureSource.getSchema());
	        Layer dLayer = new FeatureLayer(featureSource, style);
	        map.addLayer(dLayer);

	        // Now display the map
	        JMapFrame.showMap(map);
		
	        
	        SimpleFeatureSource source = featureSource;

	        String school = "Franklin Area School District";
	        
	       // Filter filter = CQL.toFilter("NAME = '" + school + "'");
	        //System.out.println("the filter of useing the NAME attribute of teh shape file " + filter.toString());
	        
	        ///*
	        Filter filter = CQL.toFilter("NAME = '" + school + "'");
	        SimpleFeatureCollection features = source.getFeatures(filter);

	        /*
	         * Testing for getting the 
	        SimpleFeatureIterator iterator = features.features();
	        	try {
	                while (iterator.hasNext()) {
	                	SimpleFeature feature = iterator.next();
	                    Geometry geom = (Geometry) feature.getDefaultGeometry();
	                   //... do something here
	                }
	            } finally {
	                iterator.close(); // IMPORTANT
	            }
	        	
	        	MapContent map2 = new MapContent();
		        map2.setTitle("Map2");

		        style = SLD.createSimpleStyle(featureSource.getSchema());
		        dLayer = new FeatureLayer(featureSource, style);
		        map2.addLayer(dLayer);
	        	
	        */
	}

}
