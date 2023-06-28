package edu.ncsu.csc316.trail.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for TrailManager Class
 * @author Ben Morris
 */
public class TrailManagerTest {
	/** ReportManager */
	private ReportManager rm;
	/** TrailManager */
	private TrailManager tm;
	/** Landmark file path */
	private static final String LANDMARK_PATH = "input/landmarks_sample.csv";
	/** Trails file path */
	private static final String TRAILS_PATH = "input/trails_sample.csv";	
	
	/**
	 * Setup for tests. Creates a new ReportManager and a new TrailManager
	 * @throws FileNotFoundException if either file isn't found
	 */
	@Before
	public void setup() throws FileNotFoundException {
		try {
			rm = new ReportManager(LANDMARK_PATH, TRAILS_PATH);
			tm = new TrailManager(LANDMARK_PATH, TRAILS_PATH);			
		} catch (Exception e) {
			throw new FileNotFoundException();
		}		
	}
	
	/**
	 * Test for getPathToLandmarFile()
	 */
	@Test
	public void testGetPathtolandmarkfile() {
		assertNotNull(rm);
		tm.setPathtolandmarkfile(LANDMARK_PATH);
		assertEquals(tm.getPathtolandmarkfile(), LANDMARK_PATH);
	}
	
	/**
	 * Test for getPathToTrailsFile()
	 */
	@Test
	public void testGetPathtotrailsfile() {
		tm.setPathtotrailsfile(TRAILS_PATH);
		assertEquals(tm.getPathtotrailsfile(), TRAILS_PATH);
	}
	
	/**
	 * Test for getProposedFirstAidLocations()
	 */
	@Test
	public void testGetProposedFirstAidLocations() {		
		assertEquals(tm.getLtMap(), tm.getProposedFirstAidLocations(10));
	}
	
	/**
	 * Test for getDistancesToDestinations()
	 */
	@Test
	public void testGetDistancesToDestinations() {
		assertEquals(tm.getDistancesToDestinations("L11").size(), 1);
		assertEquals(tm.getDistancesToDestinations("L04").size(), 9);
	}
	
	/**
	 * Test for maxIntersections()
	 */
	@Test
	public void testMaxIntersections() {
		assertEquals(tm.getMaxIntersections(), 3);		
	}
	
	/**
	 * Test for getLandmarkByID()
	 */
	@Test
	public void testGetLandmarkById() {
		// Test a valid landmark
		assertEquals(tm.getLandmarkByID("L01").getId(), "L01");

		// Test a nonexistent ID
		assertNull(tm.getLandmarkByID("L20"));
	}
}
