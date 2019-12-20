/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.repository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.jfoenix.controls.JFXButton;

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
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
			/*
			 * String sql = "select * from usercached";
			 * 
			 * ResultSet rs = stmt.executeQuery(sql);
			 * 
			 * while(rs.next()) { System.out.println("id: "+rs.getLong("user_id"));
			 * System.out.println("screenname: "+rs.getString("userScreenName"));
			 * System.out.println("data : "+rs.getString("data")); }
			 */
	         
	         // if we run this class we're going to get the data of a user who has been cached
	         
	         InterestPointDAO dao = new InterestPointDAO();
	         User user = dao.getUserFromDatabase("realDonaldTrump");
	         System.out.println(user.getId()+"\n"+user.getName());
	         System.out.println(user.getDescription());
	         
	         
	         
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
