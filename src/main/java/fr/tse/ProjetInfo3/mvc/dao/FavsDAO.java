/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.tse.ProjetInfo3.mvc.dto.Favourite;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.SingletonDBConnection;

/**
 * @author Laïla
 *
 */
public class FavsDAO {

	public User saveFavouriteUser(User user) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO twitteruserfav(userName,userScreenName,favourite) " + 
            		 "VALUES (?,?,?)");
                    
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getScreen_name());
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            System.out.println("added"+user.getName());
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
	public Hashtag saveFavouriteHashtag(Hashtag hashtag) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hashtagfavs(hashtag,favourite) "
                    + "VALUES (?,?)");
                    
            preparedStatement.setString(1, hashtag.getHashtag());
            preparedStatement.setInt(2, 1);
            preparedStatement.executeUpdate();

            System.out.println("added "+hashtag.getHashtag());
            //preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtag;
    }
	public User deleteFavouriteUser(User user) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO twitteruserfav(hashtag,favourite) " 
            		                    + "VALUES (?,?)");

            preparedStatement.setString(1, user.getName());
            preparedStatement.executeUpdate();

            //preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
	public List<User> getFavouriteUsers() {
		 Connection connection = SingletonDBConnection.getInstance();
		 List<User> users=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM twitteruserfav WHERE favourite=1");
	            ResultSet rs = preparedStatement.executeQuery();
	            while (rs.next()) {
	            	User user=new User(0, null, null, null, null, null, null, 0, 0, 0, 0, 0, null, null, null, null);
		                user.setScreen_name(rs.getString("userScreenName"));
		                user.setName(rs.getString("userName"));
		                users.add(user);
		               
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return users;
	}
	public List<Hashtag> getFavouriteHashtags() {
		 Connection connection = SingletonDBConnection.getInstance();
		 List<Hashtag> hashtags=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hashtagfavs WHERE favourite=1");
	            ResultSet rs = preparedStatement.executeQuery();
	            while (rs.next()) {
	            	Hashtag hashtag=new Hashtag();
		                hashtag.setHashtag(rs.getString("hashtag"));
		                System.out.println(hashtag.getHashtag());
		                hashtags.add(hashtag);
		               
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return hashtags;
	}
}
