package main;

public class Main {
	public static void main(String args[]) {
		
		Reader.init("data.csv");
		
		DatabaseParser parser = new DatabaseParser();
		parser.setURL("records");
		parser.createNewDatabaseWithTable();
		parser.insertAll();
	}
}