package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.FrenchSimpleDateFormat;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.Cell;

import fr.tse.ProjetInfo3.mvc.viewer.TwitterDateParser;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fr.tse.ProjetInfo3.mvc.utils.FrenchSimpleDateFormat.frenchSimpleDateFormat;

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
    private VBox vBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXListView<JFXListCell> listTweets;

    @FXML
    private JFXButton compareButton;

    @FXML
    private JFXToggleNode favoriteToggle;

    @FXML
    private JFXSpinner progressIndicator;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label lastAnalysedLabel;

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
    private JFXListView<ResultHashtag> listHashtags;
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
    public void setUserViewer(UserViewer userViewer) throws InterruptedException {
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
        });

        Thread thread = new Thread(getTweets());
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void initialize() {
        //hide elements
        compareButton.setVisible(false);
        favoriteToggle.setVisible(false);
        JFXScrollPane.smoothScrolling(scrollPane);

        listHashtags.setCellFactory(param -> new Cell());

        Platform.runLater(() -> hideLists(true));

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
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();
        try {
            if (userViewer != null) {
                for (Tweet tweet : toptweets) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
                    JFXListCell jfxListCell = fxmlLoader.load();
                    jfxListCell.setMinWidth(listTweets.getWidth() - listTweets.getWidth() * 0.1);
                    listTweets.widthProperty().addListener((obs, oldval, newval) -> {
                        Double test = newval.doubleValue() - newval.doubleValue() * 0.1;
                        jfxListCell.setMinWidth(test);
                    });
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
        return 'c';
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

    /**
     * Called by setUser, it gets the tweets of a user,
     * then create Two concurrent thread to check the top Hashtag and top Tweets
     */
    private Task<Void> getTweets() throws InterruptedException {
        Platform.runLater(() -> {
            initProgress(false);
        });

        long numberOfRequest = userToPrint.getStatuses_count();
        if (numberOfRequest > 3194) {
            numberOfRequest = 3194;
        }
        tweetList = userViewer.getTweetsByCount(userToPrint.getScreen_name(), (int) numberOfRequest, progressBar);

        //Tweet are collected
        Platform.runLater(() -> {
            initProgress(true);
        });

        Thread thread = new Thread(setTopHashtags());
        thread.setDaemon(true);
        thread.start();

        //Set top tweets, but it can be dangerous with the shared tweetlist, we have to test
        Thread thread2 = new Thread(setTopTweets());
        thread2.setDaemon(true);
        thread2.start();

        //Wait for the two other tasks
        while (thread.isAlive() && thread2.isAlive()) {
            Thread.sleep(1000);
        }
        Platform.runLater(() -> {
            String date = frenchSimpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
            lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                    date);

            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            hideLists(false);
        });

        return null;
    }

    private Task<Void> setTopHashtags() {
        hashtagUsed = userViewer.topHashtag(tweetList);

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtagUsed.get(hashtag).toString()));
            i++;
            if (i == 5) {
                break;
            }
        }
        Platform.runLater(() -> {
            listHashtags.getItems().addAll(hashtagsToPrint);
            titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });
        return null;
    }

    private Task<Void> setTopTweets() {
        Tweeted = userViewer.topTweets(tweetList);
        ObservableList<Tweet> tweetsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Tweet tweet : Tweeted.keySet()) {
            tweetsToPrint.add(tweet);
            System.out.println(tweet);
            i++;
            if (i == 5) {
                break;
            }
        }

        Platform.runLater(() -> {
            addTweetsToList(tweetsToPrint);
            titledTweet.setMaxHeight(70 * tweetsToPrint.size());
        });
        return null;
    }

    private void initProgress(boolean isIndeterminate) {
        if (!isIndeterminate) {
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            progressLabel.setVisible(true);
            progressLabel.setText("Récupération des tweets depuis Twitter.com");
        } else {
            progressBar.setProgress(-1);
            progressLabel.setText("Analyse des tweets");
        }
    }

    private void hideLists(boolean show) {
        vBox.setVisible(!show);
        lastAnalysedLabel.setVisible(!show);
    }

}
