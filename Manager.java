package com.company;

import java.sql.*;
import java.util.Scanner;

public class Manager {
    final Connection conn;
    final Scanner in;
    String uid, callnum, start_date, end_date;
    int input, copynum;

    public Manager(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }
    public void start() {
        while(true) {
            System.out.println(Utils.managerMenuMessage);
            while(true) {
                input = Utils.promptInt(this.in);
                if (1 <= input && input <= 4) {
                    break;
                } else {
                    System.out.println("[ERROR] Invalid input.");
                }
            }

            if (input == 1) {
                System.out.print("\nEnter user id: ");
                uid = Utils.promptLine(this.in);
                System.out.print("Enter call number: ");
                callnum = Utils.promptLine(this.in);
                System.out.print("Enter copy number: ");
                copynum = Utils.promptInt(this.in);
                // Ask why this line is needed? -> scanner.nextLine();
                try {
                    String date = currentDate();
                    rentCar(uid, callnum, copynum, date);
                } catch (SQLException e) {
                    System.out.println("[ERROR]: " + e);
                    System.exit(1);
                }
            }
            else if (input == 2) {
                System.out.print("\nEnter user id: ");
                uid = Utils.promptLine(this.in);
                System.out.print("Enter call number: ");
                callnum = Utils.promptLine(this.in);
                System.out.print("Enter copy number: ");
                copynum = Utils.promptInt(this.in);
                try {
                    String date = currentDate();
                    returnCar(uid, callnum, copynum, date);
                } catch (SQLException e) {
                    System.out.println("[ERROR]: " + e);
                    System.exit(1);
                }
            }
            else if (input == 3) {
                System.out.print("\nEnter start date: ");
                start_date = Utils.promptLine(this.in);
                System.out.print("Enter end date: ");
                end_date = Utils.promptLine(this.in);
                try {
                    listUnreturned(start_date, end_date);
                } catch (SQLException e) {
                    System.out.println("[ERROR]: " + e);
                    System.exit(1);
                }
            }
            else if (input == 4) {
                //ALWAYS TRUE?
                break;
            }
        }

    }

    private String currentDate() throws SQLException {
        String date = "";
        //get current date into a string
        String sql = "SELECT CONVERT(CURDATE(), CHAR)";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        if(!resultSet.isBeforeFirst())
            System.out.println("\n[Error]");
        else
            while(resultSet.next()){
                date = resultSet.getString(1);
            }
        return date;
    }

    private void rentCar(String uid, String callnum, int copynum, String date) throws SQLException {
        ResultSet resultSet;
        // check whether callnum and copynum exist
        String sql = "SELECT * FROM RENT WHERE callnum = ? AND copynum = ? AND return_date = 'NULL' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, callnum);
        stmt.setInt(2, copynum);
        resultSet = stmt.executeQuery();

        if(!resultSet.isBeforeFirst()) {
            // if result is empty, car not exist
            System.out.println("\n[Message]: Car not exist. Renting failed");
        }
        else {
            // if car exist, check availability
            // query to check whether car available by return_date
            sql = "SELECT * FROM RENT WHERE callnum = ? AND copynum = ? AND return_date = 'NULL' ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, callnum);
            stmt.setInt(2, copynum);
            resultSet = stmt.executeQuery();

            if(!resultSet.isBeforeFirst()){
                // if result is empty, car is available (no NULL value in return_date)
                // insert new rent record
                sql = "INSERT INTO RENT (callnum, copynum, uid, checkout, return_date) VALUES (?, ?, ?, ?, 'NULL')";
                try{
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, callnum);
                    stmt.setInt(2, copynum);
                    stmt.setString(3, uid);
                    stmt.setString(4, date);
                    stmt.executeUpdate();
                }catch(SQLException e){
                    // if data cannot be inserted, either user id or callnum or copynum fail the foreign key constraint
                    System.out.println("\n[Error]: Car or User does not exist. Renting failed");
                    return;
                }
                System.out.println("\n[Message]: Car available. Renting succeeded");
            }
            else            // if car not available
                System.out.println("\n[Message]: Car not available. Renting failed.");

        }
    }

    private void returnCar(String uid, String callnum, int copynum, String date) throws SQLException {
        ResultSet resultSet;
        // query to check whether car is unreturned
        String sql = "SELECT * FROM RENT WHERE callnum = ? AND copynum = ? AND uid = ? AND return_date = 'NULL' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, callnum);
        stmt.setInt(2, copynum);
        stmt.setString(3, uid);
        resultSet = stmt.executeQuery();

        if(!resultSet.isBeforeFirst())
            // if result is empty, car is returned already or no return record (return operation failed)
            System.out.println("\n[Message]: Returning failed.");
        else{
            // if car can be returned, update rent record
            sql = "UPDATE RENT SET return_date = ? WHERE callnum = ? AND copynum = ? AND uid = ? AND return_date = 'NULL'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, date);
            stmt.setString(2, callnum);
            stmt.setInt(3, copynum);
            stmt.setString(4, uid);
            stmt.executeUpdate();
            System.out.println("\n[Message]: Returning succeeded.");
        }
    }

    private void listUnreturned(String start_date, String end_date) throws SQLException {
        ResultSet resultSet;
        String sql = "SELECT uid, callnum, copynum, checkout FROM RENT WHERE return_date = 'NULL' AND checkout >= ? AND checkout <= ? ORDER BY checkout DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, start_date);
        stmt.setString(2, end_date);
        resultSet = stmt.executeQuery();
        // print result
        if(!resultSet.isBeforeFirst())
            System.out.println("\n[Message]: No records found");
        else{
            System.out.println("\n| User ID | Call Num | Copy Num | Checkout | ");
            while(resultSet.next()){
                System.out.println("| " + resultSet.getString(1)
                        + " | " + resultSet.getString(2)
                        + " | " + resultSet.getInt(3)
                        + " | " + (resultSet.getString(4))
                        + " | ");
            }
        }
    }

}