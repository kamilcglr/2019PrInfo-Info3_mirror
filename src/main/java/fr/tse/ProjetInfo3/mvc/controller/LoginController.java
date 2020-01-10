package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.xml.crypto.Data;
import java.sql.*;

/**
 * @author Laïla
 */
public class LoginController {
    private MainController mainController;
    private DatabaseManager databaseManager;
    private FavsViewer favsViewer;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton validateButton;

    @FXML
    private JFXTextField userNameField;
    @FXML
    private JFXPasswordField passwordField;


    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController, DatabaseManager databaseManager, FavsViewer favsViewer) {
        this.mainController = mainController;
        this.databaseManager = databaseManager;
        this.favsViewer = favsViewer;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void signinButtonpressed(ActionEvent event) {
        mainController.closeCurrentTab();
        mainController.goToSigninTab();
    }

    @FXML
    private int validateButtonPressed(ActionEvent event) {

        String username = userNameField.getText();
        String password = passwordField.getText();

        UserApp userApp = databaseManager.getUserFromDataBase(username, password);
        if (userApp != null) {
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
                mainController.setConnected(true);
                mainController.setUserApp(userApp);
                favsViewer.setUserID(userApp.getId());
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

            //Handle shortcut
            mainController.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F, KeyCombination.ALT_ANY),
                    new Runnable() {
                        @Override
                        public void run() {
                            mainController.goToMyFavsPane();
                        }
                    }
            );
            mainController.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_ANY),
                    new Runnable() {
                        @Override
                        public void run() {
                            mainController.goToMyPisPane();
                        }
                    }
            );

            return 0;
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
            return 0;
        }
    }

    @FXML
    private void onEnter(ActionEvent event) {
        validateButtonPressed(event);
    }
}

	    


