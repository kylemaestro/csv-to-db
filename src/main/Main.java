package main;

public class Main {
	public static void main(String args[]) {
		
		Reader.init("data.csv");
		
		DatabaseParser parser = new DatabaseParser();
		parser.createNewDatabase("records");
	}
}