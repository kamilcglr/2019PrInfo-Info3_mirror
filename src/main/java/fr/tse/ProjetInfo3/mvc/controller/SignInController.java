/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTextField;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dao.LoginAppDAO;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * @author Laïla
 *
 */
public class SignInController {

	@FXML
	private JFXTextField mail;
	@FXML
	private JFXTextField password;
	@FXML
	private JFXTextField twitterName;
	  @FXML
	    private StackPane dialogStackPane;

	 	@FXML
	    private AnchorPane anchorPane;
	 	 @FXML
	     private JFXButton signinButton;
	
	 // JDBC driver name and database URL 
	   static final String JDBC_DRIVER = "org.h2.Driver";   
	   static final String DB_URL = "jdbc:h2:~/user";  
	   
	   //  Database credentials 
	   static final String USER = "sa"; 
	   static final String PASS = ""; 
	   
	   
	   public static int connected=0;
	   private MainController mainController;

 /*Controller can acces to this Tab */
 public void injectMainController(MainController mainController) {
     this.mainController = mainController;
 }
 @FXML
 private void initialize() {
 	
 }
 @FXML
 private void loginButtonPressed(ActionEvent event) {
	 mainController.goToLoginPane();
 }
 @FXML
 private void signinButtonpressed(ActionEvent event) {
 	//Identifiant de connexion
 	String mailField= mail.getText();
 	String twitterField=twitterName.getText();
 	//Password 
 	/*TO DO: make it invisible
 	 * */
 	
 	String passwordField= password.getText();
 	  Connection conn = null; 
	      Statement stmt = null; 
	      try { 
	    	  LoginAppDAO loginAppDAO=new LoginAppDAO();
	    	  UserApp userApp=new UserApp(null, passwordField, twitterField, mailField);
	    	  loginAppDAO.saveUser(userApp);
	         // STEP 4: Extract data from result set 
	        if(loginAppDAO.saveUser(userApp)!=null) { 
              connected=1;
	        	 Label headerLabel = new Label("loggedin");
	             Text bodyText = new Text("login successful");
	             JFXButton button = new JFXButton("close");

	             BoxBlur blur = new BoxBlur(3, 3, 3);

	             button.getStyleClass().add("dialog-button");
	             headerLabel.getStyleClass().add("dialog-header");
	             bodyText.getStyleClass().add("dialog-text");

	             JFXDialogLayout dialogLayout = new JFXDialogLayout();
	             dialogLayout.setPadding(new Insets(10));
	             JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
	             button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
	                 dialog.close();
	                 mainController.goToLoginPane();
	                 
	                 
	             });

	             dialogLayout.setHeading(headerLabel);
	             dialogLayout.setBody(bodyText);
	             dialogLayout.setActions(button);
	             dialog.show();
	             dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
	                 anchorPane.setEffect(null);
	             });
	             anchorPane.setEffect(blur);
	             //verification sur console
	        	//System.out.println("ok"+identifiant+" "+ password+"\n"+rs);
	        	
	         }
	        
	        else {
	        	Label headerLabel = new Label("Erreur");
	             Text bodyText = new Text("Identifiant ou mot de passe incorrect");
	             JFXButton button = new JFXButton("close");

	             BoxBlur blur = new BoxBlur(3, 3, 3);

	             button.getStyleClass().add("dialog-button");
	             headerLabel.getStyleClass().add("dialog-header");
	             bodyText.getStyleClass().add("dialog-text");

	             JFXDialogLayout dialogLayout = new JFXDialogLayout();
	             dialogLayout.setPadding(new Insets(10));
	             JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
	             button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
	                 dialog.close();
	             });

	             dialogLayout.setHeading(headerLabel);
	             dialogLayout.setBody(bodyText);
	             dialogLayout.setActions(button);
	             dialog.show();
	             dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
	                 anchorPane.setEffect(null);
	             });
	             anchorPane.setEffect(blur);
	             // verification sur console
	        	//System.out.println("ok"+identifiant+" "+ password+"\n"+rs);
	        }
	        
	         // STEP 5: Clean-up environment 
	       //  rs.close(); 
	      }catch(Exception e) { 
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

