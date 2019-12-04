package fr.tse.ProjetInfo3.mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.Cell;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.viewer.PITabViewer;
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

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the Edit PI window, all user interactions whith the Edit PI windows (not the tabs) are handled here
 */
public class PiTabController {
    private List<Tweet> bigTweetList;

    @FXML
    private AnchorPane PIPane;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label piNameLabel;

    @FXML
    private Accordion accordion;

    @FXML
    private Label nbTweetsLabel;

    @FXML
    private Label nbTweets;

    @FXML
    private JFXButton editButton;


    private MainController mainController;
    
    @FXML
    private JFXListView<ResultHashtag> topTenLinkedList;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private JFXListView<JFXListCell> listTweets;
    @FXML
    private TitledPane titledTweet;


    private PIViewer piViewer;
    private PITabViewer piTabViewer;
    private UserViewer userViewer;
    Map<String, Integer> hashtags;
    List<String> myHashtags = new ArrayList<>();
    List<Tweet> tweetlist = new ArrayList<>();
    Map<Tweet, Integer> Tweeted = new HashMap<Tweet, Integer>();
    List<String> ListOfUsers = new ArrayList<>();

    /*
     * THREADS
     * every thread should be declared here to kill them when exiting
     */
    private Thread threadGetTweets;

    private Thread threadTopTweets;


    private InterestPoint interestPointToPrint;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }


    @FXML
    private void initialize() {
        //hide unused elements
        //   accordion.setVisible(false);
        // nbTweets.setVisible(false);
        // nbTweetsLabel.setVisible(false);
        //     editButton.setVisible(false);
        topTenLinkedList.setCellFactory(param -> new Cell());


    }

    public void setDatas(PIViewer piViewer) {
        this.piViewer = piViewer;
        this.interestPointToPrint = piViewer.getSelectedInterestPoint();
        Platform.runLater(() -> {
            piNameLabel.setText(interestPointToPrint.getName());
        });

        threadGetTweets = new Thread(getTweets());
        threadGetTweets.setDaemon(true);
        threadGetTweets.start();
    }

    private Task<Void> getTweets() {
        //Platform.runLater(() -> {
        //    initProgress(false);
        //});
        try {
            //TODO replace with function created by laila and sobun
            bigTweetList = piViewer.getTweets(progressBar);

            ////Tweet are collected
            //Platform.runLater(() -> {
            //    initProgress(true);
            //});

            //threadsetNumbers = new Thread(setNumberOfUniqueAccountAndNumberOfTweets());
            //threadsetNumbers.setDaemon(true);
            //threadsetNumbers.start();

            threadTopTweets = new Thread(setTopTweets());
            threadTopTweets.setDaemon(true);
            threadTopTweets.start();

            //Wait for the two other tasks
            while (threadTopTweets.isAlive()) {
                Thread.sleep(1000);
            }
            Platform.runLater(() -> {
                // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                // String date = simpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
                // lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                //         date);

                // showHashtagElements(true);
                progressBar.setVisible(false);
                //progressLabel.setVisible(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setMyPITabViewer(PITabViewer piTabViewer, PIViewer piViewer) {
        myHashtags.add("blackfriday");
        myHashtags.add("mardi");
        this.piTabViewer = piTabViewer;
        this.piViewer = piViewer;
        piTabViewer.getMyHashtags().forEach(ha -> System.out.println(ha));

        this.interestPointToPrint = piViewer.getSelectedInterestPoint();
        hashtags = piTabViewer.getListOfHashtagsforPI(progressBar, myHashtags);

        Platform.runLater(() -> {
            piNameLabel.setText(interestPointToPrint.getName());
        });

        Thread thread = new Thread(setTopLinkedHashtag());
        thread.setDaemon(true);
        thread.start();
    }

    private Task<Void> setTopLinkedHashtag() {
        // List<String> hashtagPI = piTabViewer.getHashtagsOfHashtags();
        hashtags = piTabViewer.getListOfHashtagsforPI(progressBar, myHashtags);

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtags.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtags.get(hashtag).toString()));
            i++;
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
            topTenLinkedList.getItems().addAll(hashtagsToPrint);
            //titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });

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
                    tweetController.populate(tweet);
                    listTweets.getItems().add(jfxListCell);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Task<Void> setTopTweets() {
        Tweeted = piViewer.topTweets(bigTweetList, progressBar);
        ObservableList<Tweet> tweetsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Tweet tweet : Tweeted.keySet()) {
            tweetsToPrint.add(tweet);
            System.out.println(tweet);
            i++;
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
            addTweetsToList(tweetsToPrint);
            titledTweet.setMaxHeight(70 * tweetsToPrint.size());
        });
        return null;
    }
}

