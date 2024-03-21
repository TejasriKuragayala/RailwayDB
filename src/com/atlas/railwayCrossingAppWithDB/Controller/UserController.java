package com.atlas.railwayCrossingAppWithDB.Controller;

import com.atlas.railwayCrossingAppWithDB.DataBase.DB;
import com.atlas.railwayCrossingAppWithDB.Model.User;
public class UserController {
    private static UserController userController;
    private UserController()   {    }
    public static UserController getInstance(){
        if(userController==null)
        {
            userController= new UserController();
        }
        return userController;
    }
    DB db = DB.getInstance();
    public boolean registerUser(User user)
    {
        if(user.validate()) {
            return db.set(user);
        }else {
            System.err.println("Email or password can not be empty!");
        }
        return false;
    }
    public boolean userLogin(User user)
    {
        if(user.validate())
        {
            User retrievedUser = (User) db.retrieve(user.getEmail());
            if(retrievedUser!=null && retrievedUser.getUserType()== User.userType.USER){
                user.setName(retrievedUser.getName());
                return retrievedUser.getEmail().equalsIgnoreCase(user.getEmail()) &&
                        retrievedUser.getPassword().equalsIgnoreCase(user.getPassword());
            }

        }
        return false;
    }
    public int getUserCount()
    {
        return db.getUserCount();
    }

}
