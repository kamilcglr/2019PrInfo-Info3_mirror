package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import com.jfoenix.controls.JFXProgressBar;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.Cell;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Map;

public class HashtagTabController {
    /**
     * Non FXML elements
     */
    private MainController mainController;

    private HastagViewer hastagViewer;

    private Hashtag hashtagToPrint;

    //Used to know how many tweets we have during search
    private int numberOfTweetReceived;

    Map<String, Integer> hashtagUsed;

    private List<Tweet> tweetList;

    @FXML
    private ScrollPane scrollPane;

    /*
     * THREADS
     * every thread should be declared here to kill them when exiting
     */
    private Thread threadgetTweetFromHashtag;

    private Thread threadsetTopLinkedHashtag;

    private Thread threadsetNumbers;

    /**
     * Elements that will be populated with result
     */
    @FXML
    private Label hashtagLabel;
    @FXML
    private Label nbTweetsLabel;
    @FXML
    private Label nbUsersLabel;
    @FXML
    private Label tweetsLabel;
    @FXML
    private Label usersLabel;

    //lists
    @FXML
    private VBox vbox;
    @FXML
    private JFXListView topFiveTweetsList;
    @FXML
    private JFXListView<ResultHashtag> topTenLinkedList;
    @FXML
    private TitledPane titledHashtag;
    @FXML
    private Label lastAnalysedLabel;

    //Progress indicator
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label progressLabel;

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        showHashtagElements(false);
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        JFXScrollPane.smoothScrolling(scrollPane);
        topTenLinkedList.setCellFactory(param -> new Cell());
    }

    private void showHashtagElements(boolean hide) {
        vbox.setVisible(hide);
        nbTweetsLabel.setVisible(hide);
        nbUsersLabel.setVisible(hide);
        tweetsLabel.setVisible(hide);
        usersLabel.setVisible(hide);
        lastAnalysedLabel.setVisible(hide);
    }

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setHastagViewer(HastagViewer hastagViewer) throws Exception {
        this.hastagViewer = hastagViewer;
        hashtagToPrint = hastagViewer.getHashtag();

        hastagViewer.getSearchProgression();

        Platform.runLater(() -> {
            hashtagLabel.setText("#" + hashtagToPrint.getHashtagName());
        });

        threadgetTweetFromHashtag = new Thread(getTweetFromHashtag());
        threadgetTweetFromHashtag.setDaemon(true);
        threadgetTweetFromHashtag.start();
    }

    /**
     * Called by setHashtag, it gets the tweets of a hashtag,
     * then create concurrent thread to analyze data
     * This task is very long.
     * Do not change the order. We have to wait to get all tweets before doing analysis
     */
    private Task<Void> getTweetFromHashtag() {
        Platform.runLater(() -> {
            initProgress(false);
        });
        try {
            //search and get tweets from hashtag first
            hastagViewer.search(hashtagToPrint.getHashtagName(), progressBar);
            this.tweetList = hastagViewer.getTweetList();

            //Tweet are collected
            Platform.runLater(() -> {
                initProgress(true);
            });

            threadsetTopLinkedHashtag = new Thread(setTopLinkedHashtag());
            threadsetTopLinkedHashtag.setDaemon(true);
            threadsetTopLinkedHashtag.start();

            threadsetNumbers = new Thread(setNumberOfUniqueAccountAndNumberOfTweets());
            threadsetNumbers.setDaemon(true);
            threadsetNumbers.start();

            //Wait for the two other tasks
            while (threadsetTopLinkedHashtag.isAlive() && threadsetNumbers.isAlive()) {
                Thread.sleep(1000);
            }
            Platform.runLater(() -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = simpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
                lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                        date);

                showHashtagElements(true);
                progressBar.setVisible(false);
                progressLabel.setVisible(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task<Void> setTopLinkedHashtag() {
        List<String> hashtags = hastagViewer.getHashtagsLinked();
        hashtagUsed = hastagViewer.topHashtag(hashtags);

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtagUsed.get(hashtag).toString()));
            i++;
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
            topTenLinkedList.getItems().addAll(hashtagsToPrint);
            titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });

        return null;
    }

    private Task<Void> setNumberOfUniqueAccountAndNumberOfTweets() {
        Platform.runLater(() -> {
            nbUsersLabel.setText(hastagViewer.getNumberOfUniqueAccounts().toString());
            nbTweetsLabel.setText(hastagViewer.getNumberOfTweets().toString());
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

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        if (threadgetTweetFromHashtag != null) {
            threadgetTweetFromHashtag.interrupt();
        }
        if (threadsetNumbers != null) {
            threadsetNumbers.interrupt();

        }
        if (threadsetTopLinkedHashtag != null) {
            threadsetTopLinkedHashtag.interrupt();
        }
    }
}
