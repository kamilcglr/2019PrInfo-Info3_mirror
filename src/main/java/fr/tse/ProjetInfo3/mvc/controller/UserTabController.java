package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleNode;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * @author Sobun UNG
 **/
public class UserTabController {
    private MainController mainController;

    private UserViewer userViewer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXListView listTweets;

    @FXML
    private JFXListView listHashtags;

    @FXML
    private JFXButton compareButton;

    @FXML
    private JFXToggleNode favoriteToggle;

    /*
     * We will populate this fields/labels by the result of search
     */
    @FXML
    private Label username;
    @FXML
    private Label twittername;
    @FXML
    private Label description;
    @FXML
    private Label nbTweet;
    @FXML
    private Label nbFollowers;
    @FXML
    private Label nbFollowing;

    /**************************************************************/

    /*Controller can access to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUserViewer(UserViewer userViewer) {
        User userToPrint = userViewer.getUser();
        Platform.runLater(() -> {
            username.setText("@" + userToPrint.getScreen_name());
            twittername.setText(userToPrint.getName());
            description.setText(userToPrint.getDescription());
            nbTweet.setText(String.valueOf(userToPrint.getStatuses_count()));
            nbFollowers.setText(String.valueOf(userToPrint.getFollowers_count()));
            nbFollowing.setText(String.valueOf(userToPrint.getFriends_count()));
        });

    }

    @FXML
    private void initialize() {
        //hide elements
        compareButton.setVisible(false);
        favoriteToggle.setVisible(false);
    }


    @FXML
    private void addTweetsToList() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();

        try {
            //For the tests
            JFXListCell jfxListCell = fxmlLoader.load();
            TweetController tweetController = fxmlLoader.getController();
            tweetController.injectUserTabController(this);
            tweetController.populate();
            listTweets.getItems().add(jfxListCell);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
