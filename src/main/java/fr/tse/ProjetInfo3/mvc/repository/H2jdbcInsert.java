/**
 *
 */
package fr.tse.ProjetInfo3.mvc.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dao.LoginAppDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;

/**
 * @author Laïla
 *
 */
public class H2jdbcInsert {

    public static void main(String[] args) {
        insertUser();
    }


    public static void insertSomething() {
        Connection conn = null;
        Statement stmt = null;
        try {

            // STEP 2: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = SingletonDBConnection.getInstance();
            System.out.println("Connected database successfully...");

            
            //stmt.executeUpdate(sql2);
            System.out.println("Inserted records into the table...");

            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch (
                Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        System.out.println("Goodbye!");
    }
    
    public static void insertUser() {
    	 Connection conn = null;
         Statement stmt = null;
         try {

             // STEP 2: Open a connection
             System.out.println("Connecting to a selected database...");
             conn = SingletonDBConnection.getInstance();
             System.out.println("Connected database successfully...");

             /*// STEP 3: Execute a query
             stmt = conn.createStatement();
             // testing the storing process we have made

             LoginAppDAO dao = new LoginAppDAO();
             UserApp user = new UserApp("Bob", "BOB","bob","bob@gmail.com");
            
             dao.saveUser(user);

             //stmt.executeUpdate(sql2);
             System.out.println("Inserted records into the table...");
*/
             
             

             // STEP 3: Execute a query
             stmt = conn.createStatement();

             InterestPointDAO dao = new InterestPointDAO();
             // testing the storing process we have made
             RequestManager manager = new RequestManager();
             
             User user = manager.getUser("realdonaldtrump");
             Gson gson = new Gson();
             String userJson = gson.toJson(user);
             
             dao.saveUser(user, userJson);
             // STEP 4: Clean-up environment
             stmt.close();
             conn.close();
         } catch (
                 Exception e) {
             // Handle errors for Class.forName
             e.printStackTrace();
         } finally {
             // finally block used to close resources
             try {
                 if (stmt != null) stmt.close();
             } catch (SQLException se2) {
             } // nothing we can do
             try {
                 if (conn != null) conn.close();
             } catch (SQLException se) {
                 se.printStackTrace();
             } // end finally try
         } // end try
         System.out.println("Goodbye!");
    }

}
