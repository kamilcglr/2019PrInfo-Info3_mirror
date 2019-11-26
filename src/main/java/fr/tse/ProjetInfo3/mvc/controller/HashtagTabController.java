package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.Icon;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

//import java.awt.Label;
import java.util.List;
import java.util.Map;

public class HashtagTabController {
    private MainController mainController;

    private HastagViewer hastagViewer;

    private Hashtag hashtagToPrint;

    Map<String, Integer> hashtagUsed;

    private List<Tweet> tweetList;

    @FXML
    private Label hashtagLabel;

    @FXML
    private Icon expandTweetsIcon;

    @FXML
    private Icon expandHashtagIcon;

    @FXML
    private JFXListView topFiveList;
    @FXML
    private JFXSpinner progressIndicator;
    @FXML
    private Label nbTweetLabel;
    @FXML
    private Label nbUserLabel;
    @FXML
    private JFXListView topTenLinkedList;
    @FXML
    private TitledPane titledHashtag;
    @FXML
    private GridPane gridPane;


    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setHastagViewer(HastagViewer hastagViewer) throws Exception {
        this.hastagViewer = hastagViewer;
        hashtagToPrint = hastagViewer.getHashtag();

        Platform.runLater(() -> {
            hashtagLabel.setText("#" + hashtagToPrint.getHashtagName());
        });

        Thread thread = new Thread(getTweetFromHashtag());
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Called by setHashtag, it gets the tweets of a hashtag,
     * then create concurrent thread to analyze data
     */
    private Task<Void> getTweetFromHashtag() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
        });

        Thread thread = new Thread(setTopLinkedHashtag());
        thread.setDaemon(true);
        thread.start();

        Thread thread2 = new Thread(setNumberOfUniqueAccountAndNumberOfTweets());
        thread2.setDaemon(true);
        thread2.start();

        return null;
    }

    private Task<Void> setTopLinkedHashtag() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
        });
        List<String> hashtags = hastagViewer.getHashtagsLinked();
        hashtagUsed = hastagViewer.topHashtag(hashtags);

        ObservableList<Label> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new Label(hashtag + "\t\t\t" + hashtagUsed.get(hashtag)));
            i++;
            if (i == 5) {
                break;
            }
        }
        Platform.runLater(() -> {
            topTenLinkedList.getItems().addAll(hashtagsToPrint);
            titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
            progressIndicator.setVisible(false);
        });

        return null;
    }

    private Task<Void> setNumberOfUniqueAccountAndNumberOfTweets() {
        Platform.runLater(() -> {
            nbUserLabel.setText(hastagViewer.getNumberOfUniqueAccounts().toString());
            nbTweetLabel.setText(hastagViewer.getNumberOfTweets().toString());
        });

        return null;
    }


}
