package fr.tse.ProjetInfo3.mvc.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.TwitterDateParser;

import java.sql.*;
import java.util.Date;

/**
 * This class perform all the actions that has a relation with the DB.
 */
public class DatabaseManager {

    private Gson gson;

    public DatabaseManager() {
        super();
        gson = new GsonBuilder()
                .setPrettyPrinting() //human-readable json
                .setDateFormat(TwitterDateParser.twitterFormat)
                .create();
    }

    /**
     * Cache a User on the db
     *
     * @param user that we want to save (RequestManager.getUser())
     */
    public void cacheUserToDataBase(User user) {
        String userJson = gson.toJson(user);

        Connection connection = SingletonDBConnection.getInstance();
        try {
            String Query = "INSERT INTO usercached (user_id,user_screen_name, date_of_research ,data) "
                    + "VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getScreen_name().toLowerCase());

            //Get the current Date
            java.util.Date date = new java.util.Date();
            preparedStatement.setTimestamp(3, new Timestamp(date.getTime()));
            preparedStatement.setString(4, userJson);

            preparedStatement.executeUpdate();

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cache a tweet on the db
     *
     * @param tweet that we want to save
     */
    public void cacheTweetToDataBase(Tweet tweet) {
        String tweetJson = gson.toJson(tweet);
        Connection connection = SingletonDBConnection.getInstance();
        try {
            String Query = "INSERT INTO tweetcached (tweet_id,data) "
                    + "VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, tweet.getId());
            preparedStatement.setString(2, tweetJson);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cache a hashtag on the db
     *
     * @param hashtag that we want to save
     */
    public void cacheHashtagToDataBase(Hashtag hashtag) {
        String hashtagJson = gson.toJson(hashtag);

        Connection connection = SingletonDBConnection.getInstance();
        try {
            String Query = "INSERT INTO hashtagcached (hashtag_id, hashtag_name, date_of_research, data) "
                    + "VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, hashtag.getId());
            preparedStatement.setString(2, hashtag.getHashtag());
            //Get the current Date
            java.util.Date date = new java.util.Date();
            preparedStatement.setTimestamp(3, new Timestamp(date.getTime()));
            preparedStatement.setString(4, hashtagJson);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a user from the DB.
     *
     * @return user (if exists in DB) null (if not in DB)
     */
    public User getCachedUserFromDatabase(String screen_name) {
        Connection connection = SingletonDBConnection.getInstance();
        User user = null;
        try {
            screen_name = screen_name.toLowerCase();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM usercached WHERE user_screen_name = ? ");
            ps.setString(1, screen_name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = new User();
                user = gson.fromJson(rs.getString("data"), User.class);
                user.setLastSearchDate(new Date(rs.getTimestamp("date_of_research").getTime()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Gets a user from the DB.
     *
     * @return user (if exists in DB) null (if not in DB)
     */
    public Hashtag getCachedHashtagFromDatabase(String hashtagName) {
        Connection connection = SingletonDBConnection.getInstance();
        Hashtag hashtag = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM HASHTAGCACHED WHERE hashtag_name = ? ");
            ps.setString(1, hashtagName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hashtag = new Hashtag();
                hashtag = gson.fromJson(rs.getString("data"), Hashtag.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtag;
    }

    /**
     * Gets a user from the DB.
     *
     * @return user (if exists in DB) null (if not in DB)
     */
    public Tweet getCachedTweetFromDatabase(long tweetID) {
        Connection connection = SingletonDBConnection.getInstance();
        Tweet tweet = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TWEETCACHED WHERE TWEET_ID = ? ");
            ps.setString(1, String.valueOf(tweetID));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tweet = new Tweet();
                tweet = gson.fromJson(rs.getString("data"), Tweet.class);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    /**
     * Delete a user from the DB.
     */
    public void deleteCachedUserFromDataBase(String screen_name) {
        Connection connection = SingletonDBConnection.getInstance();
        User user = null;
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM usercached WHERE user_screen_name = ? ");
            ps.setString(1, screen_name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a hashtag from the DB.
     */
    public void deleteCachedHashtagFromDatabase(String hashtagName) {
        Connection connection = SingletonDBConnection.getInstance();
        Hashtag hashtag = null;
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM HASHTAGCACHED WHERE hashtag_name = ? ");
            ps.setString(1, hashtagName);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a Tweet from the DB.
     */
    public void deleteCachedTweetFromDatabase(long tweetID) {
        Connection connection = SingletonDBConnection.getInstance();
        Tweet tweet = null;
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM TWEETCACHED WHERE TWEET_ID = ? ");
            ps.setString(1, String.valueOf(tweetID));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
