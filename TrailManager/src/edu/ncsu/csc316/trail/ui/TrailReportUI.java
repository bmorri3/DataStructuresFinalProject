package edu.ncsu.csc316.trail.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import edu.ncsu.csc316.trail.manager.ReportManager;

/** 
 * Class for user input for TrailManager project
 * @author Ben Morris
 */
public class TrailReportUI {
	
	/** Report of first aid stations is choice number 1 */
	final static int FIRST_AID = 1;
	/** Report of distances to a landmark is choice number 2 */
	final static int DISTANCES = 2;
	/** Quit TrailManager is choice number 3 */
	final static int QUIT = 3;
	/** path to the file of Landmarks */
	private static String pathToLandmarkFile;
	/** path to the file of Trails */
	private static String pathToTrailsFile;
	/** Input */
	private static Scanner in;
	/** ReportManager */
	private static ReportManager rm;
	/** Menu choice */
	private static int choice; 
	
	/**
	 * Runs UI and calls methods to run distance and first aid station reports.
	 * @param args Command line arguments (not used)
	 * @throws FileNotFoundException If the files aren't found.
	 */
	public static void main(String args[]) throws FileNotFoundException {
		in = new Scanner(System.in);
		
		// Get file names
		getFileNames();
	
		// Create the ReportManager
		try {
			rm = new ReportManager(getPathtolandmarkfile(), getPathtotrailsfile());
		} catch (Exception e) {
			throw new FileNotFoundException("File not found.");
		}	
		
		double start = System.currentTimeMillis() / 1000.;
		
		// Show the user menu and collect user choice
		choice = menu();					
		//choice = 2; for testing
		// Run RemportMangager method based on choice
		handleChoice(choice);						
		
		double end = System.currentTimeMillis() / 1000.;
		double duration = end - start; // in milliseconds
		System.out.println("duration: " + duration);
		
		in.close();
		System.out.println("END OF PROGRAM");
	}	
	
	/**
	 * Gets the path to file of Landmarks
	 * @return the pathToLandmarkFile
	 */
	public static String getPathtolandmarkfile() {
		return pathToLandmarkFile;
	}

	/**
	 * Sets the path to file of Landmarks
	 * @param pathtolandmarkfile the pathToLandmarkFile to set
	 */
	public static void setPathtolandmarkfile(String pathtolandmarkfile) {
		pathToLandmarkFile = pathtolandmarkfile;
	}

	/**
	 * Gets the path to file of Trails
	 * @return the pathToTrailFile
	 */
	public static String getPathtotrailsfile() {
		return pathToTrailsFile;
	}

	/**
	 * Sets the path to file of Trails
	 * @param pathtotrailfile the pathToTrailFile to set
	 */
	public static void setPathtotrailsfile(String pathtotrailfile) {
		pathToTrailsFile = pathtotrailfile;
	}
	
	/**
	 * Gets the file names from the user.
	 */
	public static void getFileNames() {
		Scanner landmarkInput = null;
		Scanner trailInput = null;
		
		// Get Landmark file name
		while(landmarkInput == null) {
			System.out.println("Please enter the file path for the file of Landmarks and press enter.");
			//setPathtolandmarkfile("input/landmarks_sample2.csv"); for testing

			pathToLandmarkFile = in.next();
			setPathtolandmarkfile(pathToLandmarkFile);
			try {
				landmarkInput = new Scanner(new File(getPathtolandmarkfile()));
			} catch (FileNotFoundException e) {
				System.out.println("\n" + pathToLandmarkFile + " cannot be found.");
			}
		}
		
		// Get Trail file name
		while(trailInput == null) {
			System.out.println("Please enter the file path for the file of Trails and press enter.");
			// setPathtotrailsfile("input/trails_sample2.csv"); for testing

			pathToTrailsFile = in.next();
			setPathtotrailsfile(pathToTrailsFile);
			try {
				trailInput = new Scanner(new File(getPathtotrailsfile()));
			} catch (FileNotFoundException e) {
				System.out.println("\n" + pathToTrailsFile + " cannot be found.");
			}
		}
	}
		
	/**
	 * User menu. Returns user choice.
	 * 1 for First Aid Station report
	 * 2 for Distance report
	 * 3 for Quit
	 * @return user choice
	 */
	public static int menu() {
		// Initialize choice to a non-option
		choice = 0;

		// Repeat the menu until a valid option is selected
		while(choice < FIRST_AID || choice > QUIT) {
			System.out.println("Please select from one of the following options.\n");
			System.out.println("1. View report of potential first aid stations.");
			System.out.println("2. View distances to all reachable landmarks from a given landmark.");
			System.out.println("3. Close TrailManager.");
			
			choice = in.nextInt();
		}
		return choice;
	}
	
	/**
	 * Handles the user choice.
	 * Choice 1: Calls ReportManager.getProprosedFirstAidLocations()
	 * Choice 2: Calls ReportManager.getDistancesReport()
	 * Choice 3: exits program
	 * @param choice user choice
	 * @throws FileNotFoundException if the file isn't found
	 */
	public static void handleChoice(int choice) throws FileNotFoundException {
		String report = "";
		
		switch (choice) {
		// First aid station report chosen
		case 1:
			// Get the minimum number of intersections
			System.out.println("You have chosen the option to print a report of " + 
							   "poposed landmarks for first aid stations.\n\n");
			System.out.println("What is the minimum number of trails that should " +
							   "intersect at a landmark such that the landmark " +
							   "is a suitable location for a first aid station?");
			int number = 0;
			
			while(number < 1) {
				System.out.println("Number of intersecting trails must be greater than 0.");
				number = in.nextInt();
				//number = 1; for testing
			}					
			
			// Get the report
			report = rm.getProposedFirstAidLocations(number);
			
			// Print the report.
			System.out.println(report);
									
			break;

		case 2:
			System.out.println("You have chosen the option to print a report of " + 
					   "the distances to all landmarks that are reachable " + 
					   "from a given location.\n");
			System.out.println("Enter the Landmark ID of the starting point.");
			
			String origin = in.next();
			//String origin = "L10";  for testing
			
			
			// Get the report.
			report = rm.getDistancesReport(origin);
					
			// Print the report.
			System.out.println(report);			
			
			break;
			
		case 3:
			// Exits program
			System.out.println("case 3: quit"); // Quit
			break;
			
		default:
			System.out.println("\nInvalid chioce.\n"); // Do nothing
			break;
		}
	}

	/**
	 * Returns choice
	 * @return choice
	 */
	public static int getChoice() {
		return choice;
	}

	/**
	 * Sets choice
	 * @param choice int to set to this.choice
	 */
	public static void setChoice(int choice) {
		TrailReportUI.choice = choice;
	}
}
