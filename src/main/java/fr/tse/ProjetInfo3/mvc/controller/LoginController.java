package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.sql.*;

/**
 * @author Laïla
 */
public class LoginController {
    private MainController mainController;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton validateButton;
    @FXML
    private JFXButton signinButton;

    @FXML
    private JFXTextField identifiantField;
    @FXML
    private JFXPasswordField passwordField;

    ////////
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/user";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";


    public static int connected = 0;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void signinButtonpressed(ActionEvent event) {
        mainController.goToSigninTab();
    }

    @FXML
    private void validateButtonPressed(ActionEvent event) {

        //Identifiant de connexion
        String identifiant = identifiantField.getText();
        String password = passwordField.getText();

        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "SELECT mail, password FROM userApp where mail='" + identifiant + "'" + " and password= '" + password + "'";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            if (rs.next()) {
                connected = 1;
                Label headerLabel = new Label("Connection réussie");
                Text bodyText = new Text("Vous êtes connecté. Vous avez accès aux fonctionnalités avancées dans le menu principal.");
                JFXButton button = new JFXButton("D'accord");

                BoxBlur blur = new BoxBlur(3, 3, 3);

                button.getStyleClass().add("dialog-button");
                headerLabel.getStyleClass().add("dialog-header-success");
                bodyText.getStyleClass().add("dialog-text");

                JFXDialogLayout dialogLayout = new JFXDialogLayout();
                dialogLayout.setPadding(new Insets(10));
                JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
                    dialog.close();
                    mainController.goToHomeRefresh();


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
                System.out.println("ok" + identifiant + " " + password + "\n" + rs);

            } else {
                Label headerLabel = new Label("Erreur");
                Text bodyText = new Text("Identifiant ou mot de passe incorrect");
                JFXButton button = new JFXButton("D'accord");

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
                System.out.println("ok" + identifiant + " " + password + "\n" + rs);
            }

            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
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

    public JFXTextField getIdentifiantField() {
        return identifiantField;
    }

    public void setIdentifiantField(JFXTextField identifiantField) {
        this.identifiantField = identifiantField;
    }

    public JFXPasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(JFXPasswordField passwordField) {
        this.passwordField = passwordField;
    }
}

	    


