/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.services;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML; 
/**
 * @author Laïla
 *
 */
public class H2jdbcRead {
	   // JDBC driver name and database URL 
	   static final String JDBC_DRIVER = "org.h2.Driver";   
	   static final String DB_URL = "jdbc:h2:~/user";  
	   
	   //  Database credentials 
	   static final String USER = "sa"; 
	   static final String PASS = ""; 
	   
	   //JAVAFX
	   @FXML
	   private JFXButton loginButton;
	   
	   @FXML
	   
	   public static void main(String[] args) { 
	      Connection conn = null; 
	      Statement stmt = null; 
	      try { 
	         // STEP 1: Register JDBC driver 
	         Class.forName(JDBC_DRIVER); 
	         
	         // STEP 2: Open a connection 
	         System.out.println("Connecting to database..."); 
	         conn = DriverManager.getConnection(DB_URL,USER,PASS);  
	         
	         // STEP 3: Execute a query 
	         System.out.println("Connected database successfully..."); 
	         stmt = conn.createStatement(); 
	         String sql = "SELECT id, mail, twitter, password FROM userApp"; 
	         ResultSet rs = stmt.executeQuery(sql); 
	         
	         // STEP 4: Extract data from result set 
	         while(rs.next()) { 
	            // Retrieve by column name 
	            int id  = rs.getInt("id"); 
	            String mail = rs.getString("mail"); 
	            String twitter = rs.getString("twitter"); 
	            String password = rs.getString("password");  
	            
	            // Display values 
	            System.out.print("ID: " + id); 
	            System.out.print(", Mail: " + mail); 
	            System.out.print(", Twitter: " + twitter); 
	            System.out.println(", Password: " + password); 
	         } 
	         // STEP 5: Clean-up environment 
	         rs.close(); 
	      } catch(SQLException se) { 
	         // Handle errors for JDBC 
	         se.printStackTrace(); 
	      } catch(Exception e) { 
	         // Handle errors for Class.forName 
	         e.printStackTrace(); 
	      } finally { 
	         // finally block used to close resources 
	         try { 
	            if(stmt!=null) stmt.close();  
	         } catch(SQLException se2) { 
	         } // nothing we can do 
	         try { 
	            if(conn!=null) conn.close(); 
	         } catch(SQLException se) { 
	            se.printStackTrace(); 
	         } // end finally try 
	      } // end try 
	      System.out.println("Goodbye!"); 
	   }
}
