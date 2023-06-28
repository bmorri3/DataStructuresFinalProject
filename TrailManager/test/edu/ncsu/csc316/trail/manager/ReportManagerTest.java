package edu.ncsu.csc316.trail.manager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ncsu.csc316.trail.data.Landmark;
import edu.ncsu.csc316.trail.manager.ReportManager.IDComparator;
import edu.ncsu.csc316.trail.manager.ReportManager.IntersectionListEntry;
import edu.ncsu.csc316.trail.manager.ReportManager.IntersectionListEntryComparator;
import edu.ncsu.csc316.trail.manager.ReportManager.DistanceListEntry;

/**
 * Tests for ReportManager.java
 * @author Ben Morris
 */
public class ReportManagerTest {
	/** ReportManager */
	private ReportManager rm;
	/** TrailManager */
	private static final String LANDMARK_PATH = "input/landmarks_sample.csv";
	/** Trails file path */
	private static final String TRAILS_PATH = "input/trails_sample.csv";
	/** L01 Distance Report */
	private static final String L01_REPORT = "Landmarks Reachable from Park Entrance (L01) {\n"
			+ "   1046 feet to Waste Station 1 (L03)\n"
			+ "   1179 feet to Entrance Restrooms (L04)\n"
			+ "   3013 feet to Entrance Fountain (L02)\n"
			+ "   3490 feet to Waste Station 2 (L09)\n"
			+ "   5250 feet to Overlook 1 (L05)\n"
			+ "   6289 feet (1.19 miles) to Rock Formation 1 (L06)\n"
			+ "   6626 feet (1.25 miles) to Hidden Gardens (L10)\n"
			+ "   9201 feet (1.74 miles) to Overlook 2 (L07)\n"
			+ "   11092 feet (2.10 miles) to Overlook Restrooms (L08)\n"
			+ "}\n";
	/** L11 Distance Report */
	private static final String L11_REPORT = 
			"Landmarks Reachable from Campsite 1 (L11) {\n"
			+ "   1066 feet to Campsite Restrooms (L12)\n"
			+ "}\n";
	/** L13 Distance Report */
	private static final String L13_REPORT = "No landmarks are reachable from Hidden Campsite (L13).";
	/** XYZ Distance Report */
	private static final String XYZ_REPORT = "The provided landmark ID (XYZ) is invalid for the park.";
	/** No intersections report */
	private static final String NO_INTERSECTIONS = "No landmarks have at least 4 intersecting trails.";
	/** Three intersections report */
	private static final String THREE_INTERSECTIONS = "Proposed Locations for First Aid Stations {\n"
			+ "   Park Entrance (L01) - 3 intersecting trails\n"
			+ "}\n";
		
	/**
	 * Setup for test. Creates a new ReportManager
	 * @throws FileNotFoundException if either file isn't found
	 */
	@BeforeEach
	public void setup() throws FileNotFoundException {
		try {
			rm = new ReportManager(LANDMARK_PATH, TRAILS_PATH);
		} catch (Exception e) {
			throw new FileNotFoundException();
		}
	}

	/**
	 * Tests ReportManager.getProposedFirstAidLocations()
	 * @throws FileNotFoundException if one of the files isn't found
	 */
	@Test
	public void testGetProposedFirstAidLocations() throws FileNotFoundException {
		
		// More intersections than exist
		try {
			assertEquals(rm.getProposedFirstAidLocations(4), NO_INTERSECTIONS);
		} catch (Exception e) {
			assertFalse(e instanceof FileNotFoundException);
		}
		
		// Test 3 intersections
		try {
			assertEquals(rm.getProposedFirstAidLocations(3), THREE_INTERSECTIONS);
		} catch (Exception e) {
			assertFalse(e instanceof FileNotFoundException);
		}
	}

	/**
	 * Tests ReportManager.getDistanceReport()
	 */
	@Test
	public void testGetDistanceReport() {
		// Test an existing Landmark with one trail connecting
		assertEquals(rm.getDistancesReport("L11"), L11_REPORT);
		
		// Test an existing Landmark with multiple trails connecting
		assertEquals(rm.getDistancesReport("L01"), L01_REPORT);
		
		// Test an existing Landmark with no connecting trails
		assertEquals(rm.getDistancesReport("L13"), L13_REPORT);
		
		// Test a Landmark that doesn't exist
		assertEquals(rm.getDistancesReport("XYZ"), XYZ_REPORT);
	}
	
	/**
	 * Tests idComparitor()
	 */
	@Test
	public void testIDComparitor() {
		IDComparator id = new IDComparator();
		assertTrue(id.compare(L11_REPORT, L01_REPORT) < 0);
		assertTrue(id.compare(L01_REPORT, L11_REPORT) > 0);
	}
	
	/**
	 * Tests reverseDistanceListEntry()
	 */
	@Test
	public void testReverseDistanceListEntry() {
		DistanceListEntry entry = new DistanceListEntry();
		assertNull(entry.getLandmark());
		assertEquals(entry.getDistance(), 0);
		Landmark landmark = new Landmark("L100", "Parking Lot", "Parking Lot");
		Integer distance = 1234;
		entry = new DistanceListEntry(landmark, distance);
		assertEquals(entry.getLandmark().getId(), "L100");
		assertEquals(entry.getDistance(), 1234);
		assertEquals(entry.compareTo(entry), 0);
	}
	
	/**
	 * Tests intersectionListEntry()
	 */
	@Test
	public void testIntersectionListEntry() {
		IntersectionListEntry entry = new IntersectionListEntry();
		IntersectionListEntry entry2 = new IntersectionListEntry();
		assertNull(entry.getLandmark());
		assertEquals(entry.getNumIntersections(), 0);
		Landmark landmark = new Landmark("L100", "Parking Lot", "Parking Lot");
		Integer num = 1234;
		entry = new IntersectionListEntry(landmark, num);
		entry2 = new IntersectionListEntry(landmark, num - 1);
		
		assertEquals(entry.getStringID(), "L100");
		assertEquals(entry.getNumIntersections(), 1234);
		assertEquals(entry.compareTo(entry), 0);
		assertEquals(entry.getLandmark(), landmark);
		assertEquals(entry.getDescription(), "Parking Lot");
		assertEquals(entry.compareTo(entry), 0);
		assertTrue(entry.compareTo(entry2) < 0);
		
		IntersectionListEntryComparator compare = new IntersectionListEntryComparator();
		assertTrue(compare.compare(entry, entry2) < 0);
	}

}
