package com.company;

import java.util.Scanner;

public class Utils {
    public static final String RoleMessage = "\n################################\nWelcome to car renting system!\n---Main Menu---\n1. Admin\n2. User\n3. Manager\n4. Exit\nChoice: ";
    public static final String managerMenuMessage = "\n################################\n---Manager Menu---\n1. Car renting\n2. Car returning\n3. List unreturned car\n4. Return to main menu\nChoice: ";
    public static final String adminMenuMessage = "\n################################\n---Admin Menu---\n1. Create table\n2. Delete table\n3. Load data\n4. Show number of record\n5. Return to main menu\nChoice: ";
    public static final String userMenuMessage = "\n################################\n---User Menu---\n1. Search car\n2. Show loan record\n3. Return to main menu\nChoice: ";
    public static final String userSearchCarMessage = "\n################################\n---Search Criterion---\n1. Call number\n2. Name\n3. Company\nChoice: ";

    public static int promptInt(Scanner in) {
        int choice;
        try {
            choice = in.nextInt();
            return choice;
        } catch(Exception e) {
            System.out.println("[Error]: Input is not a valid integer. Terminating program now...");
            System.exit(-1);
        }
        return -1;
    }

    public static String promptLine(Scanner in) {
        String choice;
        try {
            choice = in.next();
            return choice;
        } catch(Exception e) {
            System.out.println("[Error]: Input is not a valid string. Terminating program now...");
            System.exit(-1);
        }
        return null;
    }

}
