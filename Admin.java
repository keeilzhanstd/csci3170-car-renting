package com.company;

import java.sql.*;
import java.util.Scanner;

public class Admin {
    final Connection conn;
    final Scanner in;

    int input;

    public Admin(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void start() {

        while(true) {
            System.out.println(Utils.adminMenuMessage);
            while(true) {
                input = Utils.promptInt(this.in);
                if (1 <= input && input <= 5) {
                    break;
                } else {
                    System.out.println("[ERROR]: Invalid input.");
                }
            }

            if (input == 1) {
                try {
                    createTable();
                    System.out.println("[Message]: Tables created");
                } catch (SQLException e) {
                    System.out.println("[ERROR]: Tables already exist");
                    System.exit(1);
                }
            } else if (input == 2) {
                try {
                    deleteTable();
                    System.out.println("\n[Message]: Tables deleted");
                } catch (SQLException e) {
                    System.out.println("[ERROR]: " + e);
                    System.exit(1);
                }
            } else if (input == 3) {
                try {
                    loadData();
                    System.out.println("\n[Message]: Data loaded");
                } catch (SQLException e){
                    System.out.println("[ERROR]: " + e);
                    System.exit(1);
                }
            } else if (input == 4) {
                try {
                    showNum();
                } catch(SQLException e) {
                    System.out.println("[ERROR]: Cannot get size of tables. Check if tables exist.");
                    System.exit(1);
                }
            } else {
                break;
            }
        }
    }


    private void createTable() throws SQLException{
            //Create USER_CATEGORY
            String sql = "CREATE TABLE USER_CATEGORY(ucid integer(1) primary key, max integer(1) NOT NULL, period integer(2) NOT NULL)";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            //Create USER
            sql = "CREATE TABLE USER(uid varchar(12) primary key, name varchar(25) NOT NULL, age integer(2) NOT NULL, occupation varchar(20) NOT NULL, ucid integer(1) NOT NULL, FOREIGN KEY (ucid) REFERENCES USER_CATEGORY (ucid))";
            stmt.executeUpdate(sql);
            //Create CAR_CATEGORY
            sql = "CREATE TABLE CAR_CATEGORY(ccid integer(1) primary key, ccname varchar(20) NOT NULL)";
            stmt.executeUpdate(sql);
            //Create CAR
            sql = "CREATE TABLE CAR(callnum varchar(8) primary key, name varchar(10) NOT NULL, manufacture varchar(10) NOT NULL, time_rent integer(2) NOT NULL, ccid integer(1) NOT NULL, FOREIGN KEY (ucid) REFERENCES USER_CATEGORY (ucid))";
            stmt.executeUpdate(sql);
            //Create COPY
            sql = "CREATE TABLE COPY(callnum varchar(8), copynum integer(1) NOT NULL, PRIMARY KEY (callnum, copynum), FOREIGN KEY (ucid) REFERENCES USER_CATEGORY (ucid))";
            stmt.executeUpdate(sql);
            //Create RENT
            sql = "CREATE TABLE RENT(callnum varchar(8) NOT NULL, copynum integer(1) NOT NULL, uid varchar(12) NOT NULL, checkout varchar(10), return_date varchar(10), PRIMARY KEY (uid, callnum, copynum, checkout), FOREIGN KEY (callnum, copynum) REFERENCES COPY (callnum, copynum), FOREIGN KEY (uid) REFERENCES USER (uid))";
            stmt.executeUpdate(sql);
            //Create PRODUCE
            sql = "CREATE TABLE PRODUCE(cname varchar(25) NOT NULL, callnum varchar(8) NOT NULL, PRIMARY KEY (cname, callnum), FOREIGN KEY (callnum) REFERENCES CAR (callnum))";
            stmt.executeUpdate(sql);
            // TEMP table for load data to COPY table and compute available copy of car in search car operation
            sql = "CREATE TABLE TEMP(callnum varchar(8) NOT NULL, numOfCopy int(1) NOT NULL);";
            stmt.execute(sql);
    }

    private void deleteTable() throws SQLException{
        String sql = "DROP TABLE IF EXISTS RENT, COPY, PRODUCE, TEMP, USER, USER_CATEGORY, CAR, CAR_CATEGORY";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
    }

    private void loadData() throws SQLException{

        System.out.println("Enter the path to folder");
        String path = in.next();

        Statement stmt = conn.createStatement();
        String sql = "LOAD DATA LOCAL INFILE '" + path + "/user_category.txt' INTO TABLE USER_CATEGORY;";
        stmt.execute(sql);
        sql = "LOAD DATA LOCAL INFILE '" + path + "/user.txt' INTO TABLE USER;";
        stmt.execute(sql);
        sql = "LOAD DATA LOCAL INFILE '" + path + "/car_category.txt' INTO TABLE CAR_CATEGORY;";
        stmt.execute(sql);

        //separate date in car.txt into 3 tables CAR and COPY and PRODUCE
        sql = "LOAD DATA LOCAL INFILE '" + path + "/car.txt' INTO TABLE CAR (callnum, @dummy, name, @dummy, manufacture, time_rent, ccid);";
        stmt.execute(sql);
        sql = "LOAD DATA LOCAL INFILE '" + path + "/car.txt' INTO TABLE PRODUCE (callnum, @dummy, @dummy, cname, @dummy, @dummy, @dummy);";
        stmt.execute(sql);

        // for number of copy of each call number,
        // insert corresponding number of record of that callnum ( 1 to number of copies) in COPY
        // First load data to TEMP table storing callnum and numOfCopy
        sql = "LOAD DATA LOCAL INFILE '" + path + "/car.txt' INTO TABLE TEMP (callnum, numOfCopy, @dummy, @dummy, @dummy, @dummy, @dummy);";
        stmt.execute(sql);
        sql = "SELECT * FROM TEMP";
        ResultSet resultSet = stmt.executeQuery(sql);

        String callnum;
        int numOfCopy;
        // for each callnum, insert corresponding record to table COPY
        if(!resultSet.isBeforeFirst())
            System.out.println("\n[Error]");
        else
            while(resultSet.next()){
                callnum = resultSet.getString(1);
                numOfCopy = resultSet.getInt(2);
                for (int i = 1; i <= numOfCopy; i++) {
                    sql = "INSERT INTO COPY VALUES (?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, callnum);
                    pstmt.setInt(2, i);
                    pstmt.executeUpdate();
                }
            }

        sql = "LOAD DATA LOCAL INFILE '" + path + "/rent.txt' INTO TABLE RENT;";
        stmt.execute(sql);
    }

    private void showNum() throws SQLException{
        String[] table = {"USER_CATEGORY", "USER", "CAR_CATEGORY", "CAR", "COPY", "RENT", "PRODUCE"};
        String sqlTemplate = "SELECT COUNT(*) FROM <table>";
        for (int i = 0; i < 7; i++) {
            // prepared statement
            String sql = sqlTemplate.replace("<table>", table[i]);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            // print result if result is not empty
            if(!resultSet.isBeforeFirst())
                System.out.println("\n[Message]: No records found");
            else
                while(resultSet.next()) System.out.println(table[i] + ": " + resultSet.getInt(1));
        }
    }
}