package edu.sru.group1.proj.batch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.domain.SchoolInformation;

/**
 * Main class for Batch Routing of the Excel Files.
 * @author Connor
 *
 */
public class BatchRoutingExcelFiles {
	/**
	 * Batch routing for excel files class.
	 * @param schoolInfoList information of the schools
	 * @param clusterInfoList information for the clustered list
	 * @param districtName names of the districts
	 * @param state state name
	 * @param distNum number of the district
	 * @param fileNum number of the file
	 * @param schType type of school
	 */
	public static void generateRoutingFile(List<SchoolInformation> schoolInfoList, List<Cluster> clusterInfoList, String districtName, String state, int distNum, int fileNum, String schType)
	{
		String countyName = schoolInfoList.get(0).getCountyName().replace(" ", "");
		countyName = countyName.replace("County", "");
		String distName = schoolInfoList.get(0).getDistName().replace(" ", "");
		distName = districtName.substring(0,4);
		
		String schoolsAbriv = "";
		for(SchoolInformation school : schoolInfoList) {
			String schoolAbriv = school.getSchoolName().replace(" ", "");
			schoolAbriv = schoolAbriv.substring(0,4);
			schoolsAbriv += "-" + schoolAbriv;
		}
		
		String file1SchoolInfoName = "./BatchFiles/AddrInfo/"+countyName +"-"+distNum+"-"+fileNum+"-"+ distName +"-" + schType +"-" +schoolsAbriv + ".csv";
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
	}
}
