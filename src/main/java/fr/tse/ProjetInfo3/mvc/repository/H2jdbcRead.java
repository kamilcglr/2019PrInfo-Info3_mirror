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

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
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
	         String sql = "select * from twitteruser where interestpoint_id = 1";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         
	         while(rs.next()) {
	        	 System.out.println("id: "+rs.getInt("interestpoint_id"));
	        	 System.out.println("name : "+rs.getString("name"));
	        	 System.out.println("description : "+rs.getString("description"));
	         }
	         
	         
	         System.out.println("deleted successfully");
	         
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
