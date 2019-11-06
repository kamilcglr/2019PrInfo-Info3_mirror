package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

/**
 * @author Sobun UNG
 **/

public class UserTabController {
    private MainController mainController;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXListView listTweets;

    @FXML
    private JFXListView listHashtags;

    @FXML
    private JFXButton compareButton;

    @FXML
    private JFXButton favoriteButton;

    @FXML
    JFXToggleNode chevronTweet;

    private FontIcon arrow_up;
    private FontIcon arrow_down;

    /*Controller can access to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        arrow_up = new FontIcon(FontAwesomeSolid.CHEVRON_UP);
        arrow_down = new FontIcon(FontAwesomeSolid.CHEVRON_DOWN);
        for (int i = 1; i < 11; i++) listHashtags.getItems().add(new Label("Hashtag " + i));
        //for (int i = 1; i < 11; i++) listTweets.getItems().add(new Label("Tweets " + i));
        addTweetsToList();
        addTweetsToList();

    }

    @FXML
    private void expandTweetsTogglePressed(ActionEvent event) {
        /*if the toggle is already expanded we fold it*/
        if (chevronTweet.isSelected()) {
            chevronTweet.setGraphic(arrow_up);
            gridPane.getRowConstraints().get(4).setPrefHeight(1000);
            listTweets.setPrefHeight(1000);
        } else {
            chevronTweet.setGraphic(arrow_down);
            gridPane.getRowConstraints().get(4).setPrefHeight(100);
        }
    }

    @FXML
    private void addTweetsToList() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();

        try {
            //For the tests
            JFXListCell jfxListCell = fxmlLoader.load();
            TweetController tweetController = (TweetController) fxmlLoader.getController();
            tweetController.injectUserTabController(this);
            tweetController.populate();
            listTweets.getItems().add(jfxListCell);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
