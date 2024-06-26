package com.atlas.railwayCrossingAppWithDB.DataBase;
import com.atlas.railwayCrossingAppWithDB.Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DB implements DAO{
    private LinkedHashMap<String,User> users = new LinkedHashMap<>();
    private LinkedHashMap<String, RailwayCrossing> crossings = new LinkedHashMap<>();
    private static DB db;
    private static Connection conn;
    private static Connection getConnection(){
        String DB_URI="jdbc:sqlserver://localhost:52964;databaseName=RailwayDB;trustServerCertificate=true;";
        String user="ktejasri";
        String Password="ktejasri123";
        try {
            conn= DriverManager.getConnection(DB_URI);
            if(conn!=null)
            {
                System.out.println("Successfully to database Connected!");
                return conn;
            }else{
                System.out.println("Something went wrong!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private void createTableUsers() throws SQLException {
        String sqlCreate = "IF OBJECT_ID('dbo.Users', 'U') IS NULL CREATE TABLE Users (\n" +
                "\t\t\t\tuserId  int not null IDENTITY(1,1) primary key,\n" +
                "                name nvarchar(100),\n" +
                "                email nvarchar(50) NOT NULL UNIQUE,\n" +
                "                password nvarchar(30),\n" +
                "                userType nvarchar(10))";
        Statement stmt = conn.createStatement();
        stmt.execute(sqlCreate);
    }
    private void createTableRailwayCrossing() throws SQLException {
        String sqlCreate = "IF OBJECT_ID('dbo.RailwayCrossing', 'U') IS NULL CREATE TABLE RailwayCrossing" +
                "(crossingId  int  IDENTITY(1,1) primary key,"
                + "name nvarchar(100),"
                + "address nvarchar(200),"
                + "status nvarchar(10),"
                + "personInchargeId int FOREIGN KEY(personInchargeId) REFERENCES Users(userId) ON DELETE cascade,"
                + "scheduleId int FOREIGN KEY(scheduleId) REFERENCES Schedule(scheduleId) ON DELETE NO ACTION)";

        Statement stmt = conn.createStatement();
        stmt.execute(sqlCreate);
    }
    private void createTableSchedule() throws SQLException {
        String sqlCreate = "IF OBJECT_ID('dbo.Schedule', 'U') IS NULL CREATE TABLE Schedule" +
                "(scheduleId  int not null IDENTITY(1,1) PRIMARY KEY,"
                + "startTime nvarchar(100),"
                + "endTime nvarchar(100)," +
                " personInchargeId int FOREIGN KEY(personInchargeId) REFERENCES Users(userId) ON DELETE No ACTION," +
                "UNIQUE(startTime,personInchargeId))";

        Statement stmt = conn.createStatement();
        stmt.execute(sqlCreate);
    }
    private DB(){
        try {
            conn=getConnection();
            createTableUsers();
            createTableSchedule();
            createTableRailwayCrossing();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        populateAdminUser();
    }
    public  static DB getInstance(){
        if(db==null){
             db= new DB();
        }
        return db;
    }
    public void closeConnection(){
        try {
            conn.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    void populateAdminUser() {
         String sqlstatement = "SELECT * FROM Users where userType='ADMIN'";
         try{
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlstatement);
             if(rs.next()==false){
                 set( new User("ktejasri", "ktejasri@railway.com", "ktejasri123", User.userType.ADMIN));
             }

         }catch (SQLException e){
             e.printStackTrace();
         }
    }
    public boolean set(Object object){

        if(object instanceof User)
        {
            User user = (User) object;

            String statement ="INSERT INTO USERS(name,email,password,userType) VALUES("+"'"
                    +user.getName()+"','"+user.getEmail()+"','"+user.getPassword()+"','"+user.getUserType()+
                    "')";
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM USERS WHERE email='"+user.getEmail()+"'");
                if(rs.next()==false) {
                    st.executeUpdate(statement);
                    return true;
                }else{
                    return false;
                }
            }catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }else{
            RailwayCrossing crossing =(RailwayCrossing) object;
            try {
                Statement statement1 = conn.createStatement();
                // 1. Insert the In-charge details into USERS TABLE
                set(new User(crossing.getPersonInCharge().getName(),
                        crossing.getPersonInCharge().getEmail(),
                        crossing.getPersonInCharge().getPassword(),
                        crossing.getPersonInCharge().getUserType()));
                String st1 = "SELECT userId FROM USERS WHERE email='"+crossing.getPersonInCharge().getEmail()+"'";
                ResultSet rs = statement1.executeQuery(st1);
                int personInChargeId=-1;
                while (rs.next()) {
                     personInChargeId = rs.getInt("userId");
                }
                for(String to:crossing.getSchedules().keySet())
                {
                    Statement scst = conn.createStatement();
                    ResultSet scResult = scst.executeQuery("SELECT * FROM Schedule WHERE startTime='"+to+"' AND personInchargeId="+personInChargeId);
                    if(scResult.next()==false){
                    String sc = "INSERT INTO Schedule (startTime,endTime,personInchargeId) VALUES('"+
                            to+"','"+crossing.getSchedules().get(to)+"',"+personInChargeId+
                    ")";
                    statement1.executeUpdate(sc);}
                    else{
                        System.err.println("You are trying to ENTER duplicate schedule. Please Delete the existing record and Enter Again!");
                        return false;
                    }
                }
                String st2 = "SELECT scheduleId FROM Schedule WHERE personInchargeId="+personInChargeId;
                ResultSet rs2 = statement1.executeQuery(st2);
                ArrayList<Integer> results = new ArrayList<>();
                int scheduleId = -1;
                while(rs2.next()) {
                    results.add(rs2.getInt("scheduleId"));
                }
                    for(int r:results){
                        scheduleId=r;
                    String statement = "INSERT INTO RailwayCrossing(name,address,status,personInchargeId,scheduleId) VALUES('" +
                            crossing.getName() + "','"
                            + crossing.getAddress() + "','"
                            + crossing.getStatus()+"',"
                            + personInChargeId + ","
                            + scheduleId +
                            ")";

                    statement1.executeUpdate(statement);
                }
                return true;
            }catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
    }
    public boolean updateStatus(Object object){
        try{
            RailwayCrossing crossing = (RailwayCrossing) object;
            Statement ST = conn.createStatement();
            ResultSet RS = ST.executeQuery("SELECT * FROM RailwayCrossing WHERE name='"+crossing.getName()+"'");
            if(RS.next()!=false) {
                String updates = "UPDATE RailwayCrossing SET status='" + crossing.getStatus() + "' WHERE name='" + crossing.getName() + "'";
                Statement st = conn.createStatement();
                st.executeUpdate(updates);
                return true;
            }
            return false;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Object object){

        try {
            Statement statement = conn.createStatement();
            if (object instanceof User) {
                User user = (User) object;
                String executeStatement = "DELETE FROM USERS WHERE email='" + user.getEmail() + "'";
                statement.executeUpdate(executeStatement);

            } else {
                RailwayCrossing crossing = (RailwayCrossing) object;
                String executeStatement = "DELETE FROM RailwayCrossing WHERE name='" + crossing.getName()+ "'";
                int inchageId =-1;
                ResultSet inSet = statement.executeQuery("SELECT personInchargeId FROM RailwayCrossing WHERE name='"+ crossing.getName()+ "'");
                while (inSet.next()){
                    inchageId=inSet.getInt("personInchargeId");
                }
                String ScheduleDelte="DELETE FROM Schedule WHERE personInchargeId=" + inchageId;
                statement.executeUpdate(executeStatement);
                statement.executeUpdate(ScheduleDelte);
            }
            return true;
        }catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
   public Map<String, ?> retrieveFromDB(Object object) {

      if(object instanceof User)
       {
           User user = (User)object;
           try{
               Statement statement = conn.createStatement();
               String retrievStatement = "SELECT * FROM USERS WHERE email='"+user.getEmail()+"'";
               ResultSet set = statement.executeQuery(retrievStatement);
               while(set.next()){
                   users.put(set.getString("email"),new User(set.getString("name"),set.getString("email"),set.getString("password"), user.convertUserType(set.getString("userType"))));
               }

           }catch (SQLException e)
           {
               e.printStackTrace();
           }
           return users;
       }
       RailwayCrossing RLC = (RailwayCrossing) object;
      if(RLC.getPersonInCharge()!=null) {
          try {
              Statement statement = conn.createStatement();
              String retrievStatement = "SELECT * FROM RailwayCrossing WHERE personInchargeId in (SELECT userId FROM USERS WHERE email='" + RLC.getPersonInCharge().getEmail() + "')";
              ResultSet set = statement.executeQuery(retrievStatement);
              LinkedHashMap<String, String> SCD = new LinkedHashMap<>();
              while (set.next()) {
                  // User (String name, String email, String password, User.userType userType)
                  ResultSet uset = statement.executeQuery("SELECT * FROM USER WHERE userId=" + set.getInt("personInchargeId"));
                  User personInCharge = new User();
                  if (uset.next()) {
                      personInCharge = new User(uset.getString("name"), uset.getString("email"), uset.getString("password"), new User().convertUserType(uset.getString("userType")));
                  }

                  ResultSet sset = statement.executeQuery("SELECT * FROM Schedule WHERE scheduleId=" + set.getInt("scheduleId"));
                  while (sset.next()) {
                      SCD.put(sset.getString("startTime"), sset.getString("endTime"));
                  }
                  crossings.put(personInCharge.getEmail(), new RailwayCrossing(set.getString("name"), set.getString("address"), new RailwayCrossing().convertStatus(set.getString("status")), personInCharge, SCD));
              }
          } catch (SQLException e) {
              e.printStackTrace();
          }
          return crossings;
      }else{
          try {
              Statement statement = conn.createStatement();
              String retrievStatement = "SELECT * FROM RailwayCrossing";
              ResultSet set = statement.executeQuery(retrievStatement);

              while (set.next()) {
                  Statement statemen1 = conn.createStatement();
                  ResultSet uset = statemen1.executeQuery("SELECT * FROM USERS WHERE userId=" + set.getInt("personInchargeId"));
                  User personInCharge = new User();
                  while(uset.next()) {
                      personInCharge = new User(uset.getString("name"), uset.getString("email"), uset.getString("password"), new User().convertUserType(uset.getString("userType")));
                      LinkedHashMap<String, String> SCD = new LinkedHashMap<>();
                      Statement statemen2 = conn.createStatement();
                      ResultSet sset = statemen2.executeQuery("SELECT * FROM Schedule WHERE personInchargeId=" + set.getInt("personInchargeId"));
                      while (sset.next()) {
                          SCD.put(sset.getString("startTime"), sset.getString("endTime"));
                      }
                      crossings.put(personInCharge.getEmail(), new RailwayCrossing(set.getString("name"), set.getString("address"), new RailwayCrossing().convertStatus(set.getString("status")), personInCharge, SCD));
                  }
              }
          } catch (SQLException e) {
              e.printStackTrace();
          }
          return crossings;
      }
   }
    @Override
    public Object retrieve(String key) {
            retrieveFromDB(new User(null,key,null,null));
            retrieveFromDB(new RailwayCrossing(null,null,null,new User(null,key,null, User.userType.INCHARGE),null));
        if(crossings.isEmpty())
        if(users.containsKey(key)) return  users.get(key);
        return crossings.get(key);
    }
    public int getUserCount() {
        try {
            Statement statement = conn.createStatement();
            String retrievStatement = "SELECT count(distinct userId) FROM USERS ";
            ResultSet set = statement.executeQuery(retrievStatement);
            if (set.next())
            return set.getInt(1);
            return 0;
        }catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
    public int getCrossingsCount() {
        try {
            Statement statement = conn.createStatement();
            String retrieveStatement = "SELECT count(distinct personInchargeId) FROM RailwayCrossing ";
            ResultSet set = statement.executeQuery(retrieveStatement);
            if(set.next()){
                return set.getInt(1);
            }
            return 0;
        }catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
}
