package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.TwitterDateParser;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sobun UNG
 **/
public class UserTabController {
    private MainController mainController;

    private UserViewer userViewer;

    private User userToPrint;

    private List<Tweet> tweetList;

    Map<String, Integer> hashtagUsed;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXListView<JFXListCell> listTweets;

    @FXML
    private JFXButton compareButton;

    @FXML
    private JFXToggleNode favoriteToggle;

    @FXML
    private JFXSpinner progressIndicator;

    @FXML
    private Label lastAnalysedLabel;

    @FXML
    private TitledPane titledHashtag;
    @FXML
    private Pane thirdpane;
    /*
     * We will populate this fields/labels by the result of search
     */
    @FXML
    private Label username;
    @FXML
    private Label twittername;
    @FXML
    private Label description;
    @FXML
    private Label nbTweet;
    @FXML
    private Label nbFollowers;
    @FXML
    private Label nbFollowing;
    @FXML
    private JFXListView<ResultHashtag> listHashtags;
    @FXML
    private Circle avatar;
    @FXML
    private ImageView profileImageView;

    private Image profileImage;

    /**************************************************************/

    /*Controller can access to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /*
     * Set the user of the page
     * Prints User simple infos (name, id...)
     * Prints top #
     * */
    public void setUserViewer(UserViewer userViewer) {
        this.userViewer = userViewer;
        userToPrint = userViewer.getUser();

        Platform.runLater(() -> {
            username.setText("@" + userToPrint.getScreen_name());
            twittername.setText(userToPrint.getName());
            description.setText(userToPrint.getDescription());
            nbTweet.setText(String.valueOf(userToPrint.getStatuses_count()));
            nbFollowers.setText(String.valueOf(userToPrint.getFollowers_count()));
            nbFollowing.setText(String.valueOf(userToPrint.getFriends_count()));
            buildPicture();
        });

        Thread thread = new Thread(getTweets());
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void initialize() {
        //hide elements
        compareButton.setVisible(false);
        favoriteToggle.setVisible(false);
        JFXScrollPane.smoothScrolling(scrollPane);

        listHashtags.setCellFactory(param -> new Cell());
        lastAnalysedLabel.setVisible(false);

    }

    //TODO
    private String numberFormatter(long value) {
        return Arrays.toString(String.valueOf(value).split("([0-9]{3})*$"));
    }

    /*
     * Draws the profile picture after rounding it.
     * */
    private void buildPicture() {
        Image profilePic = new Image(userToPrint.getProfile_image_url_https());
        profileImageView.setImage(profilePic);
        Circle clip = new Circle(67, 67, 67);
        profileImageView.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = profileImageView.snapshot(parameters, null);
        profileImageView.setClip(null);
        profileImageView.setImage(image);
    }

    @FXML
    private void addTweetsToList() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Tweet.fxml"));
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();

        try {
            //For the tests
            JFXListCell jfxListCell = fxmlLoader.load();
            TweetController tweetController = fxmlLoader.getController();
            tweetController.injectUserTabController(this);
            tweetController.populate();
            listTweets.getItems().add(jfxListCell);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private char getTypeSearch() {
        return 'd';
        //TODO getType by user
    }

    private Date getDate() {
        Date twitterDate = null;
        try {
            twitterDate = TwitterDateParser.parseTwitterUTC("Fri Nov 11 20:00:00 CET 2019");
            //TODO getField by user
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return twitterDate;
    }

    /**
     * Called by setUser, it gets the tweets of a user,
     * then create Two concurrent thread to check the top Hashtag and top Tweets
     */
    private Task<Void> getTweets() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
        });

        long numberOfRequest = userToPrint.getStatuses_count();
        if (numberOfRequest > 3194) {
            numberOfRequest = 3194;
        }
        tweetList = userViewer.getTweetsByCount(userToPrint.getScreen_name(), (int) numberOfRequest);

        Thread thread = new Thread(setTopHashtags());
        thread.setDaemon(true);
        thread.start();

        //Set top tweets, but it can be dangerous with the shared tweetlist, we have to test
        //Thread thread = new Thread(settoptweets());
        //thread.setDaemon(true);
        //thread.start();

        return null;
    }

    private Task<Void> setTopHashtags() {
        hashtagUsed = userViewer.topHashtag(tweetList);

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtagUsed.get(hashtag).toString()));
            i++;
            if (i == 5) {
                break;
            }
        }
        Platform.runLater(() -> {
            listHashtags.getItems().addAll(hashtagsToPrint);
            titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
            progressIndicator.setVisible(false);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = simpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
            lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                    date);
            lastAnalysedLabel.setVisible(true);
        });
        return null;
    }

    /**
     * This class will represent a result of a linked hashtag
     */
    static class Cell extends ListCell<ResultHashtag> {
        HBox hBox = new HBox();
        Label classementLabel = new Label("");
        Label hashtagLabel = new Label("");
        Label nbTweetLabel = new Label("");

        Cell() {
            super();
            classementLabel.getStyleClass().add("indexLabel");
            hashtagLabel.getStyleClass().add("hashtagTextLabel");
            nbTweetLabel.getStyleClass().add("nbTweetLabel");
            hBox.getChildren().addAll(classementLabel, hashtagLabel, nbTweetLabel);
        }

        public void updateItem(ResultHashtag resultHashtag, boolean empty) {
            super.updateItem(resultHashtag, empty);

            if (resultHashtag != null && !empty) {
                classementLabel.setText(resultHashtag.getClassementIndex());
                hashtagLabel.setText(resultHashtag.getHashtagName());
                nbTweetLabel.setText(resultHashtag.getNbTweets() + " tweets");
                setGraphic(hBox);
            }
        }
    }

    /**
     * This class will represent a result of a linked hashtag
     */
    private static class ResultHashtag {
        private final String classementIndex;
        private final String hashtagName;
        private final String nbTweets;

        ResultHashtag(String classementIndex, String hashtagName, String nbTweets) {
            this.classementIndex = classementIndex;
            this.hashtagName = hashtagName;
            this.nbTweets = nbTweets;
        }

        String getClassementIndex() {
            return classementIndex;
        }

        String getHashtagName() {
            return hashtagName;
        }

        String getNbTweets() {
            return nbTweets;
        }
    }
}
