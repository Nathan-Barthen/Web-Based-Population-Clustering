package edu.sru.group1.proj.dbscan;

import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import edu.sru.group1.proj.domain.SchoolInformation;

/**
 * Main class containing method: SchoolsLongLat
 */
public class SchoolsLongLat {
/**
 * Method that passes a vector of type SchoolInformation and state name...
 * Opens URL, finds, and then stores the lon and lat for the given schools.
 * Returns the vector of type SchoolInformation with the added lon and lat values.
 * @param schools passes the given schools.
 * @param state passes the state name.
 * @return returns the updated schools. 
 */
	public Vector<SchoolInformation> getSchoolsLongLat(Vector<SchoolInformation> schools, String state){
		Vector<SchoolInformation> updatedSchools = new Vector<SchoolInformation>();
		
		//Goes to the specified url for every school, and gets the lon lat location of the school.
		for(SchoolInformation school : schools) {	
			String address = school.getAddress();
			String city = school.getCity();
			//Changes spaces in variables to + for the URL.
			address = address.replace(" ", "+");
			city = city.replace(" ", "+");
			state = state.replace(" ", "+");
			
			String siteURL = "https://geocoding.geo.census.gov/geocoder/geographies/address?street=" + address+ "&city=" + city + "&state=" + state + "&zip=" + school.getZip() + "&benchmark=4&vintage=4";       
			
			//https://www.javatpoint.com/java-get-data-from-url
			    try  
			    {  
			      URL url = new URL(siteURL); // creating a url object  
			      URLConnection urlConnection = url.openConnection();
			      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			      String line;

			      // Reads the webpage line by line
			      while ((line = bufferedReader.readLine()) != null)
			      {
			    	  //If it finds the line with the informatrion about lon and lat
			    	  if(line.contains("Interpolated Longitude (X)")) {
			    		  String lon;
			    		  String lat;
			    		  //entireLine = entireLine.substring(entireLine.indexOf("Interpolated Longitude (X) Coordinates: </span>") + 1);
			    		  lon = StringUtils.substringBetween(line, "Interpolated Longitude (X) Coordinates: </span>", "<br/><span");
			    		  lat = StringUtils.substringBetween(line, "Interpolated Latitude (Y) Coordinates: </span>", "<br/><span");
			    		  
			    		  double schoolLon = Double.parseDouble(lon);
			    		  double schoolLat = Double.parseDouble(lat);
			    		  school.setSchoolLon(schoolLon);
			    		  school.setSchoolLat(schoolLat);
			    		  updatedSchools.add(school);
			    		  
			    	  }
			      }
			      bufferedReader.close();
			    }  
			    catch(Exception e)  
			    {  
			      System.out.println("Error");
			      e.printStackTrace();  
			    }  
		
		}
		return updatedSchools;
	}
	
}
