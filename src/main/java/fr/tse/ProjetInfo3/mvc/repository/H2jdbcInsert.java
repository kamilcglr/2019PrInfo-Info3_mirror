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

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint; 
/**
 * @author Laïla
 *
 */
public class H2jdbcInsert {
	   
	   public static void main(String[] args) { 
	      Connection conn = null; 
	      Statement stmt = null; 
	      try{
	        
	         // STEP 2: Open a connection 
	         System.out.println("Connecting to a selected database..."); 
	         conn = SingletonDBConnection.getInstance();
	         System.out.println("Connected database successfully..."); 
	         
	         // STEP 3: Execute a query 
	         stmt = conn.createStatement();  

	  
	         
	         InterestPointDAO dao = new InterestPointDAO();
	         InterestPoint ip = new InterestPoint("Santé","description sur la santé",new Date(10000));
	         List<Hashtag> hashtags = new ArrayList<>();
	         
	         Hashtag president = new Hashtag("#president");
	         Hashtag congres = new Hashtag("#congrés");
	         Hashtag meetup = new Hashtag("#meetup");

	         hashtags.add(president);
	         hashtags.add(congres);
	         hashtags.add(meetup);
	         
	         ip.setHashtags(hashtags);
	         dao.saveInterestPoint(ip);
	         //stmt.executeUpdate(sql2); 
	         System.out.println("Inserted records into the table..."); 
	         
	         // STEP 4: Clean-up environment 
	         stmt.close(); 
	         conn.close(); 
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
