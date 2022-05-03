package edu.sru.group1.proj.shapefiles;

import java.awt.geom.Path2D;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.geotools.data.*;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.opencsv.CSVReader;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;


import edu.sru.group1.proj.shapefiles.Record;

class DistrictCoordinates {
	private double lon;
	private double lat;
	
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
}

/**
 * Main class used to display the shapefile.
 * Iterates through finding the specified school district.
 * @author Connor
 *
 */
public class SelectPolygonAndAddrs{
	/**
	 * Method used to get the district coordinates and uses the shape.
	 * @param district passes the given school.
	 * @param state passes the given state.
	 * @return returns the coordinates of the polygon.
	 * @throws IOException throws IOException.
	 */
	public static MultiPolygon DistrictIndex(String district, String state) throws IOException {
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
        Integer index = 0;
        boolean polygonFound = false;
        
	    //Iterates over .dbf searching for index that contain the specified school district
        //Link - https://docs.geotools.org/stable/userguide/library/data/shape.html
        String dbf =  "states/"+ state.toLowerCase()+ "/" + state.toLowerCase()+".dbf";
        FileInputStream fis = new FileInputStream( dbf );
        DbaseFileReader dbfReader =  new DbaseFileReader(fis.getChannel(), false,  Charset.forName("ISO-8859-1"));
        String distictName = null;
        while ( dbfReader.hasNext() ){
           final Object[] fields = dbfReader.readEntry();
           distictName = (String) fields[3];
           

           if(distictName.equalsIgnoreCase(district)) {
        	   polygonFound = true;
        	   //break when current field of school matches school name.  Saves index.
        	   break;
           }
           index++;
        }

        dbfReader.close();
        fis.close();
        
        if(polygonFound == false) {
       	 return polygon;
       }
        //Iterates over districts shapefile for a given state, saves polygon of the specified district (based on index)
        String shp = "states/" + state.toLowerCase()+ "/" + state.toLowerCase()+".shp";
        File shpFile = new File(shp);
        Map<String, Object> map = new HashMap<>();
        map.put("url", shpFile.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source =
                dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE;
        
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                String temp = feature.toString().toLowerCase();
                if(temp.contains(distictName.toLowerCase())) {
	                String shpIndex = (String) feature.getID();
	                String remove = state.toLowerCase()+".";
	                shpIndex = shpIndex.replace(remove, "");
		                //Stores MultiPolygon for the given district.
		                polygon = (MultiPolygon) feature.getDefaultGeometryProperty().getValue();
		                break;
                 
               }
            }
        } 
        catch (Exception e) {
           System.out.println("Error reading shp file");
        }
        
        //Coordinate[] polygonCoords = polygon.getCoordinates();
        return polygon;
	}
	
	
	
	//=========================================================================================
	
	/*
	 * Takes the coordinates a districts polygon and county name...
	 * Generates the polygon of the district then...
	 *  accesses csv file containing addresses per county...
	 *  selects addresses only in the polygon (the given district)
	 */
	/**
	 * Takes the coordinates a districts polygon and county name...
	 * Generates the polygon of the district then...
	 * accesses csv file containing addresses per county...
	 * selects addresses only in the polygon (the given district)
	 * @param polygonCoords passes the polygon coordinates.
	 * @param countyName passes the county name.
	 * @param state passes the state name.
	 * @return returns the address data.
	 * @throws NumberFormatException throws NumberFormatException.
	 * @throws IOException throws IOException.
	 */
	@SuppressWarnings("null")
	public static List<Record> SelectAddresses(Coordinate[] polygonCoords, String countyName, String state) throws NumberFormatException, IOException {
        Path2D.Double poly = new Path2D.Double();
        //Vector<DistrictCoordinates> distAddrs = new Vector<DistrictCoordinates>();
        List<Record> addrData = new ArrayList<Record>();
        
		for(int i=0; i<polygonCoords.length; i++) {
			double longVal = (polygonCoords[i].getX());
    		double latVal = (polygonCoords[i].getY());
    		final DecimalFormat round= new DecimalFormat("00.0000000");
    		longVal = Double.parseDouble(round.format(longVal));
    		latVal = Double.parseDouble(round.format(latVal));    		
			if (i==0) {
				poly.moveTo(longVal, latVal);
				
			}
			else {		
	    		poly.lineTo(longVal, latVal);
			}
			
		}
	    poly.closePath();
	    
	  //Reads the csv file contains addresses in a given district
	    	countyName = countyName.replace(" County", "");
	  		//https://www.journaldev.com/12014/opencsv-csvreader-csvwriter-example
	  		@SuppressWarnings("deprecation")
	  		String addrFileName = "Addr-" + state.toLowerCase();
	  		String file = "states/" + state.toLowerCase()+ "/" + addrFileName + ".zip";
	  		
	  		final ZipFile zipFile = new ZipFile(file);
	  		ZipEntry entry = zipFile.getEntry(addrFileName+".txt");
	  		InputStream input = zipFile.getInputStream(entry);
	  		
	  		BufferedReader objReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
	  		//Read line by line
	  		String currLine;
	  		String[] line = null;
	  		int skip = 0;
	  		while ((currLine = objReader.readLine()) != null) {
	  			//Skips the first line in the file since it is the header.
	  			line = currLine.split("	");
	  			if(skip == 1 && line.length == 2) {
	  	        		//If the current row (of long and lat) is inside of the districts polygon. Save coords.
	  	        		if(poly.contains(Double.parseDouble(line[0]), Double.parseDouble(line[1]))) {
	  	        			addrData.add(new Record(Double.parseDouble(line[0]), Double.parseDouble(line[1])));
	  	        		}
	  				
	      		}
	      		else {
	      			skip = 1;
	      		}
	  		}
	  		objReader.close();
	    return addrData;
	}

	


}
