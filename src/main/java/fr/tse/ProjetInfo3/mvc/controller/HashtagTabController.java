package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.Icon;

import java.io.IOException;
import java.util.List;

public class HashtagTabController {
    private MainController mainController;

    private HastagViewer hastagViewer;

    @FXML
    private Icon expandTweetsIcon;

    @FXML
    private Icon expandHashtagIcon;

    @FXML
    private JFXListView topFiveList;

    @FXML
    private JFXListView topTenLinkedList;

    @FXML
    private GridPane gridPane;


    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setHastagViewer(HastagViewer hastagViewer) {
        this.hastagViewer = hastagViewer;
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void addTweetsToList() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();

        try {
            //This part will be official when hastag method will be implemented
//            if(hastagViewer != null){
//                List<Tweet> tweets = hastagViewer.getHashtag().getTweets();
//                for(Tweet tweet : tweets){
//                    JFXListCell jfxListCell = fxmlLoader.load();
//                    listTweetCell.add(jfxListCell);
//                    TweetController tweetController = (TweetController) fxmlLoader.getController();
//                    tweetController.injectHashtagTabController(this);
//
//                    tweetController.populate(tweet);
//                }
//            }

            //For the tests
            JFXListCell jfxListCell = fxmlLoader.load();
            TweetController tweetController = fxmlLoader.getController();
            tweetController.injectHashtagTabController(this);
//            //tweetController.populate();
            topFiveList.getItems().add(jfxListCell);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
