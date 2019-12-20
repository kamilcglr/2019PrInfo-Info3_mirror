package fr.tse.ProjetInfo3.mvc.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * This class init the tables, use it CAREFULLY
 * */
public class H2jsbcCreateOrDelete {
    public static void main(String[] args) {
        //  deleteAllTables();
       createTables();
        //oldCreateUser();
    }

    public static void deleteAllTables() {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = SingletonDBConnection.getInstance();
            stmt = conn.createStatement();

            stmt.executeUpdate("drop table interestpoint");
            stmt.executeUpdate("drop table hashtag");
            stmt.executeUpdate("drop table twitteruser");
            stmt.executeUpdate("drop table userApp");


            System.out.println("Delete all tables tables in given database...");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // STEP 4: Clean-up environment
            try {
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }


    public static void createTables() {
        Connection conn = null;
        Statement stmt = null;
        try {

            //STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = SingletonDBConnection.getInstance();

            //STEP 3: Execute a query
            System.out.println("Creating tables in given database...");
            stmt = conn.createStatement();

            // we are creating interestpoint table and hashtag table
            // hashtag table will have a foreign key which is
            // the interestpoint_id so we could for each PI store its hashtags !

            String interestpoint = "CREATE TABLE   interestpoint " +
                    "(interestpoint_id INTEGER AUTO_INCREMENT,"
                    + "name VARCHAR(255) , " +
                    " description VARCHAR(255), " +
                    " created_at DATE,"
                    + "PRIMARY KEY (interestpoint_id))";

            String hashtag = "CREATE TABLE   hashtag " +
                    "( hashtag_id INTEGER AUTO_INCREMENT,"
                    + "hashtag VARCHAR(255),"
                    + "PRIMARY KEY (hashtag_id),"
                    + "interestpoint_id INTEGER,"
                    + "FOREIGN KEY(interestpoint_id) REFERENCES interestpoint)";

            String user = "CREATE TABLE   twitteruser " +
                    "( user_id INTEGER AUTO_INCREMENT,"
                    + "userName VARCHAR(255),"
                    + "userScreenName VARCHAR(255),"
                    + "PRIMARY KEY (user_id),"
                    + "interestpoint_id INTEGER,"
                    + "FOREIGN KEY(interestpoint_id) REFERENCES interestpoint)";
            
            String newUser = "CREATE TABLE   usercached " +
                    "( user_id INTEGER AUTO_INCREMENT,"
                    + "userScreenName VARCHAR(255),"
                    + "PRIMARY KEY (user_id),"
                    + "data CLOB)";
            
            //stmt.executeUpdate(interestpoint);
            //stmt.executeUpdate(hashtag);
            //stmt.executeUpdate(user);

            stmt.executeUpdate(newUser);
            
            System.out.println("Created tables in given database...");

            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // STEP 4: Clean-up environment
            try {
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Goodbye!");
        }
    }

    public static void oldCreateUser() {
        Connection conn = null;
        Statement stmt = null;
        try {

            //STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = SingletonDBConnection.getInstance();

            //STEP 3: Execute a query
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            String userapp = "CREATE TABLE   user" +
                    "(username VARCHAR(255) , " +
                    " mail VARCHAR(255), " +
                    " twitter VARCHAR(255), " +
                    " password VARCHAR(20))";
            stmt.executeUpdate(userapp);
            System.out.println("Created tables in given database...");
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // STEP 4: Clean-up environment
            try {
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Goodbye!");
        } //end try
        System.out.println("Goodbye!");
    }
}
