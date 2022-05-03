package edu.sru.group1.proj.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;

/**
 * Main class that controls the data for the counties.
 * @author Connor
 *
 */
public class CountyDataset {
	
	/**
	 * Reads the excel files then stores and the unique county names into a Vector. Requires a String of state.
	 * @param state passes the state being accessed.
	 * @return returns the county names from the vector.
	 */
	public static Vector<String> countyFile(String state) {
		Vector<String> countyNames = new Vector<String>();
		try {
			
			//Reads the excel file
			File countiesFile = new File("CountrySchoolsCounties.xlsx");
			FileInputStream fis = new FileInputStream(countiesFile);
			
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0); 
			XSSFRow row = null;
			
			int iterator = 1;
			//Iterates over the file - row by row
			while((row = sheet.getRow(iterator)) != null) {
				
				String excelStateVal = (row.getCell(3).toString());
				
				//If the current row at column 4 in the Excel File matches the state variable ... store information.
				if(excelStateVal.contains(state)) {
					//If countyName is not null ... store information.
					if(row.getCell(2) != null)
					{
						String county = row.getCell(2).toString();
						//If countyName contains " County" ... Do Nothing.
						if(county.contains(" County")) {}
						//Else append " County" to the County name for uniformity.
						else {
							county = county + " County";
						}
						if(countyNames.contains(county)) { 
							//Do nothing
						}
						//If the county name is unique, the else will store value in CountyNames
						else {
									countyNames.addElement(county);
							 }	
					}
				
				}
				iterator++;
			}
			
			workbook.close();
			fis.close();
			
		}
		catch (IOException e) {
			System.out.println("Could not find file");
		}
		 Collections.sort(countyNames);
		
		return countyNames;
	}
	
	/**
	 * Taking input of state name and county name and returns a vector containing the districts that belong to the given county.
	 * @param state passes the state String the was selected.
	 * @param countyName passes the selected school district.
	 * @return returns the districts in a county.
	 */
	public static Vector<String> getDistricts(String state, String countyName) {
		Vector<String> districts = new Vector<String>();
		
		try {
			
			//Reads the excel file
			File countiesFile = new File("CountrySchoolsCounties.xlsx");
			FileInputStream fis = new FileInputStream(countiesFile);
			
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0); 
			XSSFRow row = null;
			
			
			//Iterates over the file - row by row
			int iterator = 1;
			while((row = sheet.getRow(iterator)) != null) {
				
				String excelStateVal = (row.getCell(3).toString());
				
				//If the current row at column 4 in the Excel File matches the state variable ... store information.
				if(excelStateVal.contains(state)) {
					if(row.getCell(2) != null)
					{
						//If the countyName string is equal to county name at current row, store district name
						if( countyName.compareTo(row.getCell(2).toString()) == 0) { 
							//Edited - was originally: if(row.getCell(0).toString().contains("Charter School") || row.getCell(0).toString().contains("Career Center")) {
							if(row.getCell(0).toString().contains("Charter School") || row.getCell(0).toString().contains("Career Center")) {
								//Don't save. Too small
							}
							else {
								districts.addElement(row.getCell(0).toString());
							}
						}
						else {
							//Do nothing
						}
					}
				}
				iterator++;
			}
			workbook.close();
			fis.close();
			
		}
		catch (IOException e) {
			System.out.println("Could not find file");
		}
		
		return districts;
	}
	
	/**
	 * Reads the excel file, returns Vector of type string. Contains information about the charter school districts.
	 * @param state requires a string of a state name. 
	 * @param countyName requires string of a county name.
	 * @return returns the vector string of all the charter school districts.
	 */
	public static Vector<String> getCharterDistricts(String state, String countyName) {
		Vector<String> charterDistricts = new Vector<String>();
		
		try {
			
			//Reads the excel file
			File countiesFile = new File("CountrySchoolsCounties.xlsx");
			FileInputStream fis = new FileInputStream(countiesFile);
			
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0); 
			XSSFRow row = null;
			
			
			//Iterates over the file - row by row
			int iterator = 1;
			while((row = sheet.getRow(iterator)) != null) {
			
			String excelStateVal = (row.getCell(3).toString());
			//If the current row at column 4 in the Excel File matches the state variable ... store information.
			if((excelStateVal.toLowerCase()).contains(state.toLowerCase())) {
				if(row.getCell(2) != null)
				{
					//If the countyName string is equal to county name at current row, store district name
					if( countyName.compareTo(row.getCell(2).toString()) == 0) { 
						if(row.getCell(0).toString().contains("Charter School") ) {
							charterDistricts.addElement(row.getCell(0).toString());
						}
						else {
							//Do nothing
						}
					}
					else {
						//Do nothing
					}
				}
			}
			iterator++;
		}
		workbook.close();
		fis.close();
		
	}
	catch (IOException e) {
		System.out.println("Could not find file");
	}
		
		
		return charterDistricts; 
	
	}
/**
 * The main calls the countyFile method and accepts one of the states.	
 * @param args passes args for a selected state.
 */
	public static void main(String[] args) {
		
		//countyFile("Ohio");
		
	}
}




