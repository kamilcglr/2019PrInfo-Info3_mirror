package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleTopHashtagCell;

import fr.tse.ProjetInfo3.mvc.utils.NumberParser;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.*;

/**
 * @author Sobun UNG
 * @author kamilcaglar
 **/
public class UserTabController {
    private MainController mainController;

    private UserViewer userViewer;

    private FavsViewer favsViewer;
    private User userToPrint;

    /* THREADS
     * Every thread should be declared here to kill them when exiting
     */
    private Thread threadGetTweets;

    private Thread threadSetTopHashtags;

    private Thread threadSetTopTweets;

    /* LISTS
     */
    private List<Tweet> tweetList;

    @FXML
    private JFXListView<JFXListCell> listTweets;

    @FXML
    private Label lastAnalysedLabel;

    @FXML
    private TitledPane titledHashtag;

    @FXML
    private TitledPane titledTweet;

    @FXML
    private VBox vBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXButton refreshButton;

    @FXML
    private JFXToggleNode favoriteToggle;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private JFXToggleNode NotfavoriteToggle;

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
    private ImageView profileImageView;
    @FXML
    private Label lastSearchLabel;


    /**************************************************************/
    /*Controller can access to main Controller */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        hideElements(true);
        listHashtags.setCellFactory(param -> new SimpleTopHashtagCell());
        JFXScrollPane.smoothScrolling(scrollPane);
    }

    private void hideElements(boolean hideAll) {
        Platform.runLater(() -> {
            refreshButton.setVisible(false);
            lastSearchLabel.setVisible(false);
            favoriteToggle.setVisible(false);
            NotfavoriteToggle.setVisible(false);
            hideLists(true);
            if (hideAll) {
                username.setVisible(false);
                twittername.setVisible(false);
                description.setVisible(false);
                nbTweet.setVisible(false);
                nbFollowers.setVisible(false);
                nbFollowing.setVisible(false);
            }
        });
    }

    private void hideLists(boolean show) {
        vBox.setVisible(!show);
        lastAnalysedLabel.setVisible(!show);
    }

    public void refreshButtonPressed(ActionEvent actionEvent) {
        userViewer.deleteCachedUser();
        mainController.closeCurrentTab();
        mainController.goToUserPane(userViewer);
    }

    /**
     * Set the user of the page. Get from db if exist, do search if not.
     * Prints User simple infos (name, id...)
     *
     * @param userViewer
     */
    public void setUserViewer(UserViewer userViewer) {
        this.userViewer = userViewer;

        Platform.runLater(() -> initProgress(true, "Vérification si l'utilisateur est déjà dans la base."));

        //if in database, load everything from there
        if (userViewer.verifyUserInDataBase()) {
            Platform.runLater(() -> progressLabel.setText("Récupération des données depuis la base de données."));
            userViewer.setUserFromDataBase();
            setUserInfos();
            tweetList = userToPrint.getTweets();
            setTops();
        } else {
            setUserInfos();
            //user not in db, get tweets from twitter
            threadGetTweets = new Thread(getTweets());
            threadGetTweets.setDaemon(true);
            threadGetTweets.start();
        }
    }

    private void setUserInfos() {
        userToPrint = userViewer.getUser();
        Platform.runLater(() -> {
            if (userToPrint.getLastSearchDate() != null) {
                lastSearchLabel.setText("Dernière recherche effectuée le " + hoursAndDateFormat.format(userToPrint.getLastSearchDate()));
                lastSearchLabel.setVisible(true);
            }
            username.setVisible(true);
            refreshButton.setVisible(true);
            username.setText("@" + userToPrint.getScreen_name());
            twittername.setVisible(true);
            twittername.setText(userToPrint.getName());
            description.setVisible(true);
            description.setText(userToPrint.getDescription());
            nbTweet.setText(NumberParser.spaceBetweenNumbers(userToPrint.getStatuses_count()));
            nbTweet.setVisible(true);
            nbFollowers.setText(NumberParser.spaceBetweenNumbers(userToPrint.getFollowers_count()));
            nbFollowers.setVisible(true);
            nbFollowing.setText(NumberParser.spaceBetweenNumbers(userToPrint.getFriends_count()));
            nbFollowing.setVisible(true);
            buildPicture();
        });
        if (LoginController.connected == 1) {
            favourites();
        } else {
            favoriteToggle.setVisible(false);
            NotfavoriteToggle.setVisible(false);
        }
    }

    /**
     * Sets top tweets and top hashtags
     */
    private void setTops() {
        Platform.runLater(() -> initProgress(true, "Analyse des tweets"));
        threadSetTopHashtags = new Thread(setTopHashtags());
        threadSetTopHashtags.setDaemon(true);
        threadSetTopHashtags.start();

        //Set top tweets, but it can be dangerous with the shared tweetList, we have to test
        threadSetTopTweets = new Thread(setTopTweets());
        threadSetTopTweets.setDaemon(true);
        threadSetTopTweets.start();

        //Wait for the two other tasks
        while (threadSetTopHashtags.isAlive() || threadSetTopTweets.isAlive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            lastSearchLabel.setText("Dernière recherche effectuée le " + hoursAndDateFormat.format(userToPrint.getLastSearchDate()));
            if (tweetList.size() > 0) {
                String date = frenchSimpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
                lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " + date);
            } else {
                lastAnalysedLabel.setText("Cet utilisateur n'a aucun tweet");
            }
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            hideLists(false);
        });
    }

    /**
     * Called by setUser, it gets the tweets of a user,
     * then create Two concurrent thread to check the top Hashtag and top Tweets
     */
    private Task<Void> getTweets() {
        Platform.runLater(() -> initProgress(false, "Récupération des tweets depuis Twitter.com"));

        long numberOfRequest = userToPrint.getStatuses_count();
        if (numberOfRequest > 3194) {
            numberOfRequest = 3194;
        }
        tweetList = userViewer.getTweetsByCount(userToPrint.getScreen_name(), (int) numberOfRequest, progressBar);

        //Save the new search to DB
        userToPrint.setLastSearchDate(new Date());
        userToPrint.setTweets(tweetList);
        userViewer.cacheUserToDataBase(userToPrint);

        setTops();
        return null;
    }

    private Task<Void> setTopHashtags() {
        Map<String, Integer> hashtagUsed = userViewer.getTopTenHashtags(tweetList);

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
        });
        return null;
    }

    private Task<Void> setTopTweets() {
        Map<Tweet, Integer> topTweets = userViewer.topTweets(tweetList);
        ObservableList<Tweet> tweetsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Tweet tweet : topTweets.keySet()) {
            tweetsToPrint.add(tweet);
            i++;
            if (i == 5) {
                break;
            }
        }

        Platform.runLater(() -> {
            addTweetsToList(tweetsToPrint);
            titledTweet.setMaxHeight(70 * tweetsToPrint.size());
        });
        return null;
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
    private void addTweetsToList(List<Tweet> toptweets) {
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();
        try {
            if (userViewer != null) {
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

                    tweetController.injectUserTabController(this);
                    tweetController.populate(tweet, false);
                    listTweets.getItems().add(jfxListCell);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void initProgress(boolean isIndeterminate, String text) {
        if (!isIndeterminate) {
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            progressLabel.setVisible(true);
        } else {
            progressBar.setProgress(-1);
        }
        progressLabel.setText(text);
    }

    /* ================ FAVORITES ================    */
    public void favourites() {
        favsViewer = new FavsViewer();
        userToPrint = userViewer.getUser();
        int fav = favsViewer.checkUserInFav(userToPrint);
        if (fav == 1) {
            favoriteToggle.setVisible(false);
            NotfavoriteToggle.setVisible(true);
        } else {
            favoriteToggle.setVisible(true);
            NotfavoriteToggle.setVisible(false);
        }
    }

    @FXML
    private void favouriteTogglePressed() {

        favsViewer = new FavsViewer();
        favsViewer.addUserToFavourites(userToPrint);
        int fav = favsViewer.checkUserInFav(userToPrint);
        if (fav == 1) {
            favoriteToggle.setVisible(false);
            NotfavoriteToggle.setVisible(true);
        } else {
            favoriteToggle.setVisible(true);
            NotfavoriteToggle.setVisible(false);
        }
    }

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        if (threadGetTweets != null) {
            threadGetTweets.interrupt();
        }
        if (threadSetTopHashtags != null) {
            threadSetTopHashtags.interrupt();

        }
        if (threadSetTopTweets != null) {
            threadSetTopTweets.interrupt();
        }
    }
}
