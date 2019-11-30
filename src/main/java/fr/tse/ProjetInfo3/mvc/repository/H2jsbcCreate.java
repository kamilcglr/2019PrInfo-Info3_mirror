/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.repository;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.sql.Statement;  

/**
 * @author Laïla
 *
 */
public class H2jsbcCreate {  
	   public static void main(String[] args) { 
	      Connection conn = null; 
	      Statement stmt = null; 
	      try { 
	   
	         //STEP 2: Open a connection 
	         System.out.println("Connecting to database..."); 
	         conn = SingletonDBConnection.getInstance();
	         
	         //STEP 3: Execute a query 
	         System.out.println("Creating table in given database..."); 
	         stmt = conn.createStatement(); 
	         String userapp =  "CREATE TABLE   userApp " + 
	            "(id INTEGER not NULL, " + 
	            " mail VARCHAR(255), " +  
	            " twitter VARCHAR(255), " +  
	            " password VARCHAR(20), " +  
	            " PRIMARY KEY ( id ))";
	         
	         String interestpoint =  "CREATE TABLE   interestpoint " + 
		 	            "(interestpoint_id INTEGER AUTO_INCREMENT,"
		 	            + "name VARCHAR(255) , " + 
		 	            " description VARCHAR(255), " +  
		 	            " created_at DATE,"
		 	            + "PRIMARY KEY (interestpoint_id))";
	         
	         String hashtag = "CREATE TABLE   hashtag " + 
		 	            "( hashtag_id INTEGER AUTO_INCREMENT,"
		 	            + "hashtag VARCHAR(255),"
		 	            + "PRIMARY KEY (hashtag_id),"
		 	            + "interestpoint_id INTEGER,"
		 	            + "CONSTRAINT FK_hashtag_interestpoint FOREIGN KEY (interestpoint_id) REFERENCES interestpoint(interestpoint_id))";
	         
	         
	         stmt.executeUpdate(interestpoint);
	         stmt.executeUpdate(hashtag);
	         
	         //stmt.executeUpdate("drop table interestpoint");
	         //stmt.executeUpdate("drop table hashtag");
	         
	         System.out.println("Created tables in given database..."); 
	         
	         // STEP 4: Clean-up environment 
	         stmt.close(); 
	         conn.close(); 
	      }  catch(Exception e) { 
	         //Handle errors for Class.forName 
	         e.printStackTrace(); 
	      } finally { 
	         //finally block used to close resources 
	         try{ 
	            if(stmt!=null) stmt.close(); 
	         } catch(SQLException se2) { 
	         } // nothing we can do 
	         try { 
	            if(conn!=null) conn.close(); 
	         } catch(SQLException se){ 
	            se.printStackTrace(); 
	         } //end finally try 
	      } //end try 
	      System.out.println("Goodbye!");
	   } 
}
