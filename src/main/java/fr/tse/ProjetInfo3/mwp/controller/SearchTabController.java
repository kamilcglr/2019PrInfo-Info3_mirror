package fr.tse.ProjetInfo3.mwp.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.validation.RequiredFieldValidator;
import fr.tse.ProjetInfo3.mwp.Main;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;
import fr.tse.ProjetInfo3.mwp.viewer.UserViewer;
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
        //Disable the text field, we wait for the at least one toggle to be pressed
        activateField(false);
        /*
         * When the text in the input field is changed,
         * we constantly remove spaces and add the # or @ at the begining
         */
      searchField.textProperty().addListener(
              (observable, old_value, new_value) -> {
                  if (new_value.contains(" ")) {
                      searchField.setText(old_value);
                  }
                  if (hashtagToggle.isSelected()) {
                      if (searchField.getText().isEmpty() || !searchField.getText(0, 1).equals("#")) {
                          searchField.setText("#" + new_value);
                      }
                  } else if (userToggle.isSelected()) {
                      if (searchField.getText().isEmpty() || !searchField.getText(0, 1).equals("@")) {
                          searchField.setText("@" + new_value);
                      }
                  }
              }
      );
    }

    /*Only one Toggle can be pressed, so we change the color of the second Toggle */
    @FXML
    private void hashtagTogglePressed(ActionEvent event) {
        /*if the toggle is selected, we change the the color of the toggle
         * else, this toggle is unselected, then we change the color of this icon*/
        if (hashtagToggle.isSelected()) {
            userToggle.setSelected(false);
            hashtagIcon.setIconColor(Paint.valueOf("#ffffff"));
            userIcon.setIconColor(Paint.valueOf("#48ac98ff"));

            //Set floating label to help the user
            searchField.setLabelFloat(true);
            searchField.setPromptText("Entrez le hashtag # que vous souhaitez chercher");

            activateField(true);
        } else {
            hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            searchField.setLabelFloat(false);

            activateField(false);
        }

    }

    /*Only one Toggle can be pressed, so we change the color of the second Toggle */
    @FXML
    private void userTogglePressed(ActionEvent event) {
        /*if the toggle is selected, we change the the color
         * else, this toggle is unselected, then we change the color of this icon*/
        if (userToggle.isSelected()) {
            userIcon.setIconColor(Paint.valueOf("#ffffff"));
            hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            hashtagToggle.setSelected(false);

            //Set floating label to help the user
            searchField.setLabelFloat(true);
            searchField.setPromptText("Entrez l'identifiant @ de l'user que vous souhaitez chercher");

            activateField(true);
        } else {
            userIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            searchField.setLabelFloat(false);

            activateField(false);
        }

    }

    /*
     * 1. Verify if at least one toggle option is set
     * 2. Give hand to search function
     * */
    @FXML
    private void searchButtonPressed() throws IOException {
        //get the content of the fied
        String research = searchField.getText();

        //verify if it is empty or contains only the @/#
        if (research.length() <= 1) {
            launchDialog("Aucune saisie", "Veuillez entrer quelque chose à chercher", "D'accord");
        } else {
            UserViewer userViewer = new UserViewer();
            try {
                //if search does not throw error
                userViewer.searchId(research);
                mainController.goToSearchPane();

            } catch (Exception e) {
                //else we print error on dialog
                System.out.println("Something went wrong : " + e);
            }
        }
    }

    /**
     * Launch dialog to inform the user
     *
     * @param header      label of the header
     * @param text        content printed inside
     * @param labelButton label inside of button
     */
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

    /**
     * Desactive of active the search field and button
     *
     * @param activate : boolean, if true, activate search button and field
     */
    private void activateField(boolean activate) {
        if (activate) {
            searchField.setDisable(false);
            searchButton.setDisable(false);
        } else {
            searchField.setDisable(true);
            searchButton.setDisable(true);
        }
        searchField.setText("");
    }

}
