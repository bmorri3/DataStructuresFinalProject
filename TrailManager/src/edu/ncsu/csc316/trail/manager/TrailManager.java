package edu.ncsu.csc316.trail.manager;

import java.io.FileNotFoundException;

import edu.ncsu.csc316.dsa.list.List;
import edu.ncsu.csc316.dsa.map.Map;
import edu.ncsu.csc316.trail.data.Landmark;
import edu.ncsu.csc316.trail.data.Trail;
import edu.ncsu.csc316.trail.dsa.DSAFactory;
import edu.ncsu.csc316.trail.io.TrailInputReader;
import edu.ncsu.csc316.trail.manager.ReportManager.DescriptionComparator;

/**
 * Manages the data of the files passed. TrailManager finds the distances to destinations given an
 * origin. It can fetch Landmarks by LandmarkID. It can get a List of proposed first
 * aid stations based on the minimum number of intersecting trails. 
 * @author Ben Morris
 */
public class TrailManager {
    /** Field for LandmarkFile */
	private String pathToLandmarkFile;
	/** Field for TrailFile */
	private String pathToTrailFile;
	/** Map of Landmarks to their trails */
	private Map<Landmark, List<Trail>> ltMap;
	/** Map of LandmarkIDs->Landmarks */
	private Map<String, Landmark> idMap;
	/** Track maximum number of intersections while reading in Trails */
	private int maxIntersections;
	/** List of Landmarks */
	private List<Landmark> landmarkList;
	/** List of trails */
	private List<Trail> trailList;

	
	/**
	 * Constructor
	 * @param pathToLandmarkFile Path to LandmarkFile
	 * @param pathToTrailFile Path to TrailFile
	 * @throws FileNotFoundException If either file isn't found.
	 */
	public TrailManager(String pathToLandmarkFile, String pathToTrailFile) throws FileNotFoundException {
		setPathtolandmarkfile(pathToLandmarkFile);
	    setPathtotrailsfile(pathToTrailFile);
	    
	    // Calls setup method to create ltMap and idMap
	    setup();
	}

	/**
     * Sets up the map of Landmarks to trails that each intersects
     */
    private void setup () throws FileNotFoundException {
    	landmarkList = DSAFactory.getIndexedList();
    	trailList = DSAFactory.getIndexedList();    	
    	ltMap = DSAFactory.getMap(new DescriptionComparator());
    	idMap = DSAFactory.getMap(null);
    	
    	// Get Landmarks
    	try {
    		landmarkList = TrailInputReader.readLandmarks(getPathtolandmarkfile());
		} catch (Exception e) {
			throw new FileNotFoundException();
		}
    	
    	// Get Trails
    	try {
    		trailList = TrailInputReader.readTrails(getPathtotrailsfile());
		} catch (Exception e) {
			throw new FileNotFoundException();
		}
    	
    	// Initialize the max number of intersections to zero
    	setMaxIntersections(0);
    	
    	// Create a Map of ID->Landmark
    	for (Landmark landmark : landmarkList) {
			idMap.put(landmark.getId(), landmark);			
		}    		
    	
    	// Create the Map of Landmark->List<Trail>
    	for (Trail trail : trailList) {
    		// Get the two end points of the trail
    		Landmark landmark1 = getLandmarkByID(trail.getLandmarkOne());
    		Landmark landmark2 = getLandmarkByID(trail.getLandmarkTwo());   			    	

    		List<Trail> trail1 = DSAFactory.getIndexedList();    	
    		if (ltMap.get(landmark1) == null) {    			
    			//idMap.put(trail.getLandmarkOne(), landmark1);    			
    			ltMap.put(landmark1, trail1);
    		}
    		else
    			trail1 = ltMap.get(landmark1);
    		
    		List<Trail> trail2 = DSAFactory.getIndexedList();    		
    		if (ltMap.get(landmark2) == null) {
    			//idMap.put(trail.getLandmarkTwo(), landmark2);
    			ltMap.put(landmark2, trail2);
    		}
    		else
    			trail2 = ltMap.get(landmark2);
    		
    		// Add the two end points to the map entries of Landmarks at the end points
    		trail1.addLast(trail);
    		trail2.addLast(trail);
    		
    		// Get the sizes of the Lists of Trails to compare to max number of intersections
    		int size1 = trail1.size();
    		int size2 = trail2.size();

    		// Compare and set new max if necessary
    		if (size1 > maxIntersections)
				setMaxIntersections(size1);
    		if (size2 > maxIntersections)
				setMaxIntersections(size2);
		}
    }
	
	/**
	 * Returns pathToLandmarkFile
	 * @return the pathtolandmarkfile
	 */
	public String getPathtolandmarkfile() {
		return pathToLandmarkFile;
	}

	/**
	 * Sets pathToLandmarkFile
	 * @param pathtolandmarkfile the pathtolandmarkfile to set
	 */
	public void setPathtolandmarkfile(String pathtolandmarkfile) {
		pathToLandmarkFile = pathtolandmarkfile;
	}

	/**
	 * Returns pathToTrailFile
	 * @return the pathToTrailFile
	 */
	public String getPathtotrailsfile() {
		return pathToTrailFile;
	}

	/**
	 * Sets pathToTrailFile
	 * @param pathtotrailfile the pathToTrailFile to set
	 */
	public void setPathtotrailsfile(String pathtotrailfile) {
		pathToTrailFile = pathtotrailfile;
	}

	/**
	 * Returns ltMap
	 * @param numberOfIntersectingTrails Minimum number of intersecting trails at a Landmark
	 *   	  for the Landmark to be place in the report
	 * @return ltMap
	 */
	public Map<Landmark, List<Trail>> getProposedFirstAidLocations(int numberOfIntersectingTrails) {
		return ltMap;
	}

	/**
	 * Creates a Map of Landmarks to distances of Landmark from originLandmark
	 * @param originLandmark Landmark to measure distance to
	 * @return Map of Landmarks to distances of Landmark from originLandmark
	 */
	public Map<Landmark, Integer> getDistancesToDestinations(String originLandmark) {

		// If the origin is null
		if(getLandmarkByID(originLandmark) == null)
    		return DSAFactory.getMap(null);
    				
		// Create a new map to store Landmarks and distances to start
		Map<Landmark, Integer> distanceMap = DSAFactory.getMap(null);
		
		// Create previous to check if next node has already been visited
		String previous = null;

		// Initialize totalDistance to zero
		int totalDistance = 0;
		// Call helper method getNeighborDistances() to iterate through start’s neighbors
		getNeighborDistances(ltMap, originLandmark, previous, distanceMap, totalDistance);
		
    	return distanceMap;
    }
	
	/**
	 * Helper method. Recursively called in order to get the distance of Landmark to origin
	 * @param ltMap2 Map of Landmark->List of Trails
	 * @param current current Landmark ID
	 * @param previous previous Landmark ID visited
	 * @param distanceMap Map of Landmark->distance to the origin of Landmark
	 * @param totalDistance total distance to the origin to parent node
	 */
    private void getNeighborDistances(Map<Landmark, List<Trail>> ltMap2, String current, String previous,
			Map<Landmark, Integer> distanceMap, int totalDistance) {
    	
    	// Get the list of Trails intersecting current Landmark
    	Landmark currentLandmark = getLandmarkByID(current);
    	
    	// List of trails to currentLandmark
    	trailList = ltMap2.get(currentLandmark);        	
    	
    	if(trailList != null)
    		for (Trail trail : trailList) {
	    	    // Use helper method getOtherEndpoint to get the other endpoint of the Trail
	    		String next = getOtherEndpoint(trail, current);    		
	    		
	    	    // (Since there are no loops, the only way for next to have been already visited is
	    	    // if it were the previous node to current.) If the next Landmark is not the same 
	    	    // as the previous Landmark to current, add to the total distance and put the new
	    	    // Landmark in the distance Map.
	    		if(!next.equals(previous)) {
	    			// Update totalDistance by adding distance to this Landmark
	    			totalDistance += trail.getLength();
	    			// Add this this Landmark and distance to the distanceMap
	    			distanceMap.put(getLandmarkByID(next), totalDistance);
	
	    			// Call getNeighborDistances() for next with current becoming the previous node
	    			getNeighborDistances(ltMap2, next, current, distanceMap, totalDistance);
	
	    	        // Decrement totalDistance after all of start’s neighbors’ distances are
	    	        // calculated before moving to the next neighbor of previous
	    			totalDistance -= trail.getLength();
	    		}
    		}
	}

    /**
     * Gets the endpoint2 of the Trail to endpoint1
     * @param trail Trail
     * @param endpoint1 starting point
     * @return ending point
     */
    private String getOtherEndpoint(Trail trail, String endpoint1) {
    	// Return the endpoint that endpoint1 is not equal to
        if (trail.getLandmarkOne().equals(endpoint1))
            return trail.getLandmarkTwo();
        else 
            return trail.getLandmarkOne();

    }
    
    /**
     * Returns the Landmark with the ID landmarkID
     * @param landmarkID Landmark ID to search for
     * @return Landmark with the ID landmarkID
     */
	public Landmark getLandmarkByID(String landmarkID) {
        Landmark landmark = idMap.get(landmarkID);
        
    	return landmark;
    }
    
    /**
     * Returns the maximum number of intersections at any Landmark
	 * @return the maxIntersections
	 */
	public int getMaxIntersections() {
		return maxIntersections;
	}

	/**
	 * Sets the maximum number of intersections
	 * @param maxIntersections the maxIntersections to set
	 */
	private void setMaxIntersections(int maxIntersections) {
		this.maxIntersections = maxIntersections;
	}

	/**
	 * Gets the map of Landmarks to List of Trails
	 * @return the ltMap
	 */
	public Map<Landmark, List<Trail>> getLtMap() {
		return ltMap;
	}
}