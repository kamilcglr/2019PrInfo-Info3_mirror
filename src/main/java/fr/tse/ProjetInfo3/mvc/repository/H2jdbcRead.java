/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.repository;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import javafx.fxml.FXML; 
/**
 * @author Laïla
 *
 */
public class H2jdbcRead {
	   
	   //JAVAFX
	   @FXML
	   private JFXButton loginButton;
	   
	   @FXML
	   
	   public static void main(String[] args) { 
	      Connection conn = null; 
	      Statement stmt = null; 
	      try { 
	         
	         
	         // STEP 2: Open a connection 
	         System.out.println("Connecting to database..."); 
	         conn = SingletonDBConnection.getInstance();  
	         
	         // STEP 3: Execute a query 
	         System.out.println("Connected database successfully..."); 
	         stmt = conn.createStatement(); 
	         String sql = "SELECT * FROM interestpoint"; 
	         ResultSet rs = stmt.executeQuery(sql); 
	         
	         // STEP 4: Extract data from result set 
	         while(rs.next()) { 
	            // Retrieve by column name 
	            int id  = rs.getInt("interestpoint_id"); 
	            String mail = rs.getString("name"); 
	            String twitter = rs.getString("description"); 
	            String password = rs.getString("created_at");  
	            
	            System.out.println("id : "+id);
	            System.out.println("name : "+mail);
	            System.out.println("description : "+twitter);
	            System.out.println("created at : "+password);
	            
	         } 
	         // STEP 5: Clean-up environment 
	         rs.close(); 
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
	   }
}
