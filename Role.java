package com.company;

import java.sql.*;
import java.util.Scanner;

//This class redirects user based on its role either admin, user or manager.
public class Role {
    final Connection conn;
    final Scanner in;

    public Role(Connection conn) {
        this.in = new Scanner(System.in);
        this.conn = conn;
    }

    private int getInput() {
        System.out.print(Utils.RoleMessage);
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
                    System.out.println("[ERROR] Invalid input! Choose between [1, 4].");
            }
        }
    }
}