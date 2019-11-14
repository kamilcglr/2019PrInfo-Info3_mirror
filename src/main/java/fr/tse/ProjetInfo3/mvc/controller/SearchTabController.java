package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.controls.JFXSnackbar;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
//import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
     * We re-declare fxml variable here (fx:id="hashtagToggle"), with the SAME name ot use them
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

    //@FXML
    //private JFXSpinner progressIndicator;

    @FXML
    private Label progressLabel;

    @FXML
    private JFXButton loginButton;

    @FXML
    private JFXButton signinButton;

    private String research;

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        //!!!!!!!!!!!!!!!!Hide unused objects !!!!!!!!!!!!!!!!
        loginButton.setVisible(false);
        signinButton.setVisible(false);


        //Disable the text field, we wait for the at least one toggle to be pressed
        activateField(false, true);
        //progressIndicator.setVisible(false);
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

            activateField(true, true);
        } else {
            hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            searchField.setLabelFloat(false);

            activateField(false, true);
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

            activateField(true, true);
        } else {
            userIcon.setIconColor(Paint.valueOf("#48ac98ff"));
            searchField.setLabelFloat(false);

            activateField(false, true);
        }
    }

    @FXML
    public void loginButtonPressed(ActionEvent event) {
        mainController.goToLoginPane();
    }

    /*
     * 1. Verify that there is something in search bar
     * 2. Call search
     * */
    @FXML
    private void searchButtonPressed(ActionEvent event){
        //get the content of the field
        String research = searchField.getText();
        //u for user, h for hastag
        char typeOfSearch = 'e';
        if (hashtagToggle.isSelected()) {
            typeOfSearch = 'h';
        } else if (userToggle.isSelected()) {
            typeOfSearch = 'u';
        } else {
            //TODO create error here
        }
        //verify if it is empty or contains only the @/#
        if (research.length() <= 1) {
            launchDialog("Aucune saisie", "Veuillez saisir au moins un caractère", "D'accord");
        } else {
            progressLabel.setVisible(true);
            progressLabel.setText("Recherche en cours");
            searchIsRunning(true);
            launchSearch(research, typeOfSearch);
        }
    }

    @FXML
    /**
     * If user pressed enter key in the search field, we call searchButtonPressed()
     */
    private void onEnter(ActionEvent event) {
        searchButtonPressed(event);
    }

    /**
     * Create a new thread that will do the search
     * If there is not error during search, it calls the maincontroller to go to userPane
     * Else we print error
     *
     * @param research:string text entered by user
     * @param typeOfSearch:   char type of search h for hastag, u for user
     */
    private void launchSearch(String research, char typeOfSearch) {
        /*
         * It permits to do the search in a separated thread, so the interface does not freeze
         * */
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    //if search does not throw error
                    if (typeOfSearch == 'h') {
                        HastagViewer hastagViewer = new HastagViewer();
                        hastagViewer.searchHashtag(research);

                        //we go to this part when hashtag exists, else Exception is thrown
                        progressLabel.setVisible(false);
                        mainController.goToHashtagPane(hastagViewer);

                    } else if (typeOfSearch == 'u') {
                        UserViewer userViewer = new UserViewer();
                        userViewer.searchScreenName(research);

                        //we go to this part when user exists, else Exception is thrown
                        progressLabel.setVisible(false);
                        mainController.goToUserPane(userViewer);
                    }
                    searchIsRunning(false);
                } catch (Exception e) {
                    //Most of the time we will catch Exception from Twitter4j
                    //Stop animations
                    searchIsRunning(false);

                    //this is necessary to update the ui because we are in a separated thread
                    Platform.runLater(() -> {
                        //hide progress label because we will use snackbar
                        progressLabel.setVisible(false);
                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.getStyleClass().add("snackbar");

                        if (e instanceof RequestManager.RequestManagerException) {
                            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Désolé, l'utilisateur " + research + " n'existe pas.", "D'accord", b -> snackbar.close())));
                        } else {
                            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Désolé, la recherche n'a pas aboutie", "D'accord", b -> snackbar.close())));
                        }
                    });
                    System.out.println("Something went wrong : " + e);
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
     * Shows the label and animation during searching
     *
     * @param searching true if searching
     */
    private void searchIsRunning(boolean searching) {
        if (searching) {
         //   progressIndicator.setVisible(true);
            searchButton.setVisible(false);
            activateField(false, false);
        } else {
           // progressIndicator.setVisible(false);
            searchButton.setVisible(true);
            activateField(true, false);
        }

    }

    /**
     * Desactive or active the search field and button
     *
     * @param activate : boolean, if true, activate search button and field
     */
    private void activateField(boolean activate, boolean delete) {
        if (activate) {
            searchField.setDisable(false);
            searchButton.setDisable(false);
        } else {
            searchField.setDisable(true);
            searchButton.setDisable(true);
        }
        if (delete) {
            searchField.setText("");
        }
    }


}
