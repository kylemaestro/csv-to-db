package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
	
	static BufferedReader csvReader;
	
	// Initializes the BufferedReader we will use to parse the csv file
	public static void init(String fileName) {
		// Finds the csv data file by name in our "data" folder
		String localDir = System.getProperty("user.dir");
		String url = localDir + "//data//" + fileName;
		
		// Attempts to open the data file
		try {
			csvReader = new BufferedReader(new FileReader(url));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Reads only the first line of a csv data file in order to
	// retrieve the desired column headers
	public static String[] readColHeader() {
		try {
			String firstRow = csvReader.readLine();
			String[] headers = firstRow.split(",");
			return headers;
		} catch (IOException e) {
			System.out.println("ERROR: CSV file reader has not been initialized yet!");
			return null;
		}
	}
	
}
