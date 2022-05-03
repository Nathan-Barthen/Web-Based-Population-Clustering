package edu.sru.group1.proj.domain;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.boot.SpringApplication;

import com.opencsv.CSVReader;

import edu.sru.group1.proj.dbscan.SchoolsLongLat;
import edu.sru.group1.proj.shapefiles.Record;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;

/**
 * Main class that controls the School Information and details.
 * @author Connor
 *
 */
public class SchoolInfoDataset {
	
	//Reads the excel, requires string of a district name, returns Vector of type SchoolInformation. Contains information about the schools in the given district
	/**
	 * Reads the excel file, returns Vector of type SchoolInformation. Contains information about the schools in the given district
	 * @param state requires a string of a state name. 
	 * @param schoolDistrictName requires string of a district name.
	 * @return returns the vector will all of the school information.
	 */
	
	
	public static Vector<SchoolInformation> schoolsDataNew(String state, String schoolDistrictName, String county) {
		Vector<SchoolInformation> schoolsInfo = new Vector<SchoolInformation>();
		Vector<SchoolInformation> charSchsInDist = new Vector<SchoolInformation>();
		try {
			String statefileName;
			statefileName = "states/" + state.toLowerCase() + "/" + state+".csv";
			
			String modifiedSchoolDistrictName1 = schoolDistrictName.replace(" School District", " SD");
			String modifiedSchoolDistrictName2 = schoolDistrictName.replace(" Charter School", " CS");
			String modifiedSchoolDistrictName3 = schoolDistrictName.replace(" Schools", " SCHS");
			String modifiedSchoolDistrictName4 = schoolDistrictName.replace(" Public Schools", " PBLC SCHS");
	  		
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader(new FileReader(statefileName), ',');
			String[] line = null;
			
			//Find all of the charter school DISTRICTS in the given county
			Vector<String> charterDistricts = CountyDataset.getCharterDistricts(state, county);
			//Select all of the charter SCHOOLS in charterDistricts  
			Vector<SchoolInformation> allCharSchInfo = getCharterSchools(state, charterDistricts, county);
			
			SelectPolygonAndAddrs shapefile = new SelectPolygonAndAddrs();
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
			MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
			//Gets polygon of the district passed.
			polygon = shapefile.DistrictIndex(schoolDistrictName, state);
			Coordinate[] districtPolygonCoords = polygon.getCoordinates();
			Path2D.Double poly = new Path2D.Double();
			if(districtPolygonCoords.length > 0) {
				for(int i=0; i<districtPolygonCoords.length; i++) {
					double longVal = (districtPolygonCoords[i].getX());
		    		double latVal = (districtPolygonCoords[i].getY());
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
			}
		    
		    SchoolsLongLat generateSchoolCoords = new SchoolsLongLat();
		    //Gets the lon/lat of all of the schools in the district.
		    allCharSchInfo = generateSchoolCoords.getSchoolsLongLat(allCharSchInfo, state.toLowerCase());
		    for(SchoolInformation charSchool : allCharSchInfo) {
		    	//If charterSchool is in the district, add charter school.
			    if(poly.contains(charSchool.getSchoolLon(), charSchool.getSchoolLat()) ) {
			    	charSchool.setDistName(schoolDistrictName);
			    	charSchsInDist.add(charSchool);
	      		}
			    
		    }
			
			//add 'regular' schools to district by comparing district to the district at the current row...
			while ((line = reader.readNext()) != null) {
	  			  if(line[8] != null) {
					//If the districtName matches the district at the current row...
					if(schoolDistrictName.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName2.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName1.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName3.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName4.equalsIgnoreCase(line[7].toString())) { 
						String modifiedNumStu = line[18].toString().replace(".00000", "");
						if(modifiedNumStu.contains("–")) { /*skip*/ }
						else {
							//Creates an instance of SchoolInformation, adds information, stores information in schoolsInfo vector.
							SchoolInformation school = new SchoolInformation();
							
							school.setSchoolName(line[6].toString());
							school.setAddress(line[8].toString());
							school.setCity(line[9].toString());
							school.setZip(line[11].toString());
							school.setNumStudents(modifiedNumStu);
							school.setLowGrade(line[4].toString());
							school.setHighGrade(line[5].toString());		
							school.setSchoolType();	
							school.setDistName(schoolDistrictName);
							school.setCountyName(county);
							
							schoolsInfo.addElement(school);
						}
					}
	  			  }
	  			  
			}
	          	
	      		
	  		reader.close();
			
		}
		catch (IOException e) {
			System.out.println("Could not find file" );
		}
		schoolsInfo.addAll(charSchsInDist);
		return schoolsInfo;
	}

	
	/**
	 * Reads the excel file, returns Vector of type SchoolInformation. Contains information about the charter schools in all charter school 'districts' in given county.
	 * @param state requires a string of a state name. 
	 * @param countyName requires vector string of all of the charter school 'districts'.
	 * @param countyName requires string of a county name.
	 * @return returns the vector string of all the charter school districts.
	 */
	public static Vector<SchoolInformation> getCharterSchools(String state, Vector<String> charterDistricts, String county) {
		Vector<SchoolInformation> charterSchoolsInfo = new Vector<SchoolInformation>();
		try {
			String statefileName;
			statefileName = "states/" + state.toLowerCase() + "/" + state+".csv";
	  		
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader(new FileReader(statefileName), ',');
			String[] line = null;
			
			
			while ((line = reader.readNext()) != null) {
	  			  if(line[8] != null) {
	  				for(String charDist : charterDistricts) {
	  					String modifiedSchoolDistrictName1 = charDist.replace(" School District", " SD");
	  					String modifiedSchoolDistrictName2 = charDist.replace(" Charter School", " CS");
					//If the districtName matches the district at the current row...
						if(charDist.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName2.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName1.equalsIgnoreCase(line[7].toString()) ) { 
							String modifiedNumStu = line[18].toString().replace(".00000", "");
							if(modifiedNumStu.contains("–")) { /*skip*/ }
							else {
								//Creates an instance of SchoolInformation, adds information, stores information in schoolsInfo vector.
								SchoolInformation school = new SchoolInformation();
								school.setSchoolName(line[6].toString());
								school.setAddress(line[8].toString());
								school.setCity(line[9].toString());
								school.setZip(line[11].toString());
								school.setNumStudents(modifiedNumStu);
								school.setLowGrade(line[4].toString());
								school.setHighGrade(line[5].toString());		
								school.setSchoolType();	
								school.setDistName(charDist);
								school.setCountyName(county);
								
								charterSchoolsInfo.addElement(school);
							}
						}
		  			  }
	  			  }
	  			  
			}
	          	
	      		
	  		reader.close();
			
		}
		catch (IOException e) {
			System.out.println("Could not find file" );
		}
		
		return charterSchoolsInfo;
	}
	
	
	
	public static Vector<SchoolInformation> batchSchoolsData(String state, String schoolDistrictName, String county) {
		Vector<SchoolInformation> schoolsInfo = new Vector<SchoolInformation>();
		try {
			String statefileName;
			statefileName = "states/" + state.toLowerCase() + "/" + state+".csv";
			
			String modifiedSchoolDistrictName1 = schoolDistrictName.replace(" School District", " SD");
			String modifiedSchoolDistrictName2 = schoolDistrictName.replace(" Charter School", " CS");
			String modifiedSchoolDistrictName3 = schoolDistrictName.replace(" Schools", " SCHS");
			String modifiedSchoolDistrictName4 = schoolDistrictName.replace(" Public Schools", " PBLC SCHS");
	  		
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader(new FileReader(statefileName), ',');
			String[] line = null;
			
			
		    
			
			//add 'regular' schools to district by comparing district to the district at the current row...
			while ((line = reader.readNext()) != null) {
	  			  if(line[8] != null) {
					//If the districtName matches the district at the current row...
					if(schoolDistrictName.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName2.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName1.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName3.equalsIgnoreCase(line[7].toString()) || modifiedSchoolDistrictName4.equalsIgnoreCase(line[7].toString())) { 
						String modifiedNumStu = line[18].toString().replace(".00000", "");
						if(modifiedNumStu.contains("–")) { /*skip*/ }
						else {
							//Creates an instance of SchoolInformation, adds information, stores information in schoolsInfo vector.
							SchoolInformation school = new SchoolInformation();
							
							school.setSchoolName(line[6].toString());
							school.setAddress(line[8].toString());
							school.setCity(line[9].toString());
							school.setZip(line[11].toString());
							school.setNumStudents(modifiedNumStu);
							school.setLowGrade(line[4].toString());
							school.setHighGrade(line[5].toString());		
							school.setSchoolType();	
							school.setDistName(schoolDistrictName);
							school.setCountyName(county);
							
							schoolsInfo.addElement(school);
						}
					}
	  			  }
	  			  
			}
	          	
	      		
	  		reader.close();
			
		}
		catch (IOException e) {
			System.out.println("Could not find file" );
		}
		return schoolsInfo;
	}
	
	
	
	
	/**
	 * Main for testing and showing schools in a given state.
	 * @param args passes the state and schools district.
	 */
	public static void main(String[] args) {
		
		//schoolsData("Virginia", "Richmond City Public Schools");
		//schoolsData("Pennsylvania", "Franklin Area School District");
		
		
	}
}




