package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseParser {
	
	String[] headers;
	String url;
	String dataFileName;
	
	// Sets the database URL using specified filename
	public void setURL(String fileName) {
		
		dataFileName = fileName;
		
		String localDir = System.getProperty("user.dir");
		
		// Creates database and logs directories for later use
		url = localDir + "//databases";
		File file = new File(url);
		file.mkdir();
		
		url = localDir + "//logs";
		file = new File(url);
		file.mkdir();
		
		// Creates path to save new database to our "/databases" folder
		url = "jdbc:sqlite:" + localDir + "//databases//" + fileName + ".db";
	}
	
	// Helper method to connect to our database with our saved url
	private Connection connect() {
		try {
			Connection connection = DriverManager.getConnection(url);
			return connection;
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	// Creates a new database in the "databases" folder
	public void createNewDatabaseWithTable() {
		
		// Obtains the column headers from 1st line of csv file
		headers = Reader.readNextEntry();
		
		// Per the specifications, table will have exactly 10 columns
		if(headers.length != 10) {
			System.out.println("ERROR: CSV file does not have 10 columns!");
			return;
		}
		
		// Prepares sql statement to create columns with names specified in
		// csv file column header names. 
		String sql = "CREATE TABLE IF NOT EXISTS people (\n"
				+ "    "+headers[0]+" text,\n"
				+ "    "+headers[1]+" text,\n"
				+ "    "+headers[2]+" text PRIMARY KEY,\n"
				+ "    "+headers[3]+" text,\n"
				+ "    "+headers[4]+" BLOB,\n" // should be BLOB
				+ "    "+headers[5]+" text,\n"
				+ "    "+headers[6]+" real,\n"
				+ "    "+headers[7]+" text,\n"
				+ "    "+headers[8]+" text,\n"
				+ "    "+headers[9]+" text\n"
				+ ");";
		
		// Attempts to create a new SQLite database
		try(Connection connection = this.connect()) {
			if(connection != null) {
				
				System.out.println("Database has been created @ "+ url);
				
				// Create our default table with header names obtained from file
				Statement stmt = connection.createStatement();
				stmt.execute(sql);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Iterates through each csv row, adding them to the database
	public void insertAll() {
		
		boolean hasNext = true;
		int received = 0, successes = 0, failures = 0; // for tracking operation statistics
		int currentRecord = 0;
		
		// Keeps track of bad data arrays to handle later
		ArrayList<ArrayList<String>> badData = new ArrayList<ArrayList<String>>();
		
		do {
			// Read in a row of data as a String[], and convert to arraylist to facilitate
			// data normalization before insertion
			String[] row = Reader.readNextEntry();
			
			currentRecord++;
			
			// Check for null input, i.e. EOF
			if(row == null) {
				hasNext = false;
				continue;
			}
			
			received += 1;
			
			ArrayList<String> data = new ArrayList<String>();
			Collections.addAll(data, row);
			
			// As only 1 table is specified, ignore any erroneous extra headers in our
			// data and log the incident
			if(Arrays.equals(row, headers)) {
				System.out.println("[WARNING] Extra header row found, ignoring...");
				failures += 1;
				badData.add(data); // add record to our badData list to handle later
				continue;
			}
			
			validateImageEntries(data);
			
			// Checks if data entries match # of columns
			if(isRightSize(data, 10)) {
				successes += 1;
				insert(data); // perform SQL insert operation with data record
				System.out.println("[INSERT] Successfully inserted record "+currentRecord);
			} else {
				failures += 1;
				badData.add(data); // add record to our badData list to handle later
			}
			
		} while(hasNext);
		
		printStats(received, successes, failures);
		
		handleBadData(badData);
		
		// Close our csv-reader when we're done with it
		try {
			Reader.csvReader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Checks row data for image data, which will contain an erroneous comma
	// If found, we concatenate the image header and data into one entry to ensure
	// the number of data entries matches the schema
	public void validateImageEntries(ArrayList<String> data) {
		
		// Check for "data:image" entries, which will have an erroneous comma we must ignore
		for(int i=0; i<data.size(); i++) {
			if(data.get(i).contains("data:image")) {
				// Concatenate image data header with image data into single entry
				data.set(i, (data.get(i) + "," + data.get(i+1)));
				data.remove(i+1);
				//System.out.println("Entries concatenated: "+data.get(i));
			}
		}
		
	}
	
	// Simply checks if the number of entries for a row matches the number of columns,
	// returning true or false depending
	public boolean isRightSize(ArrayList<String> data, int numOfCols) {
		if(data.size() == numOfCols) {
			//System.out.println("Number of data entries for row matches schema");
			return true;
		} else {
			System.out.println("[WARNING] Number of data entries for row is "+data.size()
			+ ", expected "+numOfCols+"");
			return false;
		}
	}
	
	// Log bad records to <input-filename>-bad.csv
	public void handleBadData(ArrayList<ArrayList<String>> data) {
		// Set up output stream to redirect to file for logging
		String localDir = System.getProperty("user.dir");
		String outputUrl = localDir + "//logs//" + dataFileName + "-bad.csv";
		try {
			// Open new PrintStream to a log file @ output URL
			PrintStream filePrint = new PrintStream(new File(outputUrl));
			
			// Maintain old console stream to return to later
			PrintStream console = System.out;
			
			System.setOut(filePrint);
			
			// Iterates through list of bad records, taking each record and adding
			// previously eliminated commas in order to log in CSV format
			for(int x=0; x<data.size(); x++) {
				
				String recordString = "";
				
				ArrayList<String> record = data.get(x); // gets next bad record from the list
				
				// Rebuild the record in csv form, adding eliminated commas
				for(int i=0; i<record.size(); i++) {
					
					// Add a comma after all record entries but the last
					if((i+1) != record.size()) {
						recordString = recordString + record.get(i) + ",";
					} else {
						recordString = recordString + record.get(i);
					}
					
				}
				
				// Prints the bad record to file
				System.out.println(recordString);
			}
			
			System.setOut(console); // redirect output to console
			System.out.println("[INFO] Bad records written to "+outputUrl);
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Print the stats of successful/failed insertions to the console and log file
	public void printStats(int received, int successes, int failures) {
		System.out.println("[STATS] Number of records received: "+received);
		System.out.println("[STATS] Number of successful insertions: "+successes);
		System.out.println("[STATS] Number of failed insertions: "+failures);
		
		// Set up output stream to redirect to file for logging
		String localDir = System.getProperty("user.dir");
		String outputUrl = localDir + "//logs//" + dataFileName + ".log";
		try {
			// Open new PrintStream to a log file @ output URL
			PrintStream filePrint = new PrintStream(new File(outputUrl));
			
			// Maintain old console stream to return to later
			PrintStream console = System.out;
			
			System.setOut(filePrint);
			
			// Print stats to <file-name>.log in "logs" folder
			System.out.println("[STATS] Number of records received: "+received);
			System.out.println("[STATS] Number of successful insertions: "+successes);
			System.out.println("[STATS] Number of failed insertions: "+failures);
			
			System.setOut(console); // redirect output to console again
			System.out.println("[INFO] Insert operation statistics written to "+outputUrl);
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public void insert(ArrayList<String> data) {
		
		// Prepare sql insert statement to populate our table
		String sql = "INSERT INTO people("+headers[0]+","
										  +headers[1]+","
										  +headers[2]+","
										  +headers[3]+","
										  +headers[4]+","
										  +headers[5]+","
										  +headers[6]+","
										  +headers[7]+","
										  +headers[8]+","
										  +headers[9]+") "
										  +"VALUES(?,?,?,?,?,?,?,?,?,?)";
		
		// Connect to our database and attempt the insert operation
		try(Connection connection = this.connect();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			// arg1 represents index (starting @ 1) for columns,
			// arg2 is the data we're inserting at that column for the row
			pstmt.setString(1, data.get(0)); // first
			pstmt.setString(2, data.get(1)); // last
			pstmt.setString(3, data.get(2)); // email
			pstmt.setString(4, data.get(3)); // gender
			pstmt.setBytes(5, data.get(4).getBytes()); // image data
			pstmt.setString(6, data.get(5)); // provider
			pstmt.setDouble(7, Double.valueOf(data.get(6).substring(1))); // double expected, trim '$'
			pstmt.setString(8, data.get(7)); // t/f
			pstmt.setString(9, data.get(8)); // t/f
			pstmt.setString(10, data.get(9)); // location
			pstmt.executeUpdate(); // executes SQL statement
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}

//InputStream img = new ByteArrayInputStream(data[4].getBytes());
//pstmt.setBinaryStream(5, img);
