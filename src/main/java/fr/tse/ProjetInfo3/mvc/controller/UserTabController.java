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
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
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

    Map<Tweet, Integer> Tweeted;

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

//    @FXML
//    private JFXSpinner progressIndicator;

    @FXML
    private TitledPane titledHashtag;

    @FXML
    private TitledPane titledTweet;
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
    private Circle avatar;
    @FXML
    private ImageView profileImageView;
    private Image profileImage;

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
            buildPicture();
            //Image profilePic = new Image(userToPrint.getProfile_image_url_https());
            //profileImageView.setImage(profilePic);
            //profileImageView.setClip(avatar);
            //avatar.setFill(new ImagePattern(profilePic));
        });

        Thread threadhashtags = new Thread(setTopHashtags());
        Thread threadtweets = new Thread(setTopTweets());
        threadhashtags.setDaemon(true);
        threadhashtags.start();
        threadtweets.setDaemon(true);
        threadtweets.start();
    }

    @FXML
    private void initialize() {
        //hide elements
        compareButton.setVisible(false);
        favoriteToggle.setVisible(false);
        JFXScrollPane.smoothScrolling(scrollPane);

    }

    //TODO
    private String numberFormatter(long value) {
        return Arrays.toString(String.valueOf(value).split("([0-9]{3})*$"));
    }

    /*
     * Draws the profile picture after rounding it.
     * */
    private void buildPicture() {
        Image profilePic = new Image(userToPrint.getProfile_image_url_https());
        profileImageView.setImage(profilePic);
        Circle clip = new Circle(67, 67, 67);
        profileImageView.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = profileImageView.snapshot(parameters, null);
        profileImageView.setClip(null);
        profileImageView.setImage(image);
    }

    @FXML
    private void addTweetsToList(List<Tweet> toptweets) {
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();

        try {
            if(userViewer != null){
                for(Tweet tweet : toptweets){
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
                    JFXListCell jfxListCell = fxmlLoader.load();
                    listTweetCell.add(jfxListCell);
                    TweetController tweetController = (TweetController) fxmlLoader.getController();
                    tweetController.injectUserTabController(this);
                    tweetController.populate(tweet);
                    listTweets.getItems().add(jfxListCell);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
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
//            progressIndicator.setVisible(true);
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
            titledHashtag.setMaxHeight(50*hashtagsToPrint.size());
//            progressIndicator.setVisible(false);
        });
        return null;
    }

    private Task<Void> setTopTweets() {

//        Platform.runLater(() -> {
//            progressIndicator.setVisible(true);
//        });
        char typeResearch = getTypeSearch();
        switch (typeResearch) {
            case 'd':
                tweetList = userViewer.getTweetsByCount(userToPrint.getScreen_name(), 200);
                Tweeted = userViewer.topTweets(tweetList);
                break;
            case 'c':
                tweetList = userViewer.getTweetsByDate(userToPrint.getScreen_name(), getDate());
                Tweeted = userViewer.topTweets(tweetList);
                break;
        }

        ObservableList<Tweet> TweetsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Tweet tweet : Tweeted.keySet()) {
            TweetsToPrint.add(tweet);
            System.out.println(tweet);
            i++;
            if (i == 5) {
                break;
            }
        }

        Platform.runLater(() -> {
            addTweetsToList(TweetsToPrint);
            titledTweet.setMaxHeight(50*TweetsToPrint.size());
            //progressIndicator.setVisible(false);
        });
        return null;
    }
}
