package fr.tse.ProjetInfo3.mvc.repository;

import javax.swing.plaf.nimbus.State;
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

    public static void main(String[] args) {
        try {
            System.out.println("Connecting to database...");
            conn = SingletonDBConnection.getInstance();
            stmt = conn.createStatement();

            //deleteAllTables();
            createAllTables();
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

    //TODO Delete
    public static void select() {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT * FROM hashtagfavs WHERE favourite=1";

        ResultSet rs;
        try {
            System.out.println("Connecting to database...");
            conn = SingletonDBConnection.getInstance();

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("userName: " + rs.getString("hashtag"));
                System.out.println("favourite : " + rs.getInt("favourite"));

            }
            System.out.println("bye");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            deleteFavoritesTable();
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
    public static void deleteFavoritesTable() {
        try {
            stmt.executeUpdate("drop table twitterUserFavs");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate("drop table hashtagFavs");
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
        try {
            stmt.executeUpdate("drop table hashtag");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate("drop table twitteruser");
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
        try {
            stmt.executeUpdate("drop table tweetCached");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the table that contain user of application (e.g. Bob)
     */
    public static void createAllTables() {
        createUserOfAppTable();
        createFavoritesTable();
        createCachedTables();
        createInterestPointTables();
    }

    /**
     * Create favorites users and hashtags
     */
    public static void createFavoritesTable() {


        String twitterUserFavs = "CREATE TABLE   twitterUserFavs " +
                "( user_id INTEGER AUTO_INCREMENT,"
                + "userName VARCHAR(255),"
                + "userScreenName VARCHAR(255),"
                + "PRIMARY KEY (user_id),"
                + "favourite INTEGER)";
        String hashtagFavs = "CREATE TABLE   hashtagFavs " +
                "( hashtag_id INTEGER AUTO_INCREMENT,"
                + "hashtag VARCHAR(255),"
                + "PRIMARY KEY (hashtag_id),"
                + "favourite INTEGER,)";

        try {
            stmt.execute(twitterUserFavs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.execute(hashtagFavs);
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
                + "name VARCHAR(255) , " +
                " description VARCHAR(255), " +
                " created_at DATE,"
                + "PRIMARY KEY (interestpoint_id))";

        String hashtag = "CREATE TABLE   hashtag " +
                "( hashtag_id INTEGER AUTO_INCREMENT,"
                + "hashtag VARCHAR(255),"
                + "PRIMARY KEY (hashtag_id),"
                + "interestpoint_id INTEGER,"
                + "favourite INTEGER,"
                + "FOREIGN KEY(interestpoint_id) REFERENCES interestPoint)";

        String twitterUser = "CREATE TABLE   twitterUser " +
                "( user_id INTEGER AUTO_INCREMENT,"
                + "userName VARCHAR(255),"
                + "userScreenName VARCHAR(255),"
                + "PRIMARY KEY (user_id),"
                + "interestpoint_id INTEGER,"
                + "favourite INTEGER,"
                + "FOREIGN KEY(interestpoint_id) REFERENCES interestpoint)";

        try {
            stmt.executeUpdate(interestPoint);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate(hashtag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate(twitterUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the table that contain user of application (e.g. Bob)
     */
    public static void createUserOfAppTable() {
        String userApp = "CREATE TABLE   userApp" +
                "(username VARCHAR(255) , " +
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
                "( user_id INTEGER ,"
                + "userScreenName VARCHAR(255),"
                + "PRIMARY KEY (user_id),"
                + "data CLOB)";

        String tweetCached = "CREATE TABLE   tweetCached " +
                "( tweet_id INTEGER ,"
                + "data CLOB)";

        String hashtagCached = "CREATE TABLE   hashtagCached " +
                "( hashtag_id INTEGER ,"
                + "data CLOB)";

        try {
            stmt.executeUpdate(userCached);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.executeUpdate(tweetCached);
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