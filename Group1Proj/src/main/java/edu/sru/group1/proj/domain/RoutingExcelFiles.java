package edu.sru.group1.proj.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.geotools.filter.text.cql2.CQLException;
import org.locationtech.jts.geom.Coordinate;

import com.opencsv.CSVWriter;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.StoreExcelInfo;

public class RoutingExcelFiles {
	public String namesForFile = "";
	//When user hits 'Route' button display-all-clustering. It creates the file needed for Routing schools (for Dr. Thangiah's algorithm).
	public static String generateRoutingFile(List<SchoolInformation> schoolInfoList, List<Cluster> clusterInfoList, String districtName)
	{
		String file1SchoolInfoName = "";

		String namesForFile = "";
		
		for(SchoolInformation school : schoolInfoList)
	       {
	        	namesForFile = namesForFile + school.getSchoolName().substring(0,3) + "-";
	       }

		 file1SchoolInfoName = namesForFile + "RountingInfo.csv";
		 
		
		List<String[]> list = new ArrayList<>();
        
		int totalStudents = 0;
		int busCapacity = 50;
		int travelTime = 50;
		
		//get total students
		for(Cluster clust : clusterInfoList) {
			totalStudents += clust.getPoints().size();
		}
		//get busCapacity and travelTime  (gets the largest)
		for(SchoolInformation school : schoolInfoList) {
			if(school.getBusCapacity() > busCapacity) {
				busCapacity = school.getBusCapacity();
			}
			if(school.getTravelTime() > travelTime) {
				travelTime = school.getTravelTime();
			}
		}
		
		//Add district information
		String[] header = { districtName, String.valueOf(schoolInfoList.size()), String.valueOf(totalStudents), String.valueOf(busCapacity), String.valueOf(travelTime) };
    	list.add(header);
		
    	//Add address information
    	int i=1;
    	for(Cluster cluster : clusterInfoList) {
    		List<DataPoint > temp = cluster.getPoints();
    		for(DataPoint addr : temp) {
    			int multipleStu=0;
    			int addrIndex =0;
    			for(DataPoint multStu : cluster.getPoints()) {
    				if(multStu == addr) {
    					multipleStu += 1;
    				}
    				if(multStu == addr && multipleStu > 1) {
    					temp.remove(addrIndex);
    				}
    				addrIndex++;
    			}
    			
    			
	    		String road = "NeedGoogleMapsAPIKey";
	    		
	    		String[] stuAddr = { String.valueOf(i), road, String.valueOf(addr.getLat()), String.valueOf(addr.getLon()), String.valueOf(multipleStu) };
	    		list.add(stuAddr);
	    		i++;
    		}
    	}
    	
    	//Add school information
    	int schoolIndex = 1;
        for(SchoolInformation school : schoolInfoList)
        {
        	String schoolLat = String.valueOf(school.getSchoolLat());
        	String schoolLon = String.valueOf(school.getSchoolLon());
        	String[] schoolRecord = {String.valueOf(schoolIndex), school.getSchoolName(), schoolLat, schoolLon};
        	list.add(schoolRecord);
        	schoolIndex++;
        }
        

        try (CSVWriter writer = new CSVWriter(new FileWriter(file1SchoolInfoName))) {
            writer.writeAll(list);
        } catch (IOException e) {
			System.out.println("Error generating file.");
			e.printStackTrace();
		}
        
        return file1SchoolInfoName;
        
	}
}
