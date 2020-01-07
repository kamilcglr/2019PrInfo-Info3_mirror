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
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;

/**
 * @author Laïla
 *
 */
public class FavsDAO {

	public User saveFavouriteUser(User user,int userID) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TWITTERUSERFAVS(user_id,userName,userScreenName,favourite) " +
            		 "VALUES (?,?,?,?)");
            preparedStatement.setInt(1,userID);   
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getScreen_name());
            preparedStatement.setInt(4, 1);
            preparedStatement.executeUpdate();

            System.out.println("added"+user.getName());
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
	public Hashtag saveFavouriteHashtag(Hashtag hashtag,int userID) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hashtagfavs(user_id,hashtag,favourite) "
                    + "VALUES (?,?,?)");
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, hashtag.getHashtag());
            preparedStatement.setInt(3, 1);
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
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TWITTERUSERFAVS(hashtag,favourite) "
            		                    + "VALUES (?,?)");

            preparedStatement.setString(1, user.getName());
            preparedStatement.executeUpdate();

            //preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
	public List<User> getFavouriteUsers(int userID) throws Exception {
		UserViewer userViewer=new UserViewer();
		 Connection connection = SingletonDBConnection.getInstance();
		 List<User> users=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TWITTERUSERFAVS WHERE favourite=1 and user_id=?");
	            preparedStatement.setInt(1, userID);
	            ResultSet rs = preparedStatement.executeQuery();
	            while (rs.next()) {
	            	User user=userViewer.searchScreenNameU(rs.getString("userScreenName"));
		                users.add(user);
		               
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return users;
	}
	public List<Hashtag> getFavouriteHashtags(int userID) {
		 Connection connection = SingletonDBConnection.getInstance();
		 List<Hashtag> hashtags=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hashtagfavs WHERE favourite=1 and user_id=?");
	            preparedStatement.setInt(1, userID);
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
	public int checkFavHashtag(Hashtag hashtag,int userID) {
		Connection connection = SingletonDBConnection.getInstance();
		 List<Hashtag> hashtags=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hashtagfavs WHERE hashtag=? and user_id=?");
	            preparedStatement.setString(1, hashtag.getHashtag());
	            preparedStatement.setInt(2, userID);

	            ResultSet rs = preparedStatement.executeQuery();
	           if (rs.next()) {
		            
		            return 1;
	           }
	           else {
	        	   
		            return 0;

	           }

	}catch (SQLException e) {
       e.printStackTrace();
       return 0;
	}
	}
	public void addFavHash(Hashtag hashtag,int userID) {
		Connection connection = SingletonDBConnection.getInstance();
		 List<Hashtag> hashtags=new ArrayList<>();
		 int check=checkFavHashtag(hashtag,userID);
	        try {
	           if (check==1) {
		            PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM hashtagfavs WHERE hashtag=? and user_id=?");
		            prepStatement.setString(1, hashtag.getHashtag());
		            prepStatement.setInt(2, userID);

		            prepStatement.executeUpdate();
		            System.out.println(hashtag.getHashtag()+" deleted!");
	           }
	           else {
	        	   saveFavouriteHashtag(hashtag,userID);
		            System.out.println(hashtag.getHashtag()+" saved!");

	           }

	}catch (SQLException e) {
        e.printStackTrace();
    }
}
	public void addFavUser(User user,int userID) {
		Connection connection = SingletonDBConnection.getInstance();
		 int check=checkFavUser(user,userID);
	        try {
	        	if(check==1) {
		            PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM TWITTERUSERFAVS WHERE userScreenName=? and userID=?");
		            prepStatement.setString(1,user.getScreen_name());
		            prepStatement.setInt(2, userID);

		            prepStatement.executeUpdate();
		            System.out.println(user.getScreen_name()+" deleted!");
		          
	           }
	           else {
	        	   saveFavouriteUser(user,userID);
		            System.out.println(user.getScreen_name()+" saved!");
		           

	           }

	}catch (SQLException e) {
       e.printStackTrace();
   }
}
	
	public int checkFavUser(User user,int userID) {
		Connection connection = SingletonDBConnection.getInstance();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TWITTERUSERFAVS WHERE userScreenName=? and user_id=?");
	            preparedStatement.setInt(2, userID);
	            preparedStatement.setString(1, user.getScreen_name());
	            ResultSet rs = preparedStatement.executeQuery();
	           if (rs.next()) {
		           
		            return 1;
	           }
	           else {
	        	  
		            return 0;

	           }

	}catch (SQLException e) {
        e.printStackTrace();
    }
			return 0;
}
}
