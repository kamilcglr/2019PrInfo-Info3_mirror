package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.SearchViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.Icon;

import java.util.List;


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

   @FXML
   private JFXSpinner progressIndicator;

    @FXML
    private Label progressLabel;

    @FXML
    private JFXButton loginButton;

    @FXML
    private JFXButton signinButton;
    
    @FXML
    private JFXButton myPibutton;

    @FXML
    private ListView<String> propositionList;

    @FXML
    private JFXProgressBar propositionProgressBar;

    private String research;

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        //!!!!!!!!!!!!!!!!Hide unused objects !!!!!!!!!!!!!!!!
        loginButton.setVisible(false);
        signinButton.setVisible(false);


        //Disable the text field, we wait for the at least one toggle to be pressed
        activateField(false, true);
        propositionList.setVisible(false);
        propositionProgressBar.setVisible(false);
        progressIndicator.setVisible(false);
        /*
         * When the text in the input field is changed,
         * we constantly remove spaces and add the # or @ at the begining
         */
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        searchField.textProperty().addListener(
                (observable, old_value, new_value) -> {
                    propositionList.getItems().clear();
                    propositionList.setVisible(false);
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
                        if (new_value.length() > 2) {
                            pause.setOnFinished(event -> {
                                propositionProgressBar.setVisible(true);
                                showPropositionList(new_value.substring(1));
                            });
                            pause.playFromStart();
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
    private void searchButtonPressed(ActionEvent event) {
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

    /**
     * This method displays a list of propositions based on the newValue of the searchField
     *
     * @param newValue
     */
    private void showPropositionList(String newValue) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                ObservableList<String> items = propositionList.getItems();
                SearchViewer searchViewer = new SearchViewer();
                //Here we remove the @ to make our research of propositions
                List<String> users = searchViewer.getListPropositions(newValue);
                //We go through the proposition list
                Platform.runLater(()->{
                    items.addAll(users);
                    if(items.size()>0){
                        propositionList.setVisible(true);
                    }
                    propositionProgressBar.setVisible(false);
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * If user pressed enter key in the search field, we call searchButtonPressed()
     */
    @FXML
    private void onEnter(ActionEvent event) {
        searchButtonPressed(event);
    }

    /**
     * If user choose an entry in the list, we launch the search by firing an event
     * @param event
     */
    @FXML
    private void listClicked(MouseEvent event) {
        searchField.setText(propositionList.getSelectionModel().getSelectedItem());
        propositionList.setVisible(false);
        progressLabel.setVisible(false);
        searchButton.fire();
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

                        //We get the user we chose from the list
                        String newResearch = research;
                        //we search the user and go to the user tab
                        if (newResearch != null) {
                            UserViewer userViewer = new UserViewer();
                            userViewer.searchScreenName(newResearch);
                            mainController.goToUserPane(userViewer);
                        }
                        //we go to this part when user exists, else Exception is thrown
                        progressLabel.setVisible(false);
                    }
                    searchIsRunning(false);
                } catch (Exception e) {

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
                    System.out.println("Something went wrong : ");
                    e.printStackTrace();
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
            progressIndicator.setVisible(true);
            searchButton.setVisible(false);
            activateField(false, false);
        } else {
            progressIndicator.setVisible(false);
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
        propositionProgressBar.setVisible(false);
        propositionList.setVisible(false);
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
