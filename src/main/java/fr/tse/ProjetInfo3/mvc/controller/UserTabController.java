package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.TwitterDateParser;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sobun UNG
 **/
public class UserTabController {
    private MainController mainController;

    private UserViewer userViewer;

    private User userToPrint;

    private List<Tweet> tweetList;

    Map<String, Integer> hashtagUsed;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXListView listTweets;

    @FXML
    private JFXButton compareButton;

    @FXML
    private JFXToggleNode favoriteToggle;

    @FXML
    private JFXSpinner progressIndicator;

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
    @FXML
    private JFXListView listHashtags;
    @FXML
    private ImageView profilepicture;
    @FXML
    private Circle circle;
    @FXML
    private Pane thirdpane;

    /**************************************************************/

    /*Controller can access to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /*
    * Set the user of the page
    * Prints User simple infos (name, id...)
    * Prints top #
    * */
    public void setUserViewer(UserViewer userViewer) {
        this.userViewer = userViewer;
        userToPrint = userViewer.getUser();

        Platform.runLater(() -> {
            username.setText("@" + userToPrint.getScreen_name());
            twittername.setText(userToPrint.getName());
            description.setText(userToPrint.getDescription());
            nbTweet.setText(String.valueOf(userToPrint.getStatuses_count()));
            nbFollowers.setText(String.valueOf(userToPrint.getFollowers_count()));
            nbFollowing.setText(String.valueOf(userToPrint.getFriends_count()));
//            Image image = new Image(userToPrint.getProfile_image_url_https(), true);
            profilepicture.setImage(new Image(userToPrint.getProfile_image_url_https(), true));
//            SetProfilePicture(image);

        });

        Thread thread = new Thread(setTopHashtags());
        thread.setDaemon(true);
        thread.start();
    }

//    @FXML
//    private void SetProfilePicture(Image image){
//        profilepicture.setClip(circle);
//        thirdpane.getChildren().add(profilepicture);
//        }


    @FXML
    private void initialize() {
        //hide elements
        compareButton.setVisible(false);
        favoriteToggle.setVisible(false);
        JFXScrollPane.smoothScrolling(scrollPane);

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

    //This function prints a simplified version of user
    private void setSimpleUser() {

    }

    private char getTypeSearch() {
        return 'd';
        //TODO getType by user
    }

    private Date getDate() {
        Date twitterDate = null;
        try {
            twitterDate = TwitterDateParser.parseTwitterUTC("Fri Nov 11 20:00:00 CET 2019");
            //TODO getField by user
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return twitterDate;
    }

    private Task<Void> setTopHashtags() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
        });
        char typeResearch = getTypeSearch();
        switch (typeResearch) {
            case 'd':
                tweetList = userViewer.getTweetsByCount(userToPrint.getScreen_name(), 200);
                hashtagUsed = userViewer.topHashtag(tweetList);
                break;
            case 'c':
                tweetList = userViewer.getTweetsByDate(userToPrint.getScreen_name(), getDate());
                hashtagUsed = userViewer.topHashtag(tweetList);
                break;
        }
        ObservableList<Label> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new Label(hashtag + " " + hashtagUsed.get(hashtag)));
            i++;
            if (i == 5) {
                break;
            }
        }
        Platform.runLater(() -> {
            listHashtags.getItems().addAll(hashtagsToPrint);
            progressIndicator.setVisible(false);
        });
        return null;
    }
}
