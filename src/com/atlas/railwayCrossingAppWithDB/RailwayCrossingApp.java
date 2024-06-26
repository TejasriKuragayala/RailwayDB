package com.atlas.railwayCrossingAppWithDB;

import com.atlas.railwayCrossingAppWithDB.Controller.RailwayCrossingController;
import com.atlas.railwayCrossingAppWithDB.PublicApp;


import java.util.Scanner;

public class RailwayCrossingApp {
    public static void main(String[] args) {
        while(true) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Welcome to Railway Crossing App");
            System.out.println("1: End User App");
            System.out.println("2: Government App");
            System.out.println("3: Close App");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    PublicApp.getInstance().startPublicApp();
                    break;
                case 2:
                   GovernmentApp.getInstance().startGovernmentApp();
                    break;
                case 3:
                    RailwayCrossingController.getInstance().closeConnection();
                    System.out.println("Thank you for using the application");
                    break;
                default:
                    System.err.println("Invalid Choice");
            }
            if(choice == 3) {
                break;
            }
        }
    }
}
