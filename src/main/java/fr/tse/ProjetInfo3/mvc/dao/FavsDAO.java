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

	public User saveFavouriteUser(User user) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TWITTERUSERFAVS(userName,userScreenName,favourite) " +
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
	public List<User> getFavouriteUsers() throws Exception {
		UserViewer userViewer=new UserViewer();
		 Connection connection = SingletonDBConnection.getInstance();
		 List<User> users=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TWITTERUSERFAVS WHERE favourite=1");
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
	public int checkFavHashtag(Hashtag hashtag) {
		Connection connection = SingletonDBConnection.getInstance();
		 List<Hashtag> hashtags=new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hashtagfavs WHERE hashtag=?");
	            preparedStatement.setString(1, hashtag.getHashtag());
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
	public void addFavHash(Hashtag hashtag) {
		Connection connection = SingletonDBConnection.getInstance();
		 List<Hashtag> hashtags=new ArrayList<>();
		 int check=checkFavHashtag(hashtag);
	        try {
	           if (check==1) {
		            PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM hashtagfavs WHERE hashtag=?");
		            prepStatement.setString(1, hashtag.getHashtag());
		            prepStatement.executeUpdate();
		            System.out.println(hashtag.getHashtag()+" deleted!");
	           }
	           else {
	        	   saveFavouriteHashtag(hashtag);
		            System.out.println(hashtag.getHashtag()+" saved!");

	           }

	}catch (SQLException e) {
        e.printStackTrace();
    }
}
	public void addFavUser(User user) {
		Connection connection = SingletonDBConnection.getInstance();
		 int check=checkFavUser(user);
	        try {
	        	if(check==1) {
		            PreparedStatement prepStatement = connection.prepareStatement("DELETE FROM TWITTERUSERFAVS WHERE userScreenName=?");
		            prepStatement.setString(1,user.getScreen_name());
		            prepStatement.executeUpdate();
		            System.out.println(user.getScreen_name()+" deleted!");
		          
	           }
	           else {
	        	   saveFavouriteUser(user);
		            System.out.println(user.getScreen_name()+" saved!");
		           

	           }

	}catch (SQLException e) {
       e.printStackTrace();
   }
}
	
	public int checkFavUser(User user) {
		Connection connection = SingletonDBConnection.getInstance();
	        try {
	            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TWITTERUSERFAVS WHERE userScreenName=?");
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
