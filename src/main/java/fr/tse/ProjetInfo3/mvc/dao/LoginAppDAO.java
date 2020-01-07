package fr.tse.ProjetInfo3.mvc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;
import fr.tse.ProjetInfo3.mvc.repository.SingletonDBConnection;

/**
 * @author Laïla
 *
 */
public class LoginAppDAO {

	  /**
     * in this method we're saving an interestpoint into the DB using singleton pattern to have one instance
     * accessing the DB
     */
    public UserApp saveUser(UserApp userApp) {
        Connection connection = SingletonDBConnection.getInstance();
        try {
            String Query = "INSERT INTO userApp(username,mail,twitter, password) VALUES (?,?,?,?)";
                    
            //Statement.RETURN_GENERATED_KEYS to get the id of inserted element
            PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, userApp.getUsername());
            preparedStatement.setString(2, userApp.getMail());
            preparedStatement.setString(3, userApp.getTwitterName());
            preparedStatement.setString(4, userApp.getPassword());
            

            preparedStatement.executeUpdate();

           
    }
        catch (Exception e) {
            e.printStackTrace();
		}
        return userApp;

    }
}
