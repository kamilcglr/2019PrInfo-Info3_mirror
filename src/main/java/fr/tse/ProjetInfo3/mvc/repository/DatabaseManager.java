package fr.tse.ProjetInfo3.mvc.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.TwitterDateParser;
import fr.tse.ProjetInfo3.mvc.viewer.HashtagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class perform all the actions that has a relation with the DB.
 */
public class DatabaseManager {

    private Gson gson;
    private static HashtagViewer hashtagViewer = new HashtagViewer();
    private static UserViewer userViewer = new UserViewer();

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
        //Before caching, delete old if present
        deleteCachedUserFromDataBase(user.getScreen_name());
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
     * Cache a hashtag on the db
     * DON'T LOWERCASE because twitter does not. Unique is guaranteed by twitter.
     *
     * @param hashtag that we want to save
     */
    public void cacheHashtagToDataBase(Hashtag hashtag) {
        //Before caching, delete old if present
        deleteCachedHashtagFromDatabase(hashtag.getHashtag());

        String hashtagJson = gson.toJson(hashtag);

        Connection connection = SingletonDBConnection.getInstance();
        try {
            String Query = "INSERT INTO hashtagcached (hashtag_name, date_of_research, data) "
                    + "VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, hashtag.getHashtag());
            //Get the current Date
            java.util.Date date = new java.util.Date();
            preparedStatement.setTimestamp(2, new Timestamp(date.getTime()));
            preparedStatement.setString(3, hashtagJson);
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
    public User getCachedUserFromDatabase(String screenName) {
        Connection connection = SingletonDBConnection.getInstance();
        User user = null;
        try {
            screenName = screenName.toLowerCase();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM usercached WHERE user_screen_name = ? ");
            ps.setString(1, screenName);
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
     * Gets if user is in database
     *
     * @return true if at least one time
     */
    public boolean cachedUserInDataBase(String screenName) {
        Connection connection = SingletonDBConnection.getInstance();
        int result = 0;
        try {
            screenName = screenName.toLowerCase();
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM usercached WHERE user_screen_name = ? ");
            ps.setString(1, screenName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result > 0;
    }

    /**
     * Gets a hashtag from the DB.
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
                hashtag.setLastSearchDate(new Date(rs.getTimestamp("date_of_research").getTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtag;
    }

    /**
     * Gets if user is in database
     *
     * @return true if at least one time
     */
    public boolean cachedHashtagInDataBase(String hashtagName) {
        Connection connection = SingletonDBConnection.getInstance();
        int result = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM HASHTAGCACHED WHERE hashtag_name = ? ");
            ps.setString(1, hashtagName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result > 0;
    }

    /**
     * Delete a user from the DB.
     */
    public void deleteCachedUserFromDataBase(String screen_name) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            screen_name = screen_name.toLowerCase();
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

    public long saveInterestPointToDataBase(InterestPoint interestPoint) {
        Connection connection = SingletonDBConnection.getInstance();
        long piID = 0;
        try {
            String Query = "INSERT INTO interestpoint(NAME,DESCRIPTION, LIST_USERS, LIST_HASHTAGS, CREATED_AT) "
                    + "VALUES (?,?,?,?,?)";
            //Statement.RETURN_GENERATED_KEYS to get the id of inserted element
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, interestPoint.getName());
            preparedStatement.setString(2, interestPoint.getDescription());

            //Convert users screen names to List then to Json
            List<String> usersScreenNames = interestPoint.getUsers().stream()
                    .map(user -> user.getScreen_name().toLowerCase())
                    .collect(Collectors.toList()); //twitter guaranty non case sensibility
            String userScreenNamesJson = gson.toJson(usersScreenNames);
            preparedStatement.setString(3, userScreenNamesJson);

            List<String> hashtagsNames = interestPoint.getHashtags().stream()
                    .map(Hashtag::getHashtag) //DO NOT PUT LOWERCASE
                    .collect(Collectors.toList());
            String hashtagsNamesJson = gson.toJson(hashtagsNames);
            preparedStatement.setString(4, hashtagsNamesJson);

            preparedStatement.setDate(5, new java.sql.Date(interestPoint.getDateOfCreation().getTime()));
            preparedStatement.executeUpdate();

            //Save the hashtags and users only if the preparedStatement is successful
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getLong(1));
                    //TODO delete if it is unnecessary
                    piID = generatedKeys.getLong(1);
                    //PreparedStatement ps = connection.prepareStatement("SELECT * FROM HASHTAGCACHED WHERE hashtag_name = ? ");
                    //ps.setString(1, hashtagName);
                    //ResultSet rs = ps.executeQuery();
                    //while (rs.next()) {
                    //    hashtag = new Hashtag();
                    //    hashtag = gson.fromJson(rs.getString("data"), Hashtag.class);
                    //    hashtag.setLastSearchDate(new Date(rs.getTimestamp("date_of_research").getTime()));
                    //}
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        interestPoint.setId(piID);
        return piID;
    }

    /**
     * Gets the Interest Points from DataBase.
     * NO -> (If Users or Hashtags are already in DB, load them.)
     *
     * @implNote DO NOT DELETE COMMENTS INSIDE UNTIL END SPRINT 5
     */
    public List<InterestPoint> getAllInterestPointFromDataBase() {
        Connection connection = SingletonDBConnection.getInstance();
        List<InterestPoint> interestPoints = new ArrayList<>();
        InterestPoint interestPoint;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM interestpoint");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                interestPoint = new InterestPoint();
                interestPoint.setId(rs.getLong("interestpoint_id"));
                interestPoint.setName(rs.getString("NAME"));
                interestPoint.setDescription(rs.getString("DESCRIPTION"));
                interestPoint.setDateOfCreation(rs.getDate("CREATED_AT"));

                Timestamp timestamp = rs.getTimestamp("DATE_OF_RESEARCH");
                if (timestamp != null) {
                    interestPoint.setLastSearchDate(new Date(timestamp.getTime()));
                }

                TypeToken<List<String>> token = new TypeToken<List<String>>() {
                };

                List<String> usersScreenNames = gson.fromJson(rs.getString("LIST_USERS"), token.getType());
                List<String> hashtagNames = gson.fromJson(rs.getString("LIST_HASHTAGS"), token.getType());

                for (String screenName : usersScreenNames) {
                    User user = new User();
                    //if user is in db
                    //if (this.cachedUserInDataBase(screenName)) {
                    //    user = this.getCachedUserFromDatabase(screenName);
                    //} else {
                    userViewer.searchScreenName(screenName);
                    user = userViewer.getUser();
                    //}
                    interestPoint.addToInterestPoint(user);
                }

                for (String hashtagName : hashtagNames) {
                    Hashtag hashtag = new Hashtag(hashtagName);
                    //if user is in db
                    //if (this.cachedHashtagInDataBase(hashtagName)) {
                    //    hashtag = this.getCachedHashtagFromDatabase(hashtagName);
                    //} else {
                    hashtag.setHashtag(hashtagName);
                    //}
                    interestPoint.addToInterestPoint(hashtag);
                }

                interestPoints.add(interestPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (InterestPoint ip : interestPoints){
            System.out.println(ip.getId());
        }
        return interestPoints;
    }

    /**
     * Gets interest Point from id
     */
    public InterestPoint getSelectedInterestPoint(int id) {
        Connection connection = SingletonDBConnection.getInstance();
        InterestPoint interestPoint = new InterestPoint();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM interestpoint WHERE INTERESTPOINT_ID= ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                interestPoint.setId(rs.getLong("interestpoint_id"));
                interestPoint.setName(rs.getString("NAME"));
                interestPoint.setDescription(rs.getString("DESCRIPTION"));
                interestPoint.setDateOfCreation(rs.getDate("CREATED_AT"));

                TypeToken<List<String>> token = new TypeToken<List<String>>() {
                };

                List<String> usersScreenNames = gson.fromJson(rs.getString("LIST_USERS"), token.getType());
                List<String> hashtagNames = gson.fromJson(rs.getString("LIST_HASHTAGS"), token.getType());

                for (String screenName : usersScreenNames) {
                    User user = new User();
                    //if user is in db
                    if (this.cachedUserInDataBase(screenName)) {
                        user = this.getCachedUserFromDatabase(screenName);
                    } else {
                        userViewer.searchScreenName(screenName);
                        user = userViewer.getUser();
                    }
                    interestPoint.addToInterestPoint(user);
                }

                for (String hashtagName : hashtagNames) {
                    Hashtag hashtag = new Hashtag();
                    //if user is in db
                    if (this.cachedHashtagInDataBase(hashtagName)) {
                        hashtag = this.getCachedHashtagFromDatabase(hashtagName);
                    } else {
                        hashtag.setHashtag(hashtagName);
                    }
                    interestPoint.addToInterestPoint(hashtag);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return interestPoint;
    }

    /**
     * Delete PI from his id.
     */
    public void deleteSelectedInterestPointById(long id) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM interestpoint WHERE interestpoint_id = ?");
            ps.setLong(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets list of tweets from IP
     *
     * @return List of tweets. If nothing, the size is 0.
     */
    public List<Tweet> getTweetsFromInterestPoint(long id) {
        Connection connection = SingletonDBConnection.getInstance();
        List<Tweet> tweetList = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT LIST_TWEETS FROM INTERESTPOINT WHERE INTERESTPOINT_ID = ? ");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TypeToken<List<Tweet>> token = new TypeToken<List<Tweet>>() {
                };
                tweetList = gson.fromJson(rs.getString("LIST_TWEETS"), token.getType());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweetList;
    }

    /**
     * Sets list of tweets to IP
     *
     * @param id
     * @param tweets new tweets
     */
    public void setTweetsToInterestPoint(long id, List<Tweet> tweets) {
        Connection connection = SingletonDBConnection.getInstance();
        String tweetsJson = gson.toJson(tweets);
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE INTERESTPOINT SET LIST_TWEETS = ?, DATE_OF_RESEARCH = ? WHERE INTERESTPOINT_ID = ? ");
            ps.setString(1, tweetsJson);

            //Get the current Date
            java.util.Date date = new java.util.Date();
            ps.setTimestamp(2, new Timestamp(date.getTime()));

            ps.setLong(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete list of tweets to IP
     */
    public void deleteTweetsFromInterestPoint(long id) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE INTERESTPOINT SET LIST_TWEETS = ?, DATE_OF_RESEARCH = ? WHERE INTERESTPOINT_ID = ? ");
            ps.setString(1, null);

            //Get the current Date
            ps.setTimestamp(2, null);

            ps.setLong(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
