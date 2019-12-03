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
	         String sql = "SELECT * FROM interestpoint  "; 
	         
	         ResultSet rs = stmt.executeQuery(sql); 
	         List<String> hashtags = new ArrayList<>();
	         InterestPointDAO dao = new InterestPointDAO();
	         // STEP 4: Extract data from result set 
//	         while(rs.next()) {
//	        	 System.out.println("name: "+rs.getString("name"));
//	        	 System.out.println("description: "+rs.getString("description"));
//	        	 System.out.println("created_at: "+rs.getDate("created_at"));
//	         }
	         List<InterestPoint> interestPoints = dao.getAllInterestPoints();
	         
	         for(InterestPoint i : interestPoints) {
	        	 System.out.println("name : "+i.getName());
	        	 System.out.println("description : "+i.getDescription());
	        	 for(Hashtag h : i.getHashtags()) {
	        		 System.out.println("hash : "+h.getHashtag());
	        	 }
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
