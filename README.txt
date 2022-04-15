Group 30
1155126601 Lei Ka Hong

Project: Car renting system using JDBC



Compilation and system deployment:

- The java program is compiled and run on Linux system and Mysql database system



- Use scp to transfer the java source file, mysql-connector-java.jar and other file needed (.txt file for dataset) to remote Linux system:

	$ scp path_to_file username@linux1.cse.cuhk.edu.hk:/path_to_target_directory
	
Note:	The java source file in my submission is named Main.java and the java class is named Main which is inside the package folder com/company/. To ensure the Main class can be found when running the program. The Main.java should be inside a directory '/directory_you_want_to_run_program/com/company/Main.java' in the remote Linux system.

	Other file like mysql-connector-java.jar and .txt files should be transferred to the directory you want to run the program



- Use ssh to connect to the CSE Linux server:
	
	$ ssh username@linux1.cse.cuhk.edu.hk



- Compile .java file to .class on Linux:

	$ javac directory_you_want_to_run_program/com/company/Main.java



- Change directory to the directory you want to run the program:

	$ cd directory_you_want_to_run_program/



- Run program:

	$ java -cp .:mysql-connector-java.jar com.company.Main

Note: To use the command above, it is assumed that all the files are in corresponding directory mentioned in the scp file transfer step. Otherwise the path to the mysql connector or Main need to be specified and the program may not be able to load data from .txt file properly in the program later.



- Use the car renting system according to message shown on command prompt.




Verify or examine database using Mysql:

- Connect to Mysql after connecting Linux server

	$ mysql --host=projgw --port=2633 -u Group30 –p
	$ use db30



