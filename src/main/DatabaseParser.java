package main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseParser {
	
	// Creates a new database in the "databases" folder with specified name
	public void createNewDatabase(String fileName) {
		
		// Creates path to save new database to our "/databases" folder
		String localDir = System.getProperty("user.dir");
		String url = "jdbc:sqlite:" + localDir + "//databases//" + fileName + ".db";
		
		// Obtains the column headers from csv file
		String[] headers = Reader.readColHeader();
		
		String sql = "CREATE TABLE IF NOT EXISTS people (\n"
				+ "    "+headers[0]+" VARCHAR(255),\n"
				+ "    "+headers[1]+" VARCHAR(255),\n"
				+ "    "+headers[2]+" VARCHAR(255) PRIMARY KEY,\n"
				+ "    "+headers[3]+" VARCHAR(255),\n"
				+ "    "+headers[4]+" IMAGE,\n"
				+ "    "+headers[5]+" VARCHAR(255),\n"
				+ "    "+headers[6]+" integer,\n"
				+ "    "+headers[7]+" VARCHAR(255),\n"
				+ "    "+headers[8]+" VARCHAR(255),\n"
				+ "    "+headers[9]+" VARCHAR(255)\n"
				+ ");";
		
		// Attempts to create a new SQLite database
		try(Connection connection = DriverManager.getConnection(url)) {
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

}
