package com.atlas.railwayCrossingAppWithDB;

import com.atlas.railwayCrossingAppWithDB.Controller.RailwayCrossingController;
import com.atlas.railwayCrossingAppWithDB.Model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class GovernmentApp {
    RailwayCrossingController controller;
    Scanner scanner;
    private static GovernmentApp app;
    public static GovernmentApp getInstance(){
        if(app==null)
        {
            app= new GovernmentApp();
        }
        return  app;
    }
    private GovernmentApp(){
        controller = RailwayCrossingController.getInstance();
        scanner = new Scanner(System.in);
    }
    public void Home() {
        while(true){
            System.out.println("---------------------------------------");
            System.out.println("Welcome to Railway Crossing Admin Home");
            System.out.println("We have "+controller.getCrossingsCount()+" Crossings in the DataBase");
            System.out.println("1: List Railway Crossings");
            System.out.println("2: Search Railway Crossings");
            System.out.println("3: Add Railway Crossing");
            System.out.println("4: Delete Railway Crossing");
            System.out.println("5: Update status of crossing");
            System.out.println("6: Close Admin Application");
            System.out.println("--------------------------------------");
            int choice = scanner.nextInt();
            switch (choice){
                case 1: listCrossings();
                    break;
                case 2: searchCrossing();
                    break;
                case 3: addCrossings();
                    break;
                case 4: deleteCrossing();
                    break;
                case 5: updateStatus();
                    break;
                case 6: System.out.println("Thank you for using railway crossing App!");
                    break;
                default:
                    System.err.println("Invalid choice. Try Again!");
            }
            if(choice==6)
            {
                break;
            }
        }
    }
    void updateStatus(){
        scanner.nextLine();
        System.out.println("Enter the Crossing name:");
        String crossingName =scanner.nextLine();
        System.out.println("Enter the status to be updated[OPEN/CLOSE]:");
        String status = scanner.nextLine();
        RailwayCrossing.CrossingStatus st = (status.equals("OPEN")? RailwayCrossing.CrossingStatus.OPEN: RailwayCrossing.CrossingStatus.CLOSE);

      //  (String name, String address, RailwayCrossing.CrossingStatus status, User personInCharge, LinkedHashMap<String, String> schedules)
        if(controller.updateCrossing(new RailwayCrossing(crossingName,"",st,null,null)))
        {
            System.out.println("Updated status of railway crossing:"+crossingName
                    +" to "+status);
            return;
        }
        System.out.println("Crossing could not be found!");
    }
    void searchCrossing()
    {
        scanner.nextLine();
        System.out.println("Enter the Crossing name:");
        String crossingName =scanner.nextLine();
        controller.searchCrossing(crossingName);
    }
    void deleteCrossing()
    {
        scanner.nextLine();
        System.out.println("Enter the Crossing name:");
        String crossingName =scanner.nextLine();
        Map<String, RailwayCrossing> crossings = (Map<String,RailwayCrossing>) controller.fetchCrossings();
        for(String key:crossings.keySet())
        {
            if(crossings.get(key).getName().equalsIgnoreCase(crossingName)) {
                if(controller.deleteCrossing(crossings.get(key)))
                {
                    System.out.println("Deleted railway Crossing: " +crossingName);
                }
                return;
            }
        }
        System.out.println("Crossing not found!");
    }
    void startGovernmentApp()
    {
        System.out.println("--------------------------------");
        System.out.println("Welcome Admin User");
        System.out.println("Proceed to Login [In case of no prompt to enter login details please press ENTER once Again!]");
        System.out.println("---------------------------------");
        login();
    }
    void login(){
        scanner.nextLine();
        User user = new User();
        System.out.println("Enter your Email:");
        user.setEmail(scanner.nextLine());
        System.out.println("Enter Password:");
        user.setPassword(scanner.nextLine());
        if(controller.loginUser(user)){
            System.out.println("Hi "+user.getName()+", You have Logged in Successfully!");
            System.out.println("Navigating to Admin page!");
            Home();
        }
        else {
            System.out.println("Invalid user credentials!");
        }
    }

    void addCrossings(){
        User user = new User();
        scanner.nextLine();
        RailwayCrossing crossing = new RailwayCrossing();
        System.out.println("Enter person InCharge details:");
        System.out.println("_______________________________");
        System.out.println("Enter name:");
        user.setName(scanner.nextLine());
        System.out.println("Enter Email");
        user.setEmail(scanner.nextLine());
        System.out.println("Enter Password:");
        user.setPassword(scanner.nextLine());
        user.setUserType(User.userType.INCHARGE);
        System.out.println("________________________________");
        System.out.println("Enter Railway Crossing details:");
        System.out.println("________________________________");
        System.out.println("Enter Crossing name:");
        crossing.setName(scanner.nextLine());
        System.out.println("Enter Crossing Address:");
        crossing.setAddress(scanner.nextLine());
        System.out.println("Enter Crossing Schedule:");
        System.out.println("Enter the \"From\" and \"To\" In 24 hr format comma separated."+"\nEg- HH:MM-HH:MM,HH:MM-HH:MM,...");
        String scheduleInput = scanner.nextLine();
        LinkedHashMap<String,String> schedule = new LinkedHashMap<>();
        String[] scheduleSplit=scheduleInput.split(",");
        for(String ech:scheduleSplit)
        {
            String[] sc = ech.split("-");
            if(sc.length<2){
                System.err.println("Please Enter schedule in proper format HH:MM-HH:MM,HH:MM-HH:MM...");
                return;
            }
            schedule.put(sc[0],sc[1]);
        }
        crossing.setSchedules(schedule);
        crossing.setPersonInCharge(user);
        if(controller.addOrUpdateCrossing(crossing))
        {
            System.out.println(crossing.getName()+", Added successfully!");
        }else {
            System.err.println("Something went wrong... Try Again!");
        }
    }
    void listCrossings(){
        getCrossings(controller,false);
    }
    static ArrayList<RailwayCrossing> getCrossings(RailwayCrossingController controller,boolean rtFlag) {
        ArrayList<RailwayCrossing> railwayCrossingsArray = new ArrayList<>();
        Map<String, RailwayCrossing> crossings = (Map<String,RailwayCrossing>) controller.fetchCrossings();
        if(crossings.isEmpty()){
            System.err.println("Currently Data  not Available!");
            return null;
        }
        for(String key:crossings.keySet())
        {
            if (!rtFlag) {
                System.out.println(crossings.get(key));
            }
            railwayCrossingsArray.add(crossings.get(key));
        }
        return railwayCrossingsArray;
    }
}
