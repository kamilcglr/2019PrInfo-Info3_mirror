package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListCell;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.utils.NumberParser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static fr.tse.ProjetInfo3.mvc.utils.Dates.frenchSimpleDateFormat;

public class TweetController {
    private HashtagTabController hashtagTabController;
    private UserTabController userTabController;
    private PiTabController piTabController;

    public void injectHashtagTabController(HashtagTabController hashtagTabController) {
        this.hashtagTabController = hashtagTabController;
    }

    public void injectUserTabController(UserTabController userTabController) {
        this.userTabController = userTabController;
    }

    public void injectPiTabController(PiTabController piTabController) {
        this.piTabController = piTabController;
    }
    
    @FXML
    private JFXListCell tweetCell;

    @FXML
    private Label nbretweet;

    @FXML
    private Label nbLikes;

    @FXML
    private Label texttweet;

    @FXML
    private Label dateLabel;

    @FXML
    private Label authorLabel;

    @FXML
    private Label authorIdLabel;

    /**
     * Populate the tweet
     * @param printAuthor if true, we print the author field
     */
    public void populate(Tweet tweet, boolean printAuthor) {
        int RTCount = (int) tweet.getRetweet_count();
        int FavCount = (int) tweet.getFavorite_count();
        nbretweet.setText(NumberParser.spaceBetweenNumbers(RTCount));
        nbLikes.setText(NumberParser.spaceBetweenNumbers(FavCount));
        texttweet.setText(tweet.getFull_text());
        dateLabel.setText(frenchSimpleDateFormat.format(tweet.getCreated_at()));

        if (printAuthor){
            authorLabel.setText(tweet.getUser().getName());
            authorIdLabel.setText("@" + tweet.getUser().getScreen_name());
        }else{
            authorLabel.setVisible(false);
            authorIdLabel.setVisible(false);
        }
    }

}
