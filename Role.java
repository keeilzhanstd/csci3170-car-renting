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
        return Main.promptInt(this.in);
    }

    public void run() {
        int input;
        while((input = getInput()) != 4) {
            switch (input) {
                case 1 -> (new Admin(conn, in)).start();
                case 2 -> (new Manager(conn, in)).start();
                case 3 -> (new User(conn, in)).start();
                default -> System.out.println("[ERROR] Invalid input! Please retry.");
            }
        }
    }
}