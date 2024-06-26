package com.atlas.railwayCrossingAppWithDB.Controller;

import com.atlas.railwayCrossingAppWithDB.DataBase.DB;
import com.atlas.railwayCrossingAppWithDB.Model.*;

import java.util.LinkedHashMap;
import java.util.Map;
public class RailwayCrossingController {
    private static RailwayCrossingController controller;
    private RailwayCrossingController(){
    }
    public static RailwayCrossingController getInstance(){
        if(controller==null)
        {
            controller= new RailwayCrossingController();
        }
        return controller;
    }
    private DB db = DB.getInstance();
    public boolean loginUser(User user)
    {
        if(user.validate())
        {
           // System.out.println("[RailwayCrossing][loginUser] printing user details:"+user);
          //  System.out.println(db.retrieve(user.getEmail()));
            LinkedHashMap<String,User> results = (LinkedHashMap<String,User>) db.retrieveFromDB(user);
            User retrievedUser = results.get(user.getEmail());
            if(retrievedUser!=null && ((retrievedUser.getUserType()== User.userType.ADMIN)||(retrievedUser.getUserType()== User.userType.INCHARGE))){
                user.setName(retrievedUser.getName());
                return retrievedUser.getEmail().equalsIgnoreCase(user.getEmail()) &&
                        retrievedUser.getPassword().equalsIgnoreCase(user.getPassword());
            }

        }
        return false;
    }
   //DONE
    public boolean addOrUpdateCrossing(RailwayCrossing crossing)
    {
        return  db.set(crossing);
    }
    public boolean updateCrossing(RailwayCrossing crossing)
    {
        return db.updateStatus(crossing);
    }
    public boolean deleteCrossing(RailwayCrossing crossing) {
        return db.delete(crossing);
    }
    public static void searchCrossing(String crossingName)
    {
        Map<String, RailwayCrossing> crossings = (Map<String,RailwayCrossing>) controller.fetchCrossings();
        for(String key:crossings.keySet())
        {
            if(crossings.get(key).getName().equalsIgnoreCase(crossingName)) {
                System.out.println(crossings.get(key));
                return;
            }
        }
        System.out.println("Crossing not found!");
    }
    public Map<String, ?> fetchCrossings() {
        return db.retrieveFromDB(new RailwayCrossing());
    }
    public int getCrossingsCount() {
        return db.getCrossingsCount();
    }
    public void closeConnection() {
        db.closeConnection();
    }
}
