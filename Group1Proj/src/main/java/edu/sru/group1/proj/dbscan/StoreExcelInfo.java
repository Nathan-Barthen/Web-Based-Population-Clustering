package edu.sru.group1.proj.dbscan;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.locationtech.jts.geom.Coordinate;

import com.opencsv.CSVWriter;

/**
 * 
 * Main Class that saves the info in the an Excel File.
 *
 */
public class StoreExcelInfo {
/**
 * Class that creates a list of the info that is being stored.
 * @param coords passes coordinates
 * @param clusterInfo passes the information from the clustering.
 * @return returns the list of given information.
 */
	public static List<String[]> saveAddrToExcel(Coordinate[] coords, List<Cluster> clusterInfo)
	{
		String[] header = {"polygonLon", "polygonLat", "addressLon", "addressLat", "clusterNum"};

		List<String[]> list = new ArrayList<>();
        list.add(header);
        
		int totalAddrs = 0;	
        int size = 0;
        List<DataPoint> datapoints1 = new ArrayList();
        for(int i=0; i<clusterInfo.size();i++) {
			totalAddrs = totalAddrs + clusterInfo.get(i).getPoints().size();
			datapoints1.addAll(clusterInfo.get(i).getPoints());
		}
		
        if(coords.length < totalAddrs)
        {
        	size = totalAddrs;
        }
        else
        {
        	size = coords.length;
        }
   
        for(int i = 0; i < size; i++)
        {
        	String x = "";
        	String y = "";
        	String lon = "";
        	String lat = "";
        	String clusterNum = "";
        	
        	final DecimalFormat round= new DecimalFormat("00.0000000");
        	
        	if(coords.length > i)
        	{
        		double longVal = (coords[i].getX());
        		double latVal = (coords[i].getY());
        		longVal = Double.parseDouble(round.format(longVal));
        		latVal = Double.parseDouble(round.format(latVal));
        		
        		x = Double.toString(longVal);
        		y = Double.toString(latVal);
        		
        	}
        	if(totalAddrs > i)
        	{
        		lon = Double.toString(datapoints1.get(i).getLon());
        		lat = Double.toString(datapoints1.get(i).getLat());
        		clusterNum = Integer.toString(datapoints1.get(i).getCluster());
        	}
        	String[] record = {x, y, lon, lat, clusterNum};
        	list.add(record);
        }
        return list;
	}
	
	

}
