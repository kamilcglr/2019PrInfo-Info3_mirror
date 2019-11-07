package fr.tse.ProjetInfo3.mvc.controller;

import fr.tse.ProjetInfo3.mvc.dao.Tweet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;

public class TweetController {
    private HashtagTabController hashtagTabController;
    private UserTabController userTabController;

    public void injectHashtagTabController(HashtagTabController hashtagTabController) {
        this.hashtagTabController = hashtagTabController;
    }

    public void injectUserTabController(UserTabController userTabController) {
        this.userTabController = userTabController;
    }


    @FXML
    private Label author;

    @FXML
    private Label nbretweet;

    @FXML
    private Label nbLikes;

    /**
     * For the moment this function has not any parameters, it will take Tweet tweet after Hashtag method implementation*/
    public void populate() {
        author.setText("TestLabel");
        nbretweet.setText("TestRetweets");
        nbLikes.setText("TestLikes");
    }

}
