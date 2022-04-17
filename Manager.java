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
            System.out.print(Utils.managerMenuMessage);
            while(true) {
                input = Utils.promptInt(this.in);
                if (1 <= input && input <= 4) {
                    break;
                } else {
                    System.out.print("[ERROR] Invalid input. Choose between [1, 4]\n Choice: ");
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
                }
            }
            else if (input == 3) {
                System.out.print("\nEnter start date [yyyy-mm-dd]: ");
                start_date = Utils.promptLine(this.in);
                System.out.print("Enter end date [yyyy-mm-dd]: ");
                end_date = Utils.promptLine(this.in);
                try {
                    listUnreturned(start_date, end_date);
                } catch (SQLException e) {
                    System.out.println("[ERROR]: " + e);
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

    private void rentCar( String uid, String callnum, int copynum, String date) throws SQLException {


        int currentNumberOfBorrowedCars = 0;
        int maximumNumberAllowed = 0;

        ResultSet count = null;
        // query to check whether car available by return_date
        String sqlCount = "SELECT COUNT(*) from RENT where uid = ?";
        PreparedStatement stmtcount = conn.prepareStatement(sqlCount);
        stmtcount.setString(1, uid);
        count = stmtcount.executeQuery();
        if(!count.isBeforeFirst()) {
            System.out.println("\n[Message]: No records found");
        }
        else{
            while(count.next()){
                currentNumberOfBorrowedCars = count.getInt(1);
            }
        }

        //Get max number of cars allowed for the user.

        ResultSet maxForUser = null;
        // query to check whether car available by return_date
        String sqlMax = "SELECT UC.max FROM USER_CATEGORY AS UC, USER as U WHERE U.uid = ? AND UC.ucid = U.ucid";
        PreparedStatement stmtMax = conn.prepareStatement(sqlMax);
        stmtMax.setString(1, uid);
        maxForUser = stmtMax.executeQuery();
        if(!count.isBeforeFirst()) {
            System.out.println("\n[Message]: No records found");
        }
        else{
            while(maxForUser.next()){
                maximumNumberAllowed = maxForUser.getInt(1);
            }
        }

        if(currentNumberOfBorrowedCars >= maximumNumberAllowed){
            System.out.println("\n[Error]: User " + uid + " reached maximum capacity of cars available to borrow for his user category.");
            return;
        }

        ResultSet resultSet = null;
        // query to check whether car available by return_date
        String sql = "SELECT * " +
                "FROM RENT " +
                "WHERE callnum = ? AND copynum = ? AND return_date = 'NULL'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, callnum);
        stmt.setInt(2, copynum);
        resultSet = stmt.executeQuery();

        if(!resultSet.isBeforeFirst()){     // if result is empty, car is available (no NULL value in return_date)
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