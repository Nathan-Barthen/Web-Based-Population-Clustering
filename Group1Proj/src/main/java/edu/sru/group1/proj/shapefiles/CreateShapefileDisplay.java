package edu.sru.group1.proj.shapefiles;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.domain.AllClusteringInfo;
import edu.sru.group1.proj.domain.SchoolInformation;

public class CreateShapefileDisplay {

	
	
	
	@SuppressWarnings("unchecked")
	public static String createClusterShapefile(MultiPolygon polygon, AllClusteringInfo allInfo){
		 //Creates the geojson for the html display (displays polygon, addresses, clusters, school location w/ label) when a school(s) is clustered. 
		GeometryJSON gjson  = new GeometryJSON ();
	      //Inserting key-value pairs into the json object
		
		//Basic setup for geoJson file.
		String feautureCollection = "{\r\n"
				+ "   \"type\": \"FeatureCollection\",\r\n"
				+ "   \"features\": [\r\n"
				+ "      {\r\n"
				+ "         \"type\": \"Feature\",\r\n"
				+ "         \"properties\": {},\r\n"
				+ "         \"geometry\": {\r\n"
				+ "            \"type\": \"Polygon\",\r\n"
				+ "            \"coordinates\":";
		
		
		//Add the districtPolygon coords (outline) to geoJson
		int i=0;
		String coords = "";
	      for(Coordinate cord : polygon.getCoordinates() ) {
	    	  if(i==0) {
	    		  coords = coords + "[ [ \n    ["+ cord.getX() + ", " + cord.getY() + "]";
	    		  i++;
	    	  }
	    	  else {
	    		  coords = coords + ",\n    ["+ cord.getX() + ", " + cord.getY() + "] ";
	    	  }
	      }
	      coords = coords + " ]";
	      
	      
	      
	      feautureCollection = feautureCollection + coords + " ]\r\n"
	      		+ "      }\r\n"
	      		+ "    },\r\n";
	      
	    //Add the addresses to the geoJson.
	      int iteration = 0;
	      int size = allInfo.getClusterInfo().size();
	      int colorIter=0;
	      //circle image color site: https://www.pngmagic.com/search/circle?page=1
	      String[] colors = { "red", "blue", "silver", "green", "orange", "purple", "black", "maroon", "olive", "cyan", "brown", "darkblue", "pink", "magenta", "lime",
				    		  "red", "blue", "silver", "green", "orange", "purple", "black", "maroon", "olive", "cyan", "brown", "darkblue", "pink", "magenta", "lime",
				    		  "red", "blue", "silver", "green", "orange", "purple", "black", "maroon", "olive", "cyan", "brown", "darkblue", "pink", "magenta", "lime"};
				      
	      for(Cluster clust : allInfo.getClusterInfo()) {
	    	  int pointIter = 0;
	    	  
	    	  for( DataPoint point : clust.getPoints() ) {
		    	  String cluster = "{";
		    	  cluster = cluster + "\n     \"type\": \"Feature\",\r\n"
		    			+ "      \"properties\": { \n" 
			    	  	+ "           \"color\": \"" + colors[colorIter] + "\" \n"
			    	  	+"},\r\n"
		    	  		+ "      \"geometry\": {\r\n"
		    	  		+ "        \"type\": \"Point\",\r\n"
		    	  		+ "        \"coordinates\": [\r\n";
		    	  cluster = cluster	+ "          "+ point.getLon() + ",\r\n"
		    	  		+ "          "+ point.getLat() +"\r\n";
		    	  		
		    	  		cluster = cluster + "        ]\r\n"
		    	  		+ "      }\n";
		    	  
		    	  if(iteration == size-1 && pointIter == clust.getPoints().size()-1) {
		    		  cluster = cluster + "},";
		    	  }
		    	  else {
		    		  cluster = cluster + "},\n";
		    	  }
		    	  
		    	  feautureCollection = feautureCollection + cluster;
		    	  pointIter++;
	    	  }
	    	  iteration++;
	    	  colorIter++;
	      }
	      
	      
	      //Add the school(s) points to the geoJson.
	      int schSize = allInfo.getSchoolsInfo().size();
	      int iter = 0;
	      for(SchoolInformation school : allInfo.getSchoolsInfo()) {
	    	  String schoolData = "\n {";
	    	  schoolData = schoolData + "\n     \"type\": \"Feature\",\r\n"
	    	  		+ "      \"properties\": { \n" 
	    			+ "           \"school\": \"" + school.getSchoolName() + "\", \n"
	    	  		+"\r\n"
	    	  		+ "           \"name\": \"" + "school" + "\" \n"
	    	  		+"},\r\n"
	    	  		+ "      \"geometry\": {\r\n"
	    	  		+ "        \"type\": \"Point\",\r\n"
	    	  		+ "        \"coordinates\": [\r\n";
	    	  schoolData = schoolData	+ "          "+ school.getSchoolLon() + ",\r\n"
	    	  		+ "          "+ school.getSchoolLat() +"\r\n";
	    	  		
	    	  schoolData = schoolData + "        ]\r\n"
	    	  		+ "      }\n";
	    	  if(iter == schSize-1) {
	    		  schoolData = schoolData + "}";
	    	  }
	    	  else {
	    		  schoolData = schoolData + "},";
	    	  }
	    	  
	    	  feautureCollection = feautureCollection + schoolData;
	    	  iter++;
	      }
	     
	      
	      //Close geoJson
	      feautureCollection = feautureCollection 
	    		    + "  ]\r\n"
		      		+ "}";
	      
	      //Write string to geoJson file.
	      try {
	         FileWriter file = new FileWriter("shapefile-GEOJSON.geojson");
	         file.write(feautureCollection);
	         file.close();
	      } 
	      catch (IOException e) {
	         e.printStackTrace();
	      }
	      
	      
		
		return feautureCollection;
		
	}

	
}


