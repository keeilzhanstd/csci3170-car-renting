package com.company;
import java.util.Scanner;
import java.sql.*;


//Used to establish connection with mysql, and begin the app functioning.
public class Main {

    public static int promptInt(Scanner in) {
        int choice;
        try {
            choice = in.nextInt();
            return choice;
        } catch(Exception e) {
            System.out.println("[Error]: Input is not a valid integer.");
            System.exit(-1);
        }
        return -1;
    }

    public static String promptLine(Scanner in) {
        try {
            return in.nextLine();
        } catch(Exception e) {
            System.out.println("[Error]: Input is not a valid string.");
            System.exit(-1);
        }
        return null;
    }

    public static void main(String[] args) {
        // Firstly, need to connect to the database
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group30";
        String dbUsername = "Group30";
        String dbPassword = "3170group30";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        } catch(ClassNotFoundException e) {
            System.out.println("[ERROR]: Java MySql DB Driver not found!!");
            System.exit(0);
        } catch(SQLException e) {
            System.out.println("[ERROR]: " + e);
        }
        System.out.println("Connected!");

        System.out.println("Starting of the program");
        Role app = new Role(con);
        app.run();
    }
}
