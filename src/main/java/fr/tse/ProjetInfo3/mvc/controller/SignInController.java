/**
 *
 */
package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.UserApp;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

    private MainController mainController;
    private TabPane tabPane;
    private Tab tab;

    private DatabaseManager databaseManager;

    @FXML
    private JFXTextField mail;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField username;
    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXButton signinButton;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        databaseManager = new DatabaseManager();
    }

    public void injectTabContainer(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void injectTab(Tab tab) {
        this.tab = tab;
    }

    @FXML
    private void loginButtonPressed(ActionEvent event) {
        mainController.closeCurrentTab();
        mainController.goToLoginPane();
    }

    @FXML
    private void signinButtonpressed(ActionEvent event) {
        String userName = username.getText();
        String mailField = mail.getText();
        String passwordField = password.getText();

        UserApp userApp = new UserApp(userName, passwordField, mailField);

        if (databaseManager.addNewUserToDataBase(userApp)) {
            Label headerLabel = new Label("Création de compte réussi");
            Text bodyText = new Text("Votre compte a été crée."
                    + "\nVous pouvez dès à présent vous connecter et commencer à utiliser des fonctionnalités avancées de Twiter Analytics."
                    + "\nVotre login est : " + userName);
            JFXButton button = new JFXButton("D'accord");

            BoxBlur blur = new BoxBlur(3, 3, 3);

            button.getStyleClass().add("dialog-button");
            headerLabel.getStyleClass().add("dialog-header-success");
            bodyText.getStyleClass().add("dialog-text");
            bodyText.setWrappingWidth(500);

            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setPadding(new Insets(10));
            JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
                dialog.close();
                tabPane.getTabs().remove(tab);
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
        } else {
            Label headerLabel = new Label("Erreur");
            Text bodyText = new Text("Identifiant ou mot de passe incorrect");
            JFXButton button = new JFXButton("Fermer");

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

        }

    }
}

