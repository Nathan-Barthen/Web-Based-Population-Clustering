package edu.sru.group1.proj.batch;

import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;

import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.ClustersForSchool;
import edu.sru.group1.proj.dbscan.DBScan;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.SchoolInfoAndCluster;
import edu.sru.group1.proj.dbscan.SchoolsLongLat;
import edu.sru.group1.proj.domain.AddrsPerCluster;
import edu.sru.group1.proj.domain.AllClusteringInfo;
import edu.sru.group1.proj.domain.CountyDataset;
import edu.sru.group1.proj.domain.SchoolInfoDataset;
import edu.sru.group1.proj.domain.SchoolInformation;
import edu.sru.group1.proj.domain.StateCounty;
import edu.sru.group1.proj.shapefiles.Record;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;

/**
 * Main class for batch processing
 * @author Connor
 *
 */
public class BatchProcessing {

	/**
	 * Main for choosing which schools are clustered together
	 * @param args args parameter
	 * @throws Exception throws exception
	 */
	public static void main(String[] args) throws Exception {
		String state = "Pennsylvania";
		//If all are false, it will generate routing file for each school SEPARATELY
			//Cluster elementary together:
			Boolean eleTogether = false;
			//Cluster middle and high schools together:
			Boolean midHighTogether = false;
			//Run all schools in the district together:
			Boolean allTogether = true;
		
		//What school types should the files be generated for (only one should be true, rest are false).
			Boolean runForAllSchs = true;
			Boolean runForOnlyEle = false;
			Boolean runForOnlyMiddle = false;
			Boolean runForOnlyHigh = false;
			Boolean runForMiddleAndHigh = false;
			Boolean runForEleAndMiddle = false;
		//Used to set whether you would want to include Charter schools (will slightly slow down batch processing since it has to get the lon/lat of charter school before adding to district).
			Boolean includeCharterSchools = false;
		//If you only want one county processed. Change oneCounty to true and specify countyN (county name)
			Boolean oneCounty = false;
				String countyN = "Butler County";
				//If you only want to run batch processing on one district ( MUST also set countyN (county name) )
				Boolean oneDistrict = false;
				String districtN = "Slippery Rock Area School District";
		
		
		
		
		Vector<String> counties = new Vector<String>();
		
		counties = CountyDataset.countyFile(state);
		Vector<StateCounty> countyInfoList = new Vector<StateCounty>();
		//Takes vector of counties, sets state, countyName, and districts in each county.
		for(String countyName : counties) {
			StateCounty county = new StateCounty();
			//If user only wants to run Batch on one county and/or one district.
			if(oneCounty == true) {
				county.setState(state);
				county.setCounty(countyN);
				if(oneDistrict == true) {
					Vector<String> dist = new Vector<String>();
					dist.add(districtN);
					county.setDistrict(dist);
				}
				else {  county.setDistrict( CountyDataset.getDistricts(state, countyN)); }
				if(county.getDistrict().size() == 0) {
					System.out.println("County name incorrect / no districts exsit in county");
					System.exit(0);
				}
				countyInfoList.add(county);
				break;
			}
			
			county.setState(state);
			county.setCounty(countyName);
			county.setDistrict( CountyDataset.getDistricts(state, countyName));
			countyInfoList.add(county);
		}
		
		
		//Gets district information for every district in all counties.
		Vector <Vector< Vector<SchoolInformation> >> allCountyInfo = new Vector <Vector< Vector<SchoolInformation> >>();
		
		//Loop through every county in state and save schoolsInfo for every district.
		for(StateCounty county : countyInfoList) {
			Vector  <Vector<SchoolInformation> > countyInfo = new Vector <Vector<SchoolInformation> >();
			
			//Loop through every district in county. Generate schoolInfo. Add it to countyInfo
			for(String district : county.getDistrict()) {
				//gets all of the schools in the given district (including charter schools)
				Vector<SchoolInformation> districtInfo = SchoolInfoDataset.batchSchoolsData(state, district, county.getCounty());
				int i=0;
				
				
				//Set school's ids.
				for(SchoolInformation school : districtInfo) {
					school.setId(i);
					i++;
				}
				//If user only wants to run batchProcessing on one district (and they spell district name incorrectly)
				if(oneDistrict && countyInfoList.size()==1 && districtInfo.size() == 0) {
					System.out.println("District name incorrect / no schools exsit in district");
					System.exit(0);
				}
				countyInfo.add(districtInfo);
			}
			
			//Add countyInfo to allCountyInfo
			allCountyInfo.add(countyInfo);
		}
		
		int countyNum = 1;
		//Loop through every COUNTY.
		for(Vector <Vector<SchoolInformation>>  county : allCountyInfo) {
			//int used for file naming
			int distNum=1;
			SchoolsLongLat generateSchoolCoords = new SchoolsLongLat();
			Vector<SchoolInformation> allCharSchInfo = new Vector<SchoolInformation>();
			//If you select to include charter schools. This will get the lon/lat of all charter schools in the given county (used later to see if school is located in current district).
			if( includeCharterSchools ) {
				//Find all of the CHARTER school DISTRICTS in the given county
				Vector<String> charterDistricts = CountyDataset.getCharterDistricts(state, county.get(0).get(0).getCountyName());
				//Select all of the CHARTER SCHOOLS in charterDistricts  
				allCharSchInfo = SchoolInfoDataset.getCharterSchools(state, charterDistricts, county.get(0).get(0).getCountyName());
				//Get the lon/lat of the CHARTER schools
				allCharSchInfo = generateSchoolCoords.getSchoolsLongLat(allCharSchInfo, state.toLowerCase());
			}
			
			//Loop through every DISTRICT in given COUNTY
			for(Vector<SchoolInformation> district : county) {
				//If district is empty (no schools - skip)
				if(district.size() < 1) {
					continue;
				}
				GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
				MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
			//Get district polygon and select addrs inside of polygon
				polygon = SelectPolygonAndAddrs.DistrictIndex(district.get(0).getDistName(), state);
				if( polygon.getCoordinates().length == 0){
					System.out.println("Skipped district - '"+ district.get(0).getDistName()+ "'- Reason: Polygon does not exist for district in the US Census Bureau database");
					break;
				}
				Coordinate[] districtPolygon = polygon.getCoordinates();
				//Selected all of the addresses inside of the given polygon.
				List<Record> selectedAddrs = SelectPolygonAndAddrs.SelectAddresses(districtPolygon, district.get(0).getCountyName(), state);
				
				
				//Sets the lon/lat locations of the schools in the district
				Vector<SchoolInformation> districtWithLonLat = new Vector<SchoolInformation>();
				districtWithLonLat = (generateSchoolCoords.getSchoolsLongLat(district, state.toLowerCase())); 
				
				//if true, calculate check to see if they are inside of current district's polygon. If so, add school(s) that are inside polygon.
				if( includeCharterSchools ) {
					
					Vector<SchoolInformation> charSchsInDist = new Vector<SchoolInformation>();
					
					//Check to see if the CHARTER school is inside the given district, if so add it to school list.
					SelectPolygonAndAddrs shapefile = new SelectPolygonAndAddrs();
					GeometryFactory geometryFactory1 = JTSFactoryFinder.getGeometryFactory();
					MultiPolygon polygon1 = new MultiPolygon(null, geometryFactory1);
					polygon1 = shapefile.DistrictIndex(districtWithLonLat.get(0).getDistName(), state);
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
				    
				    for(SchoolInformation charSchool : allCharSchInfo) {
				    	//If charterSchool is in the district, add chater school.
					    if(poly.contains(charSchool.getSchoolLon(), charSchool.getSchoolLat()) ) {
					    	charSchool.setDistName(districtWithLonLat.get(0).getDistName());
					    	charSchsInDist.add(charSchool);
			      		}
					    
				    }
					
					districtWithLonLat.addAll(charSchsInDist);
				}
				
				//fileNum used in file naming for county (some schools have similar names and it overrides the previous file. This fixes it)
				int fileNum=1;
				//If they want elementary schools clustered together (high+middle could be separate or together)
				
				Vector<SchoolInformation> eleSchools = new Vector<SchoolInformation>();
				Vector<SchoolInformation> mergeMiddleHigh = new Vector<SchoolInformation>();
				Vector<SchoolInformation> allSchsTogether = new Vector<SchoolInformation>();
				for(SchoolInformation school : districtWithLonLat) {	
					if(school.getSchoolType().contains("Elementary") ) {
						eleSchools.add(school);
					}
					else {
						mergeMiddleHigh.add(school);
					}
					
					allSchsTogether.add(school);
				}
				
				if(!allTogether) {
					//Create routing file for eleSchools (clustered TOGHETHER)
					if(runForAllSchs || runForOnlyEle || runForEleAndMiddle) {
						if(eleTogether) {
							BatchClusterSchools.batchClusterSchs(eleSchools, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "Eles");
							fileNum++;
						}
						//else create routing file for every elementary school (clustered SEPARATELY)
						else {
							for(SchoolInformation school : eleSchools) {	
								Vector<SchoolInformation> school1 = new Vector<SchoolInformation>();
								school1.add(school);
								BatchClusterSchools.batchClusterSchs(school1, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "Ele");
								fileNum++;
							}
						}
					}
					
					if(runForAllSchs || runForOnlyMiddle || runForOnlyHigh || runForMiddleAndHigh || runForEleAndMiddle) {
					//Create routing file for middle+high (clustered TOGHETHER)
						if(midHighTogether && (runForAllSchs || runForMiddleAndHigh) ) {
							BatchClusterSchools.batchClusterSchs(mergeMiddleHigh, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "MSHS");
							fileNum++;
						}
						//else create routing file for middle and high (clustered SEPARATELY)
						else {
							for(SchoolInformation school : mergeMiddleHigh) {	
								Vector<SchoolInformation> school1 = new Vector<SchoolInformation>();
								school1.add(school);
								String schType = "";
								if(school.getSchoolType().contains("High") && (runForAllSchs || runForOnlyHigh) ) {
									schType = "HS";
									BatchClusterSchools.batchClusterSchs(school1, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, schType);
								}
								if(school.getSchoolType().contains("Middle") && (runForAllSchs || runForOnlyMiddle || runForEleAndMiddle) ) {
									schType = "MS";
									BatchClusterSchools.batchClusterSchs(school1, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, schType);
								}
								
								fileNum++;
							}
						}
					}
				}
				//Cluster all schools together
				else {
					//All together - All schools
					if(runForAllSchs) {
						BatchClusterSchools.batchClusterSchs(districtWithLonLat, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "All-All");
					}
					//All together - Only elementary schools
					if(runForOnlyEle) {
						BatchClusterSchools.batchClusterSchs(eleSchools, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "All-Eles");
					}
					//Get vectors of: all Ele+Middle,  all Middle, all High.
					Vector<SchoolInformation> onlyEleAndMiddleSchs = new Vector<SchoolInformation>();
					Vector<SchoolInformation> onlyMiddleSchs = new Vector<SchoolInformation>();
					Vector<SchoolInformation> onlyHighSchs = new Vector<SchoolInformation>();
					for(SchoolInformation school : districtWithLonLat) {	
						if(school.getSchoolType().contains("Elementary") ) {
							onlyEleAndMiddleSchs.add(school);
						}
						if(school.getSchoolType().contains("Middle") ) {
							onlyMiddleSchs.add(school);
							onlyEleAndMiddleSchs.add(school);
						}
						if(school.getSchoolType().contains("High") ) {
							onlyHighSchs.add(school);
						}
					}
					
					//All together - Only Middle school(s)
					if(runForOnlyMiddle) {
						BatchClusterSchools.batchClusterSchs(onlyMiddleSchs, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "All-MS");
					}
					//All together - Only High school(s)
					if(runForOnlyHigh) {
						BatchClusterSchools.batchClusterSchs(onlyHighSchs, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "All-HS");
					}
					//All together - Only Middle and High school(s)
					if(runForMiddleAndHigh) {
						BatchClusterSchools.batchClusterSchs(mergeMiddleHigh, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "All-MSHS");
					}
					//All together - Only Elementary and Middle school(s)
					if(runForEleAndMiddle) {
						BatchClusterSchools.batchClusterSchs(onlyEleAndMiddleSchs, districtWithLonLat, polygon, selectedAddrs, state, distNum, fileNum, "All-ElesMS");
					}
					
				}
				
				distNum++;
				System.out.println("County: " + district.get(0).getCountyName() + " --- " + district.get(0).getDistName() + " - Routing for district is done"); 
			}
			System.out.println(countyNum + " - Routing for county: '" + county.get(0).get(0).getCountyName() + "' is generated");
			countyNum++;
			
		}
	
	
		
		
	}
}
