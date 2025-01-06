# Database editor project
Project of a database editor window app.  
Program allows working with databases containing data of simple types (like String, int) which list can be further extended.  
Program at this moment works only with mySQL databases however list of compatible can also be further extended.

#### How to run it?
In order to run the app double-click DBManager jar file.  
*Note that config file must be present in the same folder as DBManager jar file!*


#### Program features:  
1. Opening any database of compatible environment
2. Presenting content of tables of specified database with use of JTable
3. Dropdown menu including options like:
     - Changing used database
     - Selecting any table of used database
     - Editing chosen table (adding/deleting rows of tables in database)
     - Displaying the table in ascending/descending order of the selected column
     - SQL console in which user can write SQL commands and see their results (in case of SELECT commands)
4. Config file specifing database autologin



#### What was used in this project?
1. JDBC with mySQL driver for database communication
2. Swing library for GUI
