package fr.tse.ProjetInfo3.mvc.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * This class init the tables, use it CAREFULLY
 * */
public class H2jsbcCreateOrDelete {

    private static Connection conn;
    private static Statement stmt;

    /**
     * You this function can delete all the tables and recreate them.
     * They call global function that create or delete anything, if you want a more fain-grained
     * control, you can can the desired function.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to database...");
            conn = SingletonDBConnection.getInstance();
            stmt = conn.createStatement();

            deleteAllTables();
            createAllTables();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }

    /**
     * Delete ALL the tabs
     */
    public static void deleteAllTables() {
        try {
            deleteInterestPointTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            deleteUserOfAppTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            deleteFavoriteTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            deleteCachedTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete favorites users and hashtags
     */
    public static void deleteFavoriteTable() {
        try {
            stmt.executeUpdate("drop table favorites");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete Interest Point Table but also user and hashtag
     */
    public static void deleteInterestPointTable() {
        try {
            stmt.executeUpdate("drop table interestPoint");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the table that contain user of application (e.g. Bob)
     */
    public static void deleteUserOfAppTable() {
        try {
            stmt.executeUpdate("drop table userApp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the table that contain cached object
     */
    public static void deleteCachedTables() {
        try {
            stmt.executeUpdate("drop table hashtagCached");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate("drop table userCached");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the table that contain user of application (e.g. Bob)
     */
    public static void createAllTables() {
        createUserOfAppTable();
        createFavoriteTable();
        createCachedTables();
        createInterestPointTables();
    }

    /**
     * Create favorites table
     */
    public static void createFavoriteTable() {
        String favorite = "CREATE TABLE   favorites " +
                "( favorite_id INTEGER auto_increment,"
                + "user_id INTEGER,"
                + "LIST_USERS CLOB,"
                + "LIST_HASHTAGS CLOB,"
                + "PRIMARY KEY (user_id))";
        try {
            stmt.execute(favorite);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create Interest Point Table but also user and hashtag
     */
    public static void createInterestPointTables() {

        String interestPoint = "CREATE TABLE   interestPoint " +
                "(interestpoint_id INTEGER AUTO_INCREMENT,"
                + "user_id INTEGER,"
                + "name VARCHAR(255),"
                + "LIST_USERS CLOB,"
                + "LIST_HASHTAGS CLOB,"
                + "description VARCHAR(255),"
                + "LIST_TWEETS CLOB,"
                + "date_of_research TIMESTAMP,"
                + "created_at DATE,"
                + "PRIMARY KEY (interestpoint_id))";
        try {
            stmt.executeUpdate(interestPoint);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the table that contain user of application (e.g. Bob)
     */
    public static void createUserOfAppTable() {
        String userApp = "CREATE TABLE   userApp" +
                "(user_id INTEGER AUTO_INCREMENT," +
                " user_name VARCHAR(255) , " +
                " mail VARCHAR(255), " +
                " twitter VARCHAR(255), " +
                " password VARCHAR(20))";
        try {
            stmt.executeUpdate(userApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the table that contain cached object
     */
    public static void createCachedTables() {
        String userCached = "CREATE TABLE   userCached " +
                "( user_id BIGINT,"
                + "user_screen_name VARCHAR(255),"
                + "date_of_research TIMESTAMP,"
                + "PRIMARY KEY (user_id),"
                + "data CLOB)";

        String hashtagCached = "CREATE TABLE   hashtagCached "
                + "( hashtag_name  VARCHAR(255),"
                + "date_of_research TIMESTAMP,"
                + "PRIMARY KEY (hashtag_name),"
                + "data CLOB)";
        try {
            stmt.executeUpdate(userCached);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate(hashtagCached);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}