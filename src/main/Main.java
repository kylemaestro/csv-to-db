package main;

public class Main {
	public static void main(String args[]) {
		
		Reader.init("data.csv");
		
		DatabaseParser parser = new DatabaseParser();
		
		if(args.length != 1) {
			System.out.println("[ERROR] Usage: 'java -jar csv2db.jar [CSV FILE NAME]'");
			System.out.println(" -> NOTE: Do not include the file extension. Example for");
			System.out.println(" -> default CSV file 'records.csv':");
			System.out.println(" -> java -jar csv2db.jar records");
			System.exit(1);
		}
		
		parser.setURL(args[0]);
		parser.createNewDatabaseWithTable();
		parser.insertAll();
		
		System.exit(0);
	}
}