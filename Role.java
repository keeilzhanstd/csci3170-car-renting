package com.company;

import java.sql.*;
import java.util.Scanner;

//This class redirects user based on its role either admin, user or manager.
public class Role {
    final Connection conn;
    final Scanner in;
    private static final String RoleMessage = "\nWelcome to car renting system!\n---Main Menu---\n1. Admin\n2. User\n3. Manager\n4. Exit\nChoice: ";

    public Role(Connection conn) {
        this.in = new Scanner(System.in);
        this.conn = conn;
    }

    private int getInput() {
        System.out.println(RoleMessage);
        return Utils.promptInt(this.in);
    }

    public void run() {
        int input;
        while((input = getInput()) != 4) {
            switch(input) {
                case 1:
                    (new Admin(conn, in)).start();
                    break;
                case 2:
                    (new User(conn, in)).start();
                    break;
                case 3:
                    (new Manager(conn, in)).start();
                    break;
                default:
                    System.out.println("[ERROR] Invalid input! Please retry.");
            }
        }
    }
}