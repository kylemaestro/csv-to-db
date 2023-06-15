# CSV-to-DB

## Purpose
The purpose of this application is to read in a CSV file, parse the data, and insert the records into a SQLite database using 
Java. This task was assigned as part of a job candidacy, and the 'records.csv' file contained within the 'data' folder was
provided by the organization.
This repository will be used to distribute the code and track changes such as new features and bugfixes.

Full source code for the project can be found under src/main. An executable jar file named 'csv2db.jar' can be found as well,
along with a redistributed library file for working with SQLite in Java. This file is packaged in the executable, but was
included if anyone wishes to tinker with my source code. Instructions to use the executable jar file are as follows:

## Setup
To get started, click 'Clone or Download' in the main repository page, and click "Download ZIP". Extract the files to your
desktop, and open a CMD or PowerShell window. Navigate to the unzipped directory you placed on 
your desktop. To start the application from the command line, type:

```
java -jar csv2db.jar [input filename without extension]
```

So, for example, to run the application with the default 'records.csv' file included in the 'data' folder, type the following:

```
java -jar csv2db.jar records
```

You should start to see progress updates as the application parses the data file and begins inserting the records into a SQLite
database. The database file will appear in a folder named 'databases' contained in the root directory.

After the input file has been fully parsed and all insert operations have completed, statistics for the operation will be written
to '<input-filename>.log', and bad records will be written to '<input-filename>-bad.log'. Both of these log files can be found
in the 'logs' folder of the root directory.
  
To easily view the contents of the created database, you may utilize a free tool named 'DB Browser for SQLite', which can be
found here: https://sqlitebrowser.org/dl/

## Assumptions and Design Notes
The approach to this small application was to keep things simple, and delegate tasks to small helper functions and classes where
appropriate. A static Reader class handles all I/O operations, with the ability to read a single line of the input file at
a time to ensure efficient data processing. The main class simply initializes our Reader, points to the correct input file
with supplied a supplied command line argument, and sets in motion the operations carried out in DatabaseParser. The Database-
Parser class is tasked with creating the database, creating a table, directing Reader to supply records, and execute SQL
statements for insertion operations. It has additional methods to handle bad data, log statistics, and normalize records before
insertion. The idea behind these class and method seperations was low coupling and high cohesion, with each class having its own
distinctive and seperate roles.

A few assumptions were made in development of this project. Chiefly, there is an extra column header contained in the provided
'records.csv' file. This was decided to be erroneous, as the project specification made clear that only one table was expected.
The second largest assumption was that the data types for each column of the table would remain static. While this is generally
a wholly incorrect assumption for real-world databases, in the limited scope of this project it made sense to restrict the
data types to the given input. (Note from The Future [2023]: I'm not sure why I thought this assumption was incorrect for the real world. Having worked with enterprise databases for nearly 3+ years now, the data in each column _should_ be the same for all records sharing that column. We would not expect some Customer records in an e-commerce database to have a CustomerName field that sometimes is a number, and sometimes a string. Leaving this here for posterity. I never heard back from this company :)
