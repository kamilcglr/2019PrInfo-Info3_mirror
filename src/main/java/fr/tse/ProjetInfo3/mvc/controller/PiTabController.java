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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the Edit PI window, all user interactions whith the Edit PI windows (not the tabs) are handled here
 */
public class PiTabController {

    private List<Tweet> bigTweetList;

    private MainController mainController;

    private PIViewer piViewer;

    private InterestPoint interestPointToPrint;

    @FXML
    private JFXButton editButton;

    @FXML
    private JFXListView<User> topFiveUserList;

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

    /*
     * THREADS
     * every thread should be declared here to kill them when exiting
     */
    private Thread threadGetTweets;

    private Thread threadTopFiveUsers;

    //private Thread threadsetNumbers;
    //
    //private Thread threadTopTweets;


    //Progress indicator
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label progressLabel;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        //hide unused elements
        nbTweets.setVisible(false);
        nbTweetsLabel.setVisible(false);
        editButton.setVisible(false);

        topFiveUserList.setCellFactory(param -> new ListObjects.TopUserCell());
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

            threadTopFiveUsers = new Thread(setTopFiveUsers());
            threadTopFiveUsers.setDaemon(true);
            threadTopFiveUsers.start();

            //threadsetNumbers = new Thread(setNumberOfUniqueAccountAndNumberOfTweets());
            //threadsetNumbers.setDaemon(true);
            //threadsetNumbers.start();

            //threadTopTweets = new Thread(setTopTweets());
            //threadTopTweets.setDaemon(true);
            //threadTopTweets.start();

            //Wait for the two other tasks
            while (threadTopFiveUsers.isAlive()) {
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

    private Task<Void> setTopFiveUsers() throws Exception {
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

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        //if (threadGettweets != null) {
        //    threadGettweets.interrupt();
        //}
    }


}

