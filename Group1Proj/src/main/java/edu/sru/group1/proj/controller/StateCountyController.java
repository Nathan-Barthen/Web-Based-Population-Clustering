/**
 * @author Connor
 * @version 2/18/2022
 *
 */

package edu.sru.group1.proj.controller;

import java.io.IOException;
import java.util.*;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.stereotype.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.sru.group1.proj.domain.StateCounty;
import edu.sru.group1.proj.dbscan.Cluster;
import edu.sru.group1.proj.dbscan.ClustersForSchool;
import edu.sru.group1.proj.dbscan.DBScan;
import edu.sru.group1.proj.dbscan.DataPoint;
import edu.sru.group1.proj.dbscan.SchoolInfoAndCluster;
import edu.sru.group1.proj.dbscan.SchoolsLongLat;
import edu.sru.group1.proj.domain.AddrsPerCluster;
import edu.sru.group1.proj.domain.AllClusteringInfo;
import edu.sru.group1.proj.domain.CountyDataset;
import edu.sru.group1.proj.domain.EmailFile;
import edu.sru.group1.proj.domain.RoutingExcelFiles;
import edu.sru.group1.proj.domain.SchoolInfoDataset;
import edu.sru.group1.proj.domain.SchoolInformation;
import edu.sru.group1.proj.repository.User;
import edu.sru.group1.proj.repository.UserRepository;
import edu.sru.group1.proj.shapefiles.SelectPolygonAndAddrs;
import edu.sru.group1.proj.shapefiles.CreateShapefileDisplay;
import edu.sru.group1.proj.shapefiles.Record;

/**
 * 
 * Main Class that controls county information.
 *
 */
@Controller
public class StateCountyController {
	
	//This vector stores information for each unique county.
	private Vector<StateCounty> currentStateCounty;
	//Stores the unique names for each county.
	private Vector<String> counties;
	//Stores the current state for displaying the website information.
	private String currentState;
	//Stores the current county for displaying the website information.
	private String currentCounty;
	private String currentDistrict;
	//Stores the id value for the selected county (used in "/county/{schoolDistrict}" page)
	private long currentId;
	private int showRouting;
	private int showEmail;
	private int alertError;
	private double clusterDiameter;
	private Vector<SchoolInformation> schoolsInfo;
	private List<AllClusteringInfo> allSchsInfo;
	private long currUserId;
	private String rountingName;
	/**
	 * 
	 * @return returns the current county.
	 */
	public Vector<StateCounty> getCurrentStateCounty() {
		return currentStateCounty;
	}
	/**
	 * sets the current county through accessing the vector.
	 * @param currentStateCounty passes the states county from the vector.
	 */
	public void setCurrentStateCounty(Vector<StateCounty> currentStateCounty) {
		this.currentStateCounty = currentStateCounty;
	}
	/**
	 * Getter method for state counties.
	 * @return returns the counties in the state,
	 */
	public Vector<String> getCounties() {
		return counties;
	}
	/**
	 * Setter method for the state counties.
	 * @param counties passes the counties from the vector of the accessed state.
	 */
	public void setCounties(Vector<String> counties) {
		this.counties = counties;
	}
	/**
	 * Getter method for currentDistrict.
	 * @return returns the district in the state,
	 */
	public String getCurrentDistrict() {
		return currentDistrict;
	}
	/**
	 * Setter method for the currentDistrict.
	 * @param currentDistrict passes the district name.
	 */
	public void setCurrentDistrict(String currentDistrict) {
		this.currentDistrict = currentDistrict;
	}
	
	/**
	 * Getter method for the current state.
	 * @return returns the current state being accessed.
	 */
	public String getCurrentState() {
		return currentState;
	}
	/**
	 * Sets the current state being accessed. 
	 * @param currentState passed the current state.
	 */
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	/**
	 * Getter method for the current county.
	 * @return returns the current county being accessed.
	 */
	public String getCurrentCounty() {
		return currentCounty;
	}
	/**
	 * Setter method for the current county.
	 * @param currentCounty the county being accessed.
	 */
	public void setCurrentCounty(String currentCounty) {
		this.currentCounty = currentCounty;
	}
	/**
	 * Setter and Getter for currentId.
	 * @return returns the current Id.
	 */
	public long getCurrentId() {
		return currentId;
	}
	/**
	 * Getter method for the alertError.
	 * @return returns the int on whether to display error alert.
	 */
	public int getAlertError() {
		return alertError;
	}
	/**
	 * Setter for alertError.
	 * @param alertError returns number specifying whether to alert and error.
	 */
	public void setAlertError(int alertError) {
		this.alertError = alertError;
	}
	/**
	 * Getter method for the clusterDiameter.
	 * @return returns the clusterDiameter used for DBScan.
	 */
	public double getClusterDiameter() {
		return clusterDiameter;
	}
	/**
	 * Setter for clusterDiameter.
	 * @param clusterDiameter is used to set the the clusterDiameter.
	 */
	public void setClusterDiameter(double clusterDiameter) {
		this.clusterDiameter = clusterDiameter;
	}
	/**
	 * stores the Id being accessed.
	 * @param id id passed the current id being used. 
	 */
	public void storeCurrentId(long id) {
		this.currentId = id;
	}
	/**
	 * Getter method for showRouting.
	 * @return returns int on whetehr to show routing information.
	 */
	public int getShowRouting() {
		return showRouting;
	}
	/**
	 * Setter for showRouting.
	 * @param showRouting passes int on whether to show routing information.
	 */
	public void setShowRouting(int showRouting) {
		this.showRouting = showRouting;
	}
	/**
	 * Gets the shown Email.
	 * @return returns showEmail
	 */
	public int getShowEmail() {
		return showEmail;
	}
	/**
	 * Sets the shown Email.
	 * @param showEmail passes the shown Email
	 */
	public void setShowEmail(int showEmail) {
		this.showEmail = showEmail;
	}
	/**
	 * Getter method for the schoolInfo vector.
	 * @return returns the vector of the all the school in the district.
	 */
	public Vector<SchoolInformation> getSchoolsInfo() {
		return schoolsInfo;
	}
	/**
	 * Sets the schoolsInfo for the district.
	 * @param schoolsInfo passes vector of schools with info. 
	 */
	public void setSchoolsInfo(Vector<SchoolInformation> schoolsInfo) {
		this.schoolsInfo = schoolsInfo;
	}
	
	/**
	 * Gets the clustering information for every requested clustering.
	 * @return returns the class containing all school and cluster information.
	 */
	public List<AllClusteringInfo> getAllSchsInfo() {
		return allSchsInfo;
	}
	/**
	 * Sets the clustering information for every requested clustering.
	 * @param allSchsInfo passes AllClusteringInfo of clustering. 
	 */
	public void setAllSchsInfo(List<AllClusteringInfo> allSchsInfo) {
		this.allSchsInfo = allSchsInfo;
	}

	/**
	 * Gets the users id (used for emailing routing file, SMTP).
	 * @return returns current user's id.
	 */
	public long getCurrUserId() {
		return currUserId;
	}
	/**
	 * Sets the id of the current user, used for email SMTP (emailing them the routing file).
	 * @param currUserId passes the users id. 
	 */
	public void setCurrUserId(long currUserId) {
		this.currUserId = currUserId;
	}

/**
 * Gets the routing name.
 * @return returns the routing name
 */
	public String getRountingName() {
		return rountingName;
	}
	/**
	 * Sets the routing name.
	 * @param rountingName passes the routing name
	 */
	public void setRountingName(String rountingName) {
		this.rountingName = rountingName;
	}


	//set up a StateCountyRepository variable
	private UserRepository userRepository;
	//private StateCountyRepository userRepository2;
    
	/**
	 * create an StateCountyRepository instance - instantiation (new) is done by Spring
	 * @param userRepository passed a county from the vector.
	 * 
	 */
    public StateCountyController(UserRepository userRepository) {
		this.userRepository = userRepository;
		counties = new Vector<String>();
		currentStateCounty = new Vector<StateCounty>();
		schoolsInfo = new Vector<SchoolInformation>();
		allSchsInfo = new ArrayList();
		currentDistrict = "empty";
		showRouting=0;
		alertError=0;
		clusterDiameter=.029;
		showEmail = 0;
	} 
     
    /**
     * Default mapping to select a state.
     * @param model passes the model attribute for states.
     * @return returns states.
     */
    @RequestMapping({"/"})
    public String showStates(Model model) {
    	//Temp user1 used for testing until we have generic login/signup. 
    	User user1 = new User();
    	user1.setEmail("routingfileemail@gmail.com");
    	userRepository.save(user1);
    	setCurrUserId(user1.getId());
        return "states";
    }
    
    /**
     * Default mapping to select county.
     * @param state passes the selected state from the state variable.
     * @param model passed the model attribute being accessed.
     * @return returns the county they select.
     */
    @RequestMapping({"/{state}"})
    public String showCountyList(@PathVariable("state") String state, Model model) {
    	/**
    	 * 
    	 */
    	//Stores unique counties by calling setCounties.
    				
    	CountyDataset data = new CountyDataset();
    			
    	setCounties(data.countyFile(state));
    			
    	//While loop - loops thought the counties vector, storing county information (States, county name, and district(s) )
    			//Saves each StateCounty instance
    	int i = 0;
    	Vector<StateCounty> tempCurrStateCounty = new Vector<StateCounty>();
    	while(i < (getCounties()).size()) {
    		StateCounty county = new StateCounty();
    		county.setState(state);
    		setCurrentState(state);
    		county.setCounty((getCounties()).get(i));
    		//Vector<String> dists = data.getDistrict(counties.get(i));
    		county.setDistrict( data.getDistricts(state, (getCounties()).get(i)) );
    		tempCurrStateCounty.add(county);
    		i++;
    	}
    	setCurrentStateCounty(tempCurrStateCounty);
    	model.addAttribute("counties", this.currentStateCounty);
    	model.addAttribute("state", state);
        return "county";
    }
    
    /**
     * Mapping to select school district.
     * @param countyName passes the name of the county in a state.
     * @param model passes the model attribute being accessed for the district.
     * @return returns the necessary school districts. 
     */
    @RequestMapping({"county/{countyName}"})
    public String selectDistrict(@PathVariable("countyName") String countyName, Model model) {
    	setCurrentCounty(countyName);
    	StateCounty county = new StateCounty();
    	Vector<String> dists = new Vector<>();
    	CountyDataset data = new CountyDataset();
    	//Generates all of the districts in that particular county.
    	dists = data.getDistricts(getCurrentState(), countyName);
    	
    	
    	model.addAttribute("state", getCurrentState());
    	model.addAttribute("countyN", countyName);
        model.addAttribute("districts", dists);
        storeCurrentId(county.getId());
        return "district";
    }
    
    
    /**
     * Mapping that displays school district information.
     * @param district passes the school district being accessed.
     * @param model passes the model attribute for the school district.
     * @return returns the information for a school district.
     */
    @RequestMapping({"county/state/{schoolDistrict}"})
    public String selectSchool(@PathVariable("schoolDistrict") String district, Model model) {
        //Attribute stores an instance of StateCounty pertaining to selected district
    	model.addAttribute("state", getCurrentState());
    	//Attribute stores string of current county (county name)
        model.addAttribute("county", getCurrentCounty());
        //Attribute stores string of school district
        model.addAttribute("districtName", district);
        String prevDistrict = getCurrentDistrict();
        setCurrentDistrict(district);
        SchoolInfoDataset data = new SchoolInfoDataset();
        
        //Takes the string input of school district from mapping, calls method in SchoolInfoDataset.
        	//Returns the a vector of type SchoolInformation which contains each schools information.
		Vector<SchoolInformation> schoolsData = data.schoolsDataNew(getCurrentState(), district, getCurrentCounty());
		if(getSchoolsInfo().size() == 0 || !prevDistrict.contains(district)) {
			setSchoolsInfo(schoolsData);
		}
		//Clears AllClusteringInfo, if they select a new district
		if(!prevDistrict.contains(district)) {
			List<AllClusteringInfo> allInfoList = new ArrayList();
			setAllSchsInfo(allInfoList);
		}
		
		int i=0;
		Vector<SchoolInformation> setSchoolsIds = getSchoolsInfo();
		//sets id's for schools in the district (important if they add a school/delete one)
		for(SchoolInformation school : getSchoolsInfo()) {
			setSchoolsIds.get(i).setId(i);
			i++;
		}
		//Saves schools with new id's set
		setSchoolsInfo(setSchoolsIds);
		
		int showClusters = 0; //Used for graying out 'See All Clusters' button if no clusters have been generated yet.
		if(getAllSchsInfo().size() > 0)
		{
			showClusters = 1;
		}
		//Used to alert and error if clustering fails. 
		if(getAlertError() == 1) {
			model.addAttribute("alertError", 1);
			setAlertError(0); //Insufficient addresses.
		}
		else if (getAlertError() == 2) {
			model.addAttribute("alertError", 2);
			setAlertError(0); //School(s) address is incorrect.
		}
		else if (getAlertError() == 3) {
			model.addAttribute("alertError", 3);
			setAlertError(0); //The polygon for the selected district does not exist.
		}
		else {
			model.addAttribute("alertError", 0); //No errors. 
		}
		
		//Used for displaying/saving the radius selected by user (used in DBScan clustering).
		double[] diameterConversion = {.0036, .0072, .0109, .014, .022, .029, .036, .042, .051, .058};
		String[] diameterConversionSrt = {".25 Mile", ".5 Mile",".75 Mile","1 Mile", "1.5 Miles","2 Miles", "2.5 Miles", "3 Miles", "3.5 Miles", "4 Miles",};
		String currDiameter = "";
		for(int iter=0; i<diameterConversion.length; iter++) {
			if(diameterConversion[iter] == getClusterDiameter()) {
				currDiameter = "Current: " + diameterConversionSrt[iter];
				break;
			}
		}
		
		model.addAttribute("currDiameter", currDiameter);
		model.addAttribute("schoolsInfo", getSchoolsInfo());
		model.addAttribute("showClusters", showClusters);
		
		setShowEmail(0);
		
        return "district-info";
    }
    
    /**
     * Main class to display the clustering
     * @param ids passes the id's
     * @param model passes the model used
     * @return returns clustered district
     * @throws IOException throws IOException
     */
    @RequestMapping({"county/state/district/clustering/{ids}"})
    public String displayClustering(@PathVariable("ids") String ids, Model model) throws IOException {
        
    	model.addAttribute("state", getCurrentState());
    	//Attribute stores string of current county (county name)
        model.addAttribute("county", getCurrentCounty());
        //Attribute stores string of school district
        model.addAttribute("districtName", getCurrentDistrict());
        
        String[] schoolIds = ids.split(",");
        
		SelectPolygonAndAddrs shapefile = new SelectPolygonAndAddrs();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		MultiPolygon polygon = new MultiPolygon(null, geometryFactory);
		//Searches for the district polygon, saves it to polygon.
		polygon = shapefile.DistrictIndex(getCurrentDistrict(), getCurrentState());
		if( polygon.getCoordinates().length == 0){  //If polygon is empty (not found) cancel clustering.
			System.out.println("Canceled Clustering. " + " Reason: Polygon does not exist for district");
			setAlertError(3);
			return "redirect:/county/state/" + getCurrentDistrict();
		}
		Coordinate[] districtPolygon = polygon.getCoordinates();
		//Selects all of the addresses inside of the given polygon.
		List<Record> selectedAddrs = shapefile.SelectAddresses(districtPolygon, getCurrentCounty(), getCurrentState());
		//Convert Record to type DataPoint (for clustering)
		List<DataPoint> datapoints = new ArrayList();
		for(Record record : selectedAddrs) {
			DataPoint p = new DataPoint(record.getLon(), record.getLat());
			datapoints.add(p);
		}
		//DBSCAN - cluster of ALL addresses in district
		
		//Any cluster smaller than minClusterSize will be ignored as noise
			//Cluster must be at least 2% of all addresses in the district.
			//Hook point: For adding new clustering method.
		int minClusterSize = (int) (datapoints.size() * .02);
		DBScan test = new DBScan(getClusterDiameter(), minClusterSize);
		test.setPoints(datapoints);
		test.cluster();
		List<Cluster> largeClusters = test.getClusters();
		
		int totalAddrs =0;
		for(Cluster cluster : largeClusters) {
			cluster.calculateCentroid();
			totalAddrs += cluster.getPoints().size();
		}
		
		
		//Generate clusters for schools
		SchoolsLongLat generateSchoolCoords = new SchoolsLongLat();
		//Generates the schools (in given district) lon/lat and saved them (used for selecting addresses)
		setSchoolsInfo(generateSchoolCoords.getSchoolsLongLat(getSchoolsInfo(), getCurrentState().toLowerCase())); 
		
		//Get the schools selected for clustering
        Vector<SchoolInformation> schoolsData = new Vector<SchoolInformation>();
        int totalStuToCluster = 0;
		for(int i=0; i<schoolIds.length; i++) {
			for(SchoolInformation school : getSchoolsInfo()) {
				if(school.getId() == Integer.parseInt(schoolIds[i])) {
					schoolsData.add(school);
					totalStuToCluster += Integer.parseInt(school.getNumStudents());
				}
			}
		}
		//If the district does not have enough data (addresses) redirect back to district page.
		if( totalStuToCluster > totalAddrs){
			System.out.println("Canceled Clustering. " + " Reason: Insufficient Addresses");
			setAlertError(1);
			return "redirect:/county/state/" + getCurrentDistrict();
		}
		List< SchoolInfoAndCluster > everySchoolsInfo = new ArrayList();
		ClustersForSchool clustersForSchoolFile = new ClustersForSchool();
		//Assigns clusters to schools (passed) and assigns student addresses to each school (split into clusters)
		everySchoolsInfo = clustersForSchoolFile.generateSchoolClusters(largeClusters, getSchoolsInfo(), schoolsData);
		
		//Get schoolInfo from SchoolInfoAndCluster to display to screen.
		List< SchoolInformation > schoolsInfo = new ArrayList();
		for(SchoolInfoAndCluster school : everySchoolsInfo) {
			schoolsInfo.add(school.getSchoolInfo());
		}
		model.addAttribute("schInfo", schoolsInfo);
		//Cancels clustering - address is incorrect
		if( everySchoolsInfo.size() == 0){
			System.out.println("Canceled Clustering. " + " Reason: School(s) Address is incorrect");
			setAlertError(2);
			return "redirect:/county/state/" + getCurrentDistrict();
		}
		model.addAttribute("numClusters", everySchoolsInfo.get(0).getCluster().size());
		int totalStuAddrs = 0;
		int i=0;
		Vector<AddrsPerCluster> addrsPerCluster = new Vector<>();
		//Gets clustering information (used for html display)
		for(Cluster cluster : everySchoolsInfo.get(0).getCluster()) {
			totalStuAddrs += cluster.getPoints().size();
			AddrsPerCluster clust = new AddrsPerCluster(i, cluster.getPoints().size(), cluster.getSchoolName());
			addrsPerCluster.add(clust);
			i++;
		}
		model.addAttribute("numStu", totalStuAddrs);
		model.addAttribute("clusters", addrsPerCluster);
		//Saves clustering to AllClusteringInfo (used at html display that shows all of the clusters/separate routings)
		AllClusteringInfo allInfo = new AllClusteringInfo(totalStuAddrs, schoolsInfo, everySchoolsInfo.get(0).getCluster(), addrsPerCluster);
		List<AllClusteringInfo> allInfoList = getAllSchsInfo();
		allInfoList.add(allInfo);
		int setIndex = 0;
		for(AllClusteringInfo allSchInfo1 : allInfoList)
		{
			allSchInfo1.setSetIndex(setIndex);
			setIndex++;
		}
		
		//Creates the geoJson file that can be used for the rudimentary display to show clusterings/district.
		//Files stored in default directory with name 'shapefile-GEOJSON.geojson'
			//To veiw example of this file: upload it to https://mapshaper.org/ 
		String geoJson = CreateShapefileDisplay.createClusterShapefile(polygon, allInfo);
		//Passes the string of the geoJson file. 
		model.addAttribute("geoJson", geoJson);
		
		setAllSchsInfo(allInfoList);
        
        return "display-clustering";
    }
    
  /**
   * Displays all of the clusters  
   * @param model passes the model
   * @return returns display all clustering
   */
    @RequestMapping({"county/state/district/clustering"})
    public String displayAllClustering(Model model) {
       
    	model.addAttribute("state", getCurrentState());
    	//Attribute stores string of current county (county name)
        model.addAttribute("county", getCurrentCounty());
        //Attribute stores string of school district, all schools info (with clusterInfo), int for displaying/hiding routing (future work)
        model.addAttribute("districtName", getCurrentDistrict());
		model.addAttribute("allInfoList", getAllSchsInfo());
		model.addAttribute("showRouting", getShowRouting());
		
		Optional<User> user1 = userRepository.findById(getCurrUserId());
		model.addAttribute( "user", user1.get() );
		//Used for alert to display
		model.addAttribute("showEmail", getShowEmail());
		
        return "display-all-clustering";
    }
    
  //Gets called from county/state/district/clustering (Route button)
    /**
     * Gets called forom the route button and executes the route clustering.
     * @param setIndex passes the index
     * @param model passes the model
     * @return returns the redirect
     */
    @GetMapping("/route/clustering/{setIndex}")
    public String routeClustering(@PathVariable("setIndex") int setIndex, Model model) {
    	setShowRouting(1);
    	//Find which clustering to Route
    	AllClusteringInfo clustersToRoute = null;
		for(AllClusteringInfo allInfo : getAllSchsInfo()) {
			if(allInfo.getSetIndex() == setIndex) {
				clustersToRoute = allInfo;
				break;
			}
		}
		//Generate excel file for routing and save file name to setRoutingName().
		//File Example Name - School-school-school-RoutingInfo.csv (file saved to default workspace directory)
		setRountingName(RoutingExcelFiles.generateRoutingFile(clustersToRoute.getSchoolsInfo(), clustersToRoute.getClusterInfo(), getCurrentDistrict()));
	
		//Do Routing (Dr. Thangiah) -- Generate Routing File
		
		setShowEmail(1);
		
        return "redirect:/county/state/district/clustering/";
    }  
   /**
    * Class for the route email 
    * @param model passes the model 
    * @return returns the redirect
    */
    @GetMapping("/route/clustering/sendEmail")
    public String routeEmail(Model model) {
    	
    	if(getShowEmail() == 0) {
    		 return "redirect:/county/state/district/clustering/";
    	}
    	
    	Optional<User> user1 = userRepository.findById(getCurrUserId());
    	
    	final String fromEmail = "routingfileemail@gmail.com"; //requires valid gmail id
		final String password = "lazzoslzlprwcgxc"; // password for apps from google
													// password for user login on browser: RoutingPass123
		final String toEmail = user1.get().getEmail(); // can be any email id 
    	
    	Properties props = System.getProperties();
    	props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
		props.put("mail.smtp.port", "587"); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getInstance(props, auth);
    	String subject = "Bus Rounting File";
    	String body = "";
    	//sends the email to the user's email.
    	EmailFile.sendAttachmentEmail(session, toEmail, subject, body, getRountingName());
    	
        return "redirect:/county/state/district/clustering/";
    }
    /**
     * Class for the route email from textbox
     * @param model passes the model 
     * @return returns the redirect
     */
     @GetMapping("/route/clustering/sendEmailFromTextBox/{textBoxEmail}")
     public String routeEmailFromTextBox(@PathVariable("textBoxEmail") String textBoxEmail, Model model) {
     	//Sends the email to the email entered in the alternative textbox. 
     	if(getShowEmail() == 0) {
     		 return "redirect:/county/state/district/clustering/";
     	}
     	
     	
     	
     	final String fromEmail = "routingfileemail@gmail.com"; //requires valid gmail id
 		final String password = "lazzoslzlprwcgxc"; // password for apps from google
 													// password for user login on browser: RoutingPass123
 		final String toEmail = textBoxEmail; // can be any email id 
     	
     	Properties props = System.getProperties();
     	props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
 		props.put("mail.smtp.port", "587"); //TLS Port
 		props.put("mail.smtp.auth", "true"); //enable authentication
 		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

 		Authenticator auth = new Authenticator() {
 			//override the getPasswordAuthentication method
 			protected PasswordAuthentication getPasswordAuthentication() {
 				return new PasswordAuthentication(fromEmail, password);
 			}
 		};
 		Session session = Session.getInstance(props, auth);
     	String subject = "Bus Rounting File";
     	String body = "";
     	//sends the email to the email entered in textbox.
     	EmailFile.sendAttachmentEmail(session, toEmail, subject, body, getRountingName());
     	
         return "redirect:/county/state/district/clustering/";
     }
    
    /**
     * Class for setting the cluster diameter
     * @param clusterDiameter passes the cluster diameter
     * @param model passes the model
     * @return returns the redirect and the current District
     */
    @GetMapping("/county/state/setDiameter/{clusterDiameter}")
    public String setClustDiameter(@PathVariable("clusterDiameter") double clusterDiameter, Model model) {
    	//Sets the clustering diameter to the one clicked by the user on the webpage (default is 2 miles)
    	setClusterDiameter(clusterDiameter);
        return "redirect:/county/state/" + getCurrentDistrict();
    }  
  /**
   * Class for editing schools  
   * @param schName passes the school name
   * @param model passes the model
   * @return returns the edited school
   */
    @GetMapping(value ="county/state/district/edit/{schoolName}")
    public String editSchool(@PathVariable("schoolName") String schName, Model model) {
        //Find the school
    	
    	SchoolInformation editSchool = null;
		for(SchoolInformation school : getSchoolsInfo()) {
			if(school.getSchoolName().contains(schName)) {
				editSchool = school;
				break;
			}
		}
    	
		model.addAttribute("school", editSchool);
        
        return "edit-school";
    }
  /**
   * Class for updating schools information
   * @param schName passes the school name
   * @param school passes the school
   * @param result passes the result
   * @param model passes the model
   * @return returns the current district
   */
    @PostMapping("/update/{schoolName}")
    public String updateSchool(@PathVariable("schoolName") String schName, @Validated SchoolInformation school, 
      BindingResult result, Model model) {
    	
        if (result.hasErrors()) {
            school.setSchoolName(schName);
            return "edit-school";
        }
        
        Vector<SchoolInformation> schoolsData = getSchoolsInfo();
        for(int i=0; i<getSchoolsInfo().size(); i++) {
			if(getSchoolsInfo().get(i).getSchoolName().contains(schName)) {
				SchoolInformation updateSchool = schoolsData.get(i);
				schoolsData.remove(i);
				updateSchool.setNumStudents(school.getNumStudents());
				updateSchool.setNumBuses(school.getNumBuses());
				updateSchool.setBusCapacity(school.getBusCapacity());
				updateSchool.setTravelTime(school.getTravelTime());
				updateSchool.setStuPercent(school.getStuPercent());
				schoolsData.add(i, updateSchool);
				
				setSchoolsInfo(schoolsData);
				break;
			}
		}
      
        return "redirect:/county/state/" + getCurrentDistrict();
    }  
    /**
     * Class for the sign up form
     * @param school passes the school
     * @param model passes the model
     * @return returns the added school
     */
	@RequestMapping({"/addschoolsignup"})
    public String showSignUpForm(SchoolInformation school, Model model) {
		school = new SchoolInformation();
		model.addAttribute("school", school);
		
        return "add-school";
    }
	/**
	 * Class for adding a school
	 * @param school passes the school
	 * @param result passes the result
	 * @param model passes the model
	 * @return returns current district redirect
	 */ 
    @RequestMapping({"/addschool"})
    public String addSchool(@Validated SchoolInformation school, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-school";
        }
        Vector<SchoolInformation> schoolsData = getSchoolsInfo();
        school.setSchoolType();
        schoolsData.add(school);
        setSchoolsInfo(schoolsData);
        
        return "redirect:/county/state/" + getCurrentDistrict();
    }
   /**
    * Class for deleting a school 
    * @param schoolName passes the school name
    * @param model passes the model
    * @return returns the current district redirect
    */
    @GetMapping("/delete/{schoolName}")
    public String deleteSchool(@PathVariable("schoolName") String schoolName, Model model) {
    	SchoolInformation deleteSchool = null;
		for(SchoolInformation school : getSchoolsInfo()) {
			if(school.getSchoolName().contains(schoolName)) {
				deleteSchool = school;
				break;
			}
		}
		Vector<SchoolInformation> schoolsData = getSchoolsInfo();
		schoolsData.remove(deleteSchool);
		setSchoolsInfo(schoolsData);
        return "redirect:/county/state/" + getCurrentDistrict();
    }  
  /**
   * Class for deleting clusters  
   * @param index passes the index
   * @param model passes the model
   * @return returns the clustering redirect
   */
    @GetMapping("/delete/cluster/{index}")
    public String deleteClusters(@PathVariable("index") int index, Model model) {
    	AllClusteringInfo deleteCluster = null;
    	List<AllClusteringInfo> allInfoList = getAllSchsInfo();
		for(AllClusteringInfo allInfo: allInfoList) {
			if(allInfo.getSetIndex() == index) {
				deleteCluster = allInfo;
				break;
			}
		}
		allInfoList.remove(deleteCluster);
		setAllSchsInfo(allInfoList);
        return "redirect:/county/state/district/clustering";
    }  
    

}

