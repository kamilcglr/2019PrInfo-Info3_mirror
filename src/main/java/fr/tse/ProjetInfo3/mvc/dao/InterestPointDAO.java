package fr.tse.ProjetInfo3.mvc.dao;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.SingletonDBConnection;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * @author ALAMI IDRISSI Taha
 * in this class we will perform all the actions for the PIs that have a direct relations with the DB
 * like Saving , reading , updating PI in the DB
 */
public class InterestPointDAO {


    /**
     * in this method we're saving an interestpoint into the DB using singleton pattern to have one instance
     * accessing the DB
     */
    public long saveInterestPoint(InterestPoint interestPoint) {
        Connection connection = SingletonDBConnection.getInstance();
        long piID = 0;
        try {
            String Query = "INSERT INTO interestpoint(NAME,DESCRIPTION,CREATED_AT) "
                    + "VALUES (?,?,?)";
            //Statement.RETURN_GENERATED_KEYS to get the id of inserted element
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, interestPoint.getName());
            preparedStatement.setString(2, interestPoint.getDescription());
            preparedStatement.setDate(3, new java.sql.Date(interestPoint.getDateOfCreation().getTime()));

            preparedStatement.executeUpdate();

            //Save the hashtags and users only if the preparedStatement is successful
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long idOfIP = generatedKeys.getLong(1);
                    piID = idOfIP;
                    // this method will help us save all the # of a single PI
                    saveHashtag(interestPoint, idOfIP);
                    saveUsers(interestPoint, idOfIP);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return piID;
    }

    /**
     * Saving # of an interestpoint
     */
    public InterestPoint saveHashtag(InterestPoint interestPoint, long idOfIP) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hashtag(hashtag,interestpoint_id) "
                    + "VALUES (?,?)");

            for (Hashtag hash : interestPoint.getHashtags()) {
                preparedStatement.setString(1, hash.getHashtag());
                preparedStatement.setLong(2, idOfIP);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interestPoint;
    }

    /**
     * Saving users of an interestpoint
     */
    public InterestPoint saveUsers(InterestPoint interestPoint, long idOfIP) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO twitteruser(USERNAME,USERSCREENNAME,interestpoint_id) "
                    + "VALUES (?,?,?)");

            for (User user : interestPoint.getUsers()) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getScreen_name());
                preparedStatement.setLong(3, idOfIP);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interestPoint;
    }

    /**
     * here we can get all the data about interestpoints to list them in our window
     * we query all the info needed about all the PIs
     */
    public List<InterestPoint> getAllInterestPoints() {
        Connection connection = SingletonDBConnection.getInstance();
        List<InterestPoint> interestPoints = new ArrayList<>();
        InterestPoint interestPoint = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM interestpoint");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                interestPoint = new InterestPoint();
                interestPoint.setId(rs.getInt("interestpoint_id"));
                interestPoint.setName(rs.getString("NAME"));
                interestPoint.setDescription(rs.getString("DESCRIPTION"));
                interestPoint.setDateOfCreation(rs.getDate("CREATED_AT"));
                interestPoint.setHashtags(getAllHashtagOfAnInterestPoint(interestPoint));
                interestPoint.setUsers(getAllUsersfAnInterestPoint(interestPoint));
                interestPoints.add(interestPoint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interestPoints;
    }

    public List<Hashtag> getAllHashtagOfAnInterestPoint(InterestPoint interestPoint) {

        Connection connection = SingletonDBConnection.getInstance();
        List<Hashtag> hashtags = new ArrayList<Hashtag>();
        Hashtag hashtag = null;
        try {
            PreparedStatement ps2 = connection.prepareStatement("SELECT hashtag FROM hashtag WHERE interestpoint_id = ?");
            ps2.setInt(1, interestPoint.getId());

            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                hashtag = new Hashtag(rs2.getString("hashtag"));
                hashtags.add(hashtag);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtags;
    }

    public List<User> getAllUsersfAnInterestPoint(InterestPoint interestPoint) {

        Connection connection = SingletonDBConnection.getInstance();
        List<User> users = new ArrayList<User>();
        User user = null;
        UserViewer userViewer = new UserViewer();
        try {
            PreparedStatement ps2 = connection.prepareStatement("SELECT USERSCREENNAME FROM TWITTERUSER WHERE interestpoint_id = ?");
            ps2.setInt(1, interestPoint.getId());

            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                userViewer.searchScreenName(rs2.getString("USERSCREENNAME"));
                user = userViewer.getUser();
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * when clicking on a PI in our list we will use this method so we could search it on the DB
     */
    public InterestPoint getSelectedInterestPoint(int id) {
        Connection connection = SingletonDBConnection.getInstance();
        InterestPoint interestPoint = new InterestPoint();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM interestpoint WHERE id= ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                interestPoint.setId(rs.getInt("interestpoint_id"));
                interestPoint.setName(rs.getString("NAME"));
                interestPoint.setDescription(rs.getString("DESCRIPTION"));
                interestPoint.setDateOfCreation(rs.getDate("CREATED_AT"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return interestPoint;
    }
    
    // i had to name this method like this hahahah
    /**
     * the interestpoint_id is a foreign key in both this table and the twitteruser table
     * so when we delete a PI we should be sure that we already had deleted all its occurence
     * in this tables
     * 
     * 
     * deleting PI reference from Hashtag table
     */
    public void deleteInterestPointFromHashtagsToAvoidCompilationErrors(int id) {
    	Connection connection = SingletonDBConnection.getInstance();
    	
    	try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM hashtag WHERE interestpoint_id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    /**
     * the interestpoint_id is a foreign key in both this table and the twitteruser table
     * so when we delete a PI we should be sure that we already had deleted all its occurence
     * in this tables
     * 
     * 
     * deleting PI reference from twitteruser table
     */
    public void deleteInterestPointFromTwitterUsersToAvoidCompilationErrors(int id) {
    	Connection connection = SingletonDBConnection.getInstance();
    	
    	try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM twitteruser WHERE interestpoint_id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /** 
     * deleting PI from the Database
     */
    public void deleteSelectedInterestPointById(int id) {
    	Connection connection = SingletonDBConnection.getInstance();
		deleteInterestPointFromHashtagsToAvoidCompilationErrors(id);
		deleteInterestPointFromTwitterUsersToAvoidCompilationErrors(id);

    	try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM interestpoint WHERE interestpoint_id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * We save a User in the DB in the new table created
     * so we could accelerate the research 
     * 
     
     */
    
    public User saveUser(User user,String parsedData) {
    	Connection connection = SingletonDBConnection.getInstance();
        try {
            String Query = "INSERT INTO usercached (user_id,userScreenName,data) "
                    + "VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getScreen_name());
            // the parsedData will contain the jsonformat parsed into a string
            preparedStatement.setString(3, parsedData);

            preparedStatement.executeUpdate();
            
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    /**
     * we're getting a user from the DB if the user exist already we're returning the user
     * else we're returning null
     */
    public User getUserFromDatabase(String screen_name) {
    	
    	Connection connection = SingletonDBConnection.getInstance();
    	User user = null;
    	Gson gson = new Gson();
    	
    	try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM usercached WHERE userScreenName = ? ");
			ps.setString(1, screen_name);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				user = new User();
				user = gson.fromJson(rs.getString("data"), User.class);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return user;
    }

}
