package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the Edit PI window, all user interactions whith the Edit PI windows (not the tabs) are handled here
 */
public class PiTabController {

    private MainController mainController;

    private List<Tweet> bigTweetList;

    private PIViewer piViewer;

    private InterestPoint interestPointToPrint;

    @FXML
    private JFXButton editButton;

    /* LISTS
     */
    @FXML
    private JFXListView<User> topFiveUserList;
    @FXML
    private VBox vBox;
    @FXML
    private JFXListView<ListObjects.ResultHashtag> topTenLinkedList;
    @FXML
    private JFXListView<JFXListCell> listTweets;
    @FXML
    private TitledPane titledTweet;

    @FXML
    private Label piNameLabel;

    @FXML
    private Label nbTweetsLabel;

    @FXML
    private Label nbTweets;

    /*
     * THREADS
     * every thread should be declared here to kill them when exiting
     */
    private Thread threadGetTweets;

    private Thread threadTopFiveUsers;

    private Thread threadTopLinkedHashtags;

    private Thread threadTopTweets;

    private UserViewer userViewer;

    //Progress indicators
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label progressLabel;

    /*Controller can access to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        //hide unused elements
        nbTweets.setVisible(false);
        nbTweetsLabel.setVisible(false);
        editButton.setVisible(false);

        //topTenLinkedList.setCellFactory(param -> (ListCell<ListObjects.ResultHashtag>) new Cell());

        topFiveUserList.setCellFactory(param -> new ListObjects.TopUserCell());
        topTenLinkedList.setCellFactory(param -> new ListObjects.HashtagCell());

        userViewer = new UserViewer();
    }

    public void setDatas(PIViewer piViewer) {
        this.piViewer = piViewer;
        this.interestPointToPrint = piViewer.getSelectedInterestPoint();
        Platform.runLater(() -> {
            piNameLabel.setText(interestPointToPrint.getName());
            showElements(false);
        });

        threadGetTweets = new Thread(getTweets());
        threadGetTweets.setDaemon(true);
        threadGetTweets.start();
    }

    private Task<Void> getTweets() {
        try {
            bigTweetList = piViewer.getTweets(progressLabel);

            //Tweet are collected
            Platform.runLater(() -> {
                progressLabel.setText("Analyse des résultats");
            });

            threadTopFiveUsers = new Thread(setTopFiveUsers());
            threadTopFiveUsers.setDaemon(true);
            threadTopFiveUsers.start();

            threadTopLinkedHashtags = new Thread(setTopLinkedHashtags());
            threadTopLinkedHashtags.setDaemon(true);
            threadTopLinkedHashtags.start();

            threadTopTweets = new Thread(setTopTweets());
            threadTopTweets.setDaemon(true);
            threadTopTweets.start();

            //Wait for the two other tasks
            while (threadTopFiveUsers.isAlive()) {
                Thread.sleep(1000);
            }
            Platform.runLater(() -> {
                // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                // String date = simpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
                // lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                //         date);

                showElements(true);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task<Void> setTopFiveUsers() throws Exception {
        //user in parameters to find what to exclude
        List<User> users = piViewer.getTopFiveUsers(bigTweetList, interestPointToPrint.getUsers());

        ObservableList<User> usersToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (User user : users) {
            usersToPrint.add(user);
            i++;
            if (i == 5) {
                break;
            }
        }
        Platform.runLater(() -> {
            topFiveUserList.getItems().addAll(usersToPrint);
            //topFiveUserList.setMaxHeight(50 * usersToPrint.size());
        });

        return null;
    }

    private Task<Void> setTopLinkedHashtags() {
        Map<String, Integer> hashtags = userViewer.getTopTenHashtags(bigTweetList);

        int i = 0;
        ObservableList<ListObjects.ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        for (String hashtag : hashtags.keySet()) {
            i++;
            hashtagsToPrint.add(new ListObjects.ResultHashtag(String.valueOf(i), hashtag, hashtags.get(hashtag).toString()));
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
            topTenLinkedList.getItems().addAll(hashtagsToPrint);
        });
        //titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        return null;
    }

    @FXML
    private void addTweetsToList(List<Tweet> toptweets) {
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();
        try {
            if (piViewer != null) {
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

                    tweetController.injectPiTabController(this);
                    tweetController.populate(tweet, true);
                    listTweets.getItems().add(jfxListCell);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Task<Void> setTopTweets() {
        ObservableList<Tweet> tweetsToPrint = FXCollections.observableArrayList();
        Map<Tweet, Integer> topTweets = new HashMap<Tweet, Integer>();
        topTweets = piViewer.topTweets(bigTweetList, progressBar);

        int i = 0;
        for (Tweet tweet : topTweets.keySet()) {
            tweetsToPrint.add(tweet);
            i++;
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
            addTweetsToList(tweetsToPrint);
        });
        titledTweet.setMaxHeight(70 * tweetsToPrint.size());
        return null;
    }

    private void showElements(boolean show) {
        //searching or analysing tweets
        if (!show) {
            progressBar.setProgress(-1);
            progressBar.setVisible(true);
            progressLabel.setVisible(true);
        } else {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
        }

        vBox.setVisible(show);
        nbTweetsLabel.setVisible(show);
        //nbUsersLabel.setVisible(hide);
        //tweetsLabel.setVisible(hide);
        //usersLabel.setVisible(hide);
        //lastAnalysedLabel.setVisible(hide);
    }

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        if (threadGetTweets != null) {
            threadGetTweets.interrupt();
        }
        if (threadTopFiveUsers != null) {
            threadTopFiveUsers.interrupt();
        }
        if (threadTopTweets != null) {
            threadTopTweets.interrupt();
        }
        if (threadTopLinkedHashtags != null) {
            threadTopLinkedHashtags.interrupt();
        }
    }


}

