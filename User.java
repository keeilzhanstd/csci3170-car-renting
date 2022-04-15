package com.company;
import java.sql.*;
import java.util.Scanner;

public class User {
    final Connection conn;
    final Scanner in;
    private static final String userMenuMessage = "\n---User Menu---\n1. Search car\n2. Show loan record\n3. Return to main menu\nChoice: ";
    private static final String userSearchCarMessage = "\n---Search Criterion---\n1. Call number\n2. Name\n3. Company\nChoice: ";
    int input, criteria;
    String keyword, uid;

    public User(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void start(){
        while(true) {
            System.out.println(userMenuMessage);
            while(true) {
                input = Main.promptInt(this.in);
                if (1 <= input && input <= 3) {
                    break;
                } else {
                    System.out.println("[ERROR]: Invalid input.");
                }
            }

            if (input == 1) {
                System.out.print(userSearchCarMessage);
                criteria = Main.promptInt(this.in);
                if (criteria == 1 || criteria == 2 || criteria == 3){
                    System.out.print("\nEnter search keyword: ");
                    keyword = Main.promptLine(this.in);
                    try {
                        searchCar(criteria, keyword);
                    } catch (SQLException e){
                        System.out.println("[ERROR]: " + e);
                        System.exit(1);
                    }
                }
                else
                    System.out.println("\n[Error]: Invalid input");
            } else if (input == 2) {
                System.out.print("\nEnter user id: ");
                uid = Main.promptLine(this.in);
                try {
                    showLoan(uid);
                } catch (SQLException e){
                    System.out.println("[ERROR]: " + e);
                    System.exit(1);
                }
            } else {
                break;
            }
        }
    }

    private void searchCar(int criterion, String keyword) throws SQLException {
        ResultSet resultSet = null;
        if (criterion == 1){
            // search by callnum
            String sql = "SELECT C.callnum, C.name, CC.ccname, P.cname " +
                    "FROM CAR AS C, CAR_CATEGORY AS CC, PRODUCE AS P " +
                    "WHERE C.ccid = CC.ccid AND C.callnum = P.callnum AND C.callnum = ? " +
                    "ORDER BY C.callnum";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            resultSet = stmt.executeQuery();
        }
        else if (criterion == 2){
            // search by name
            String sql = "SELECT C.callnum, C.name, CC.ccname, P.cname " +
                    "FROM CAR AS C, CAR_CATEGORY AS CC, PRODUCE AS P " +
                    "WHERE C.ccid = CC.ccid AND C.callnum = P.callnum AND C.name LIKE ? " +
                    "ORDER BY C.callnum";
            PreparedStatement stmt = conn.prepareStatement(sql);
            keyword = "%" + keyword + "%";
            stmt.setString(1, keyword);
            resultSet = stmt.executeQuery();
        }
        else if (criterion == 3){
            // search by company
            String sql = "SELECT C.callnum, C.name, CC.ccname, P.cname " +
                    "FROM CAR AS C, CAR_CATEGORY AS CC, PRODUCE AS P " +
                    "WHERE C.ccid = CC.ccid AND C.callnum = P.callnum AND P.cname LIKE ? " +
                    "ORDER BY C.callnum";
            PreparedStatement stmt = conn.prepareStatement(sql);
            keyword = "%" + keyword + "%";
            stmt.setString(1, keyword);
            resultSet = stmt.executeQuery();
        }

        if(!resultSet.isBeforeFirst())
            System.out.println("\n[Message]: No records found");
        else {
            System.out.println("\n| Call Num | Name | Car Category | Company | ");
            while (resultSet.next()) {
                System.out.println("| " + resultSet.getString(1)
                        + " | " + resultSet.getString(2)
                        + " | " + resultSet.getString(3)
                        + " | " + resultSet.getString(4)
                        + " | ");
            }
        }
    }

    private void showLoan(String uid) throws SQLException {
        // query
        ResultSet resultSet;
        String sql = "SELECT callnum, copynum, checkout, return_date " +
                "FROM RENT " +
                "WHERE uid = ? " +
                "ORDER BY checkout DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, uid);
        resultSet = stmt.executeQuery();

        // print result
        if(!resultSet.isBeforeFirst())
            System.out.println("\n[Message]: No records found");
        else{
            System.out.println("\n| Call Num | Copy Num | Checkout | Returned? | ");
            while(resultSet.next()){
                System.out.println("| " + resultSet.getString(1)
                        + " | " + resultSet.getString(2)
                        + " | " + resultSet.getString(3)
                        + " | " + (resultSet.getString(4).equals("NULL")? "No" : "Yes")
                        + " | ");
            }
        }
    }

}
