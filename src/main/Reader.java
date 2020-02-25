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
	
	// Reads one line from csv file at a time. First line will represent the header names,
	// with the rest representing single row entries for our table
	public static String[] readNextEntry() {
		try {
			String row = csvReader.readLine();
			// Stop when we reach the end of the file
			if(row == null) {
				return null;
			} else {
				String[] data = row.split(",");
				return data;
			}
		} catch (IOException e) {
			System.out.println("[ERROR] CSV file reader has not been initialized yet!");
			return null;
		}
	}
	
}
