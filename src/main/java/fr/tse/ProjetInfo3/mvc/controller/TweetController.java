package fr.tse.ProjetInfo3.mvc.controller;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

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
    private Label nbretweet;

    @FXML
    private Label nbLikes;

    @FXML
    private Label texttweet;

    /**
     * For the moment this function has not any parameters, it will take Tweet tweet after Hashtag method implementation*/
    public void populate(Tweet tweet) {

        Integer RTCount = (int) tweet.getRetweet_count();
        Integer FavCount = (int) tweet.getFavorite_count();
        nbretweet.setText(RTCount.toString());
        nbLikes.setText(FavCount.toString());
        texttweet.setText(tweet.getFull_text());
        }

}
