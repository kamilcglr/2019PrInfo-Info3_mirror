package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleNode;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.Icon;

import java.io.IOException;

public class HashtagTabController {
    private MainController mainController;

    private HastagViewer hastagViewer;
    @FXML
    JFXToggleNode expandTweetsToggle;

    @FXML
    JFXToggleNode expandHashtagToggle;

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

    private FontIcon arrow_up;
    private FontIcon arrow_down;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setHastagViewer(HastagViewer hastagViewer) {
        this.hastagViewer = hastagViewer;
    }

    @FXML
    private void initialize() {
        arrow_up = new FontIcon(FontAwesomeSolid.CHEVRON_UP);
        arrow_down = new FontIcon(FontAwesomeSolid.CHEVRON_DOWN);
        addTweetsToList();
    }

    /*This function expand TweetsList*/
    @FXML
    private void expandTweetsTogglePressed(ActionEvent event) {
        /*if the toggle is already expanded we fold it*/
        if (expandTweetsToggle.isSelected()) {
            expandTweetsToggle.setGraphic(arrow_up);
            gridPane.getRowConstraints().get(2).setPrefHeight(500);
        } else {
            expandTweetsToggle.setGraphic(arrow_down);
            gridPane.getRowConstraints().get(2).setPrefHeight(100);
        }
    }

    @FXML
    private void expandHashtagTogglePressed(ActionEvent event) {

    }

    @FXML
    private void addTweetsToList() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();

        try {
            //This part will be official when hastag method will be implemented
            //if(hastagViewer != null){
            //    List<Tweet> tweets = hastagViewer.getHashtag().getTweets();
            //    for(Tweet tweet : tweets){
            //        JFXListCell jfxListCell = fxmlLoader.load();
            //        listTweetCell.add(jfxListCell);
            //        TweetController tweetController = (TweetController) fxmlLoader.getController();
            //        tweetController.injectHashtagTabController(this);
            //        tweetController.populate(tweet);
            //    }
            //}

            //For the tests
            JFXListCell jfxListCell = fxmlLoader.load();
            TweetController tweetController = (TweetController) fxmlLoader.getController();
            tweetController.injectHashtagTabController(this);
            tweetController.populate();
            topFiveList.getItems().add(jfxListCell);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
