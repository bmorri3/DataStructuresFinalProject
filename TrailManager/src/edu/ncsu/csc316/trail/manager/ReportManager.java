package edu.ncsu.csc316.trail.manager;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Iterator;

import edu.ncsu.csc316.dsa.list.List;
import edu.ncsu.csc316.dsa.map.Map;
import edu.ncsu.csc316.dsa.sorter.Sorter;
import edu.ncsu.csc316.trail.data.Landmark;
import edu.ncsu.csc316.trail.data.Trail;
import edu.ncsu.csc316.trail.dsa.Algorithm;
import edu.ncsu.csc316.trail.dsa.DSAFactory;
import edu.ncsu.csc316.trail.dsa.DataStructure;

/**
 * Prints reports for distances to all landmarks from an origin as well as proposed
 * first aid stations based on the minimum number of intersecting trails at a Landmark.
 * @author Ben Morris
 */
public class ReportManager {
	/** TrailManager */
	private static TrailManager tm;
	
	/**
	 * Constructor
	 * @param pathToLandmarkFile path to file of Landmarks
	 * @param pathToTrailsFile path to file of Trails
	 * @throws FileNotFoundException if one of the files isn't found
	 */
    public ReportManager(String pathToLandmarkFile, String pathToTrailsFile) throws FileNotFoundException {        
    	// Set Map type for DSAFactory
    	DSAFactory.setMapType(DataStructure.SKIPLIST);
    	// Set List type for DSAFactory
    	DSAFactory.setListType(DataStructure.SINGLYLINKEDLIST);
    	// Set Comparison Sorter type for DSAFactory
    	DSAFactory.setComparisonSorterType(Algorithm.MERGESORT);
    	// Set Non-Comparison Sorter type for DSAFactory
    	DSAFactory.setNonComparisonSorterType(Algorithm.RADIX_SORT);
    		    
	    // Create a new TrailManager    	
    	try {
			tm = new TrailManager(pathToLandmarkFile, pathToTrailsFile);
		} catch (Exception e) {
			throw new FileNotFoundException("File not found.");
		}
    }
    
    /**
     * Creates the Proposed First Aid Locations report
     * @param minTrails minimum number of trails intersecting a Landmark for the Landmark
     * 		  to be considered for a First Aid location
     * @return The report as a String
     * @throws FileNotFoundException if the file cannot be found
     */
    public String getProposedFirstAidLocations(int minTrails) 
		   throws FileNotFoundException {		    
    	// Report to return
		StringBuilder stationReport = new StringBuilder();
		
		// Handling choice of zero or fewer trail intersections for direct
		// method call in Testing
		if (minTrails < 1) {
			stationReport.setLength(0);
			stationReport.append("Number of intersecting trails must be greater than 0.");
			return stationReport.toString();
		}
				
		// Map to store Landmark->List<Trail>>
		Map<Landmark, List<Trail>> ltMap = tm.getProposedFirstAidLocations(minTrails);				
			    
	    // Track the number of acceptable locations
	    int num = 0;
	    int i = 0;
	    
	    // Create a new list to store Landmarks and the number of intersections
	    IntersectionListEntry[] intersectionArray = new IntersectionListEntry[ltMap.size()];
	    
	    // Create an iterator
	    Iterator<Map.Entry<Landmark, List<Trail>>> it = ltMap.entrySet().iterator();
	    // Iterate through the list
	    while(it.hasNext()) {
	    	// Get the map entry
	    	Map.Entry<Landmark, List<Trail>> mapEntry = (Map.Entry<Landmark, List<Trail>>) it.next();
	    	
	    	// Create an entry of the Landmark and its number of intersecting trails 
	    	IntersectionListEntry entry = new IntersectionListEntry(mapEntry.getKey(), mapEntry.getValue().size());
	    	// Add entry to the Array to sort later
	    	intersectionArray[i] = entry;
	    	i++;
	    	
	    	// Keep track of how many Landmarks met the intersection threshold
	    	if (mapEntry.getValue().size() >= minTrails) {	    		
	    		num++;
	    	}
	    }	    	    
	    
		// If there were no acceptable locations
		if (num == 0) {
			stationReport.setLength(0);
			stationReport.append("No landmarks have at least ").append(minTrails);
			stationReport.append(" intersecting trails.");
			return stationReport.toString();
		}
	    
	    // Sort intersectionArray. This will sort by descending number of intersections, then ID
    	Sorter<IntersectionListEntry> sorter = DSAFactory.getComparisonSorter(new IntersectionListEntryComparator()); 
    	sorter.sort(intersectionArray);

    	// Create the report
    	// Report header
    	stationReport.append("Proposed Locations for First Aid Stations {\n");
	    
    	// For each of the elements, create a line for the report if the entry meets the requirement
    	for (int j = 0; j < intersectionArray.length; j++) {
    		if( intersectionArray[j].getNumIntersections() >= minTrails) {
	    		stationReport.append("   ").append(intersectionArray[j].getDescription());
	   			stationReport.append(" (").append(intersectionArray[j].getStringID()).append(") - ");
	   			stationReport.append(intersectionArray[j].getNumIntersections()).append(" intersecting trails\n");
    		}
    	}	    	

		// Complete the report
		stationReport.append("}\n");		
			
		// Return the report
		return stationReport.toString();
	}

    /**
     * Creates the report of distances to all reachable landmarks from originLandmark
     * @param originLandmark Landmark to begin from
     * @return report of distances to all reachable landmarks from originLandmark
     */
	public String getDistancesReport(String originLandmark) {
    	
		// Get the Landmark with originLandmark as its ID
		Landmark origin = tm.getLandmarkByID(originLandmark); 
		        	
		// If the Landmark doesn't exist...
		if (origin == null)
    		return "The provided landmark ID (" + originLandmark + ") is invalid for the park.";
		
		// Create a Map of Landmark->distances
		// This will store the Landmarks reachable from origin and the distances to each
		Map<Landmark, Integer> distanceMap = tm.getDistancesToDestinations(originLandmark);
		
		// If there are no Landmarks reachable from the origin...
		if (distanceMap.isEmpty())
			return "No landmarks are reachable from " + origin.getDescription() +
			" (" + origin.getId() + ").";		      
    	
		// Initialize array index to zero
		int index = 0;
		
    	// Create an Array of ReverseDistanceListEntry to sort		
		DistanceListEntry[] reverseArray = new DistanceListEntry[distanceMap.size()];
		
		// Create an iterator
	    Iterator<Map.Entry<Landmark, Integer>> it = distanceMap.entrySet().iterator();
	    // Iterate through the list
	    while(it.hasNext()) {
	    	// Get the map entry
	    	Map.Entry<Landmark, Integer> mapEntry = (Map.Entry<Landmark, Integer>) it.next();	    	
	    	// Create an entry of the Landmark and its number of intersecting trails 
	    	DistanceListEntry entry = new DistanceListEntry(mapEntry.getKey(), mapEntry.getValue());
	    	// Add entry to the Array to sort later
	    	reverseArray[index] = entry;
	    	index++;
	    }
    	
    	// Sort reverseArray. This will sort by distance first, then ID
    	Sorter<DistanceListEntry> sorter = DSAFactory.getComparisonSorter(new ReverseDistanceListEntryComparator()); 
    	sorter.sort(reverseArray);
    	
    	// Create the report
    	StringBuilder stationReport = new StringBuilder();
    	stationReport.append("Landmarks Reachable from ").append(origin.getDescription());
    	stationReport.append(" (").append(origin.getId()).append(") {\n");
    	// Add a line to the report for each entry in distanceMap
    	for(int i = 0; i < distanceMap.size(); i++) {
    		DistanceListEntry entry = reverseArray[i];
    		stationReport.append("   ").append(entry.distance).append(" feet ");
	    	 
    		// If the distance is a mile or more, convert and show miles as well
	    	if (entry.distance >= 5280.) {
	    		double miles = entry.distance / 5280.;
	    		String milesString = String.format("%.2f", miles);
	    		stationReport.append("(").append(milesString).append(" miles) ");
	    	}

	    	// Add the description and ID
	    	stationReport.append("to ").append(entry.landmark.getDescription());
	    	stationReport.append(" (").append(entry.landmark.getId()).append(")\n");  
    	}
    
    	// Conclude the report
	    stationReport.append("}\n");
	    // Return the report
    	return stationReport.toString();
    }
    
    /**
     * Class to compare by number of Trail intersections, then key
     * @author Ben Morris
     */
    static class DescriptionComparator implements Comparator<Landmark> {
    	
    	@Override
        public int compare(Landmark entry1, Landmark entry2)
        {
    		// Compare the Landmarks' descriptions
        	if (entry1.getDescription().compareTo(entry2.getDescription()) > 0)
        		return 1;
        	if (entry1.getDescription().compareTo(entry2.getDescription()) < 0)
        		return -1;
        	else
        		return 0;
        }	
    }
    
    /**
	 * Class to compare by String
	 * @author Ben Morris
	 */
	static class IDComparator implements Comparator<String> {
	    
		@Override
		public int compare(String id1, String id2)
	    {
			// Compare the Strings
	    	if (id1.compareTo(id2) > 0)
	    		return 1;
	    	if (id1.compareTo(id2) < 0)
	    		return -1;
	    	else
	    		return 0;
	    }	
	}

	/**
	 * Entry for ReverseDistanceList
	 * ReverseDistanceListEntry has a Landmark and a distance to it
	 * @author Ben Morris
	 */
	static class DistanceListEntry implements Comparable<DistanceListEntry> {
    	/** Field for LandmarkID */
    	private Landmark landmark;
    	/** Field for distance */
    	private Integer distance;
    	
    	/**
    	 * Constructor
    	 * @param landmark Landmark reachable from the origin
    	 * @param distance Distance to the origin
    	 */
    	public DistanceListEntry(Landmark landmark, Integer distance) {
    		this.landmark = landmark;
    		this.distance = distance;    		
    	}
    	
    	/**
    	 * Default constructor
    	 */
    	public DistanceListEntry() {
    		this.landmark = null;
    		this.distance = 0;    		
    	}
    	
    	@Override
    	public int compareTo(DistanceListEntry entry) {
    		// Sorts by distance first, then Landmark descriptions
    		if (this.distance.compareTo(entry.distance) > 0)
    			return 1;
    		else if (this.distance.compareTo(entry.distance) < 0)
    			return -1;
    		else
    			if (this.landmark.getDescription().compareTo(entry.landmark.getDescription()) > 0 )
    				return 1;
    			else if (this.landmark.getDescription().compareTo(entry.landmark.getDescription()) < 0 )
    				return -1;
    		
    		return 0;    		
    	}
    	
    	/**
    	 * Returns landmark
    	 * @return landmark
    	 */
    	public Landmark getLandmark() {
    		return landmark;
    	}
    	
    	/**
    	 * Returns distance
    	 * @return distance
    	 */
    	public int getDistance() {
    		return distance;
    	}
    }
    
	/**
	 * Comparator for ReverseDistanceListEntries
	 * @author Ben Morris
	 */
    static class ReverseDistanceListEntryComparator implements Comparator<DistanceListEntry> {
        @Override
    	public int compare(DistanceListEntry entry1, DistanceListEntry entry2)
        {
        	return entry1.compareTo(entry2);
        }	
    }
    
    /**
     * Entry for IntersectionList
     * IntersectionListEntry has a Landmark and number of intersections at that Landmark
     * @author Ben Morris
     */
    static class IntersectionListEntry implements Comparable<IntersectionListEntry> {
    	/** Field for LandmarkID */
    	private Landmark landmark;
    	/** Field for distance */
    	private Integer numIntersections;
    	
    	/**
    	 * Constructor
    	 * @param landmark Landmark
    	 * @param number intersections at the Landmark
    	 */
    	public IntersectionListEntry(Landmark landmark, Integer number) {
    		this.landmark = landmark;
    		this.numIntersections = number;    		
    	}    	
    	
    	/**
    	 * Default constructor
    	 */
    	public IntersectionListEntry() {
    		this.landmark = null;
    		this.numIntersections = 0;    		
    	}
    	
    	@Override
    	public int compareTo(IntersectionListEntry entry) {
    		// Sorting by descending number of intersections, then Landmark description
    		if (this.getNumIntersections().compareTo(entry.getNumIntersections()) < 0)
    			return 1;
    		else if (this.getNumIntersections().compareTo(entry.getNumIntersections()) > 0)
    			return -1;
    		else
    			if (this.landmark.getDescription().compareTo(entry.landmark.getDescription()) > 0 )
    				return 1;
    			else if (this.landmark.getDescription().compareTo(entry.landmark.getDescription()) < 0 )
    				return -1;
    		
    		return 0;    		
    	}
    	
    	/**
    	 * Returns landmark
    	 * @return landmark
    	 */
    	public Landmark getLandmark() {
    		return landmark;
    	}
    	
    	/**
    	 * Returns Landmark's ID
    	 * @return Landmark's ID
    	 */
    	public String getStringID() {
    		return landmark.getId();
    	}
    	
    	/**
    	 * Returns Landmark's description
    	 * @return Landmark's description
    	 */
    	public String getDescription() {
    		return landmark.getDescription();
    	}
    	
    	/**
    	 * Returns the number of intersections at the Landmark
    	 * @return number of intersections at the Landmark
    	 */
    	public Integer getNumIntersections() {
    		return numIntersections;
    	}
    }
    
    /**
     * Comparator for IntersectionListEntry
     * @author Ben Morris
     */
    static class IntersectionListEntryComparator implements Comparator<IntersectionListEntry> {
        @Override
    	public int compare(IntersectionListEntry entry1, IntersectionListEntry entry2)
        {
        	return entry1.compareTo(entry2);
        }	
    }
}