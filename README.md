# Project: Car renting system using JDBC

## Group 30
- MUKAMBETKALYEV Bekzhan, 1155123739
- Lei Ka Hong, 1155126601
- Siu Fung, 1155110966

## Compilation and system deployment:

- The java program is compiled and run on Linux system and Mysql database system
- Use scp to transfer the java source file, mysql-connector-java.jar and other file needed (.txt file for dataset) to remote Linux system:
```shell
$ scp path_to_file username@linux1.cse.cuhk.edu.hk:/path_to_target_directory
```

Note:
The java source files are *.java. To ensure the Main class can be found when running the program. The *.java files should be inside a directory '/directory_you_want_to_run_program/com/company/*.java' in the remote Linux system.
Other file like mysql-connector-java.jar and .txt files should be transferred to the directory you want to run the program


- Use ssh to connect to the CSE Linux server:
```shell
$ ssh username@linux1.cse.cuhk.edu.hk
```

- Compile all .java files to .class on Linux:
```shell
$ javac directory_you_want_to_run_program/com/company/*.java
```


- Change directory to the directory you want to run the program:
```shell
$ cd directory_you_want_to_run_program/
```

- Run program:
```shell
$ java -cp .:mysql-connector-java.jar com.company.Main
```

Note: To use the command above, it is assumed that all the files are in corresponding directory mentioned in the scp file transfer step. Otherwise the path to the mysql connector or Main need to be specified and the program may not be able to load data from .txt file properly in the program later.

- Use the car renting system according to message shown on command prompt.




Verify or examine database using Mysql:

- Connect to Mysql after connecting Linux server
```shell
$ mysql --host=projgw --port=2633 -u Group30 –p
$ use db30
```


## Compilation and system deployment using makefile:
You can use makefile to easily compile and run program.

Makefile content:
```make
all: main

main:
	javac *.java

run:
	java -cp mysql-connector-java-5.1.47.jar:. Main
```

Note: The above makefile should be located in the folder with all your `*.java` and `mysql-connector-java-5.1.47.jar` files for this method to work.
- To compile the program use
```shell
make
```

- To run the program use
```shell
make run
```