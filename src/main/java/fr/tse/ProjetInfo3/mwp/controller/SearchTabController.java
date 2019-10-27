package fr.tse.ProjetInfo3.mwp.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import fr.tse.ProjetInfo3.mwp.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.Icon;

import java.io.IOException;


/**
 * @author Kamil CAGLAR
 * Controller of the SearchTab, all user interactions are handled here
 */
public class SearchTabController {
    private MainController mainController;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /*
     * We re-declare fxml variable here (fx:id="hashtagToggle"), whith the SAME name ot use them
     * */
    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXToggleNode hashtagToggle;

    @FXML
    private JFXToggleNode userToggle;

    @FXML
    private JFXTextField searchField;

    @FXML
    private Icon hashtagIcon;

    @FXML
    private Icon userIcon;

    @FXML
    private JFXButton searchButton;

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
    }

    /*Only one Toggle can be pressed, so we change the color of the second Toggle */
    @FXML
    private void hashtagTogglePressed(ActionEvent event) {
        if(hashtagToggle.isSelected()){
            hashtagIcon.setIconColor(Paint.valueOf("#ffffff"));
            userIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            userToggle.setSelected(false);
        }else{
            hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
        }

    }

    @FXML
    private void userTogglePressed(ActionEvent event) {
        if(userToggle.isSelected()){
            userIcon.setIconColor(Paint.valueOf("#ffffff"));
            hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            hashtagToggle.setSelected(false);
        }else{
            userIcon.setIconColor(Paint.valueOf("#48ac98ff"));
        }

    }

    /*
     * 1. Verify if at least one toggle option is set
     * 2. Give hand to search function
     * */
    @FXML
    private void searchButtonPressed() throws IOException {
        String research = searchField.getText();
        if (research.length() == 0) {
            launchDialog("Aucune saisie", "Veuillez entrer quelque chose à chercher", "D'accord");
        }
        if (!userToggle.isSelected() && !hashtagToggle.isSelected()) {
            launchDialog("Aucune mode de rechercher choisi", "Veuillez choisir un mode de recherche", "D'accord");
        }
    }

    /**
     * Launch dialog
     * @param header label of the header
     * @param text content printed inside
     * @param labelButton label inside of button*/
    private void launchDialog(String header, String text, String labelButton) {
        Label headerLabel = new Label(header);
        Text bodyText = new Text(text);
        JFXButton button = new JFXButton(labelButton);

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
