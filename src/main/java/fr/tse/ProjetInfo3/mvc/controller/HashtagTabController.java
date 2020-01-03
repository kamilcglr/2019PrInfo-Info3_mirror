package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleTopHashtagCell;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.NumberParser;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.HashtagViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.frenchSimpleDateFormat;
import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.hoursAndDateFormat;

public class HashtagTabController {
    /**
     * Non FXML elements
     */
    private MainController mainController;

    private HashtagViewer hashtagViewer;

    private Hashtag hashtagToPrint;

    private FavsViewer favsViewer;

    Map<String, Integer> hashtagLinked;

    private List<Tweet> tweetList;

    @FXML
    private ScrollPane scrollPane;

    /*
     * THREADS
     * every thread should be declared here to kill them when exiting
     */
    private Thread threadGetTweets;

    private Thread threadSetTopLinkedHashtag;

    private Thread threadSetNumbers;

    private Thread threadSetTopTweets;

    /**
     * Elements that will be populated with result
     */
    @FXML
    private Label hashtagLabel;
    @FXML
    private Label nbTweetsLabel;
    @FXML
    private Label nbUsersLabel;
    @FXML
    private Label tweetsLabel;
    @FXML
    private Label usersLabel;

    //lists
    @FXML
    private JFXButton refreshButton;
    @FXML
    private VBox vBox;
    @FXML
    private JFXListView listTweets;
    @FXML
    private JFXListView<ResultHashtag> topTenLinkedList;
    @FXML
    private TitledPane titledHashtag;
    @FXML
    private Label lastAnalysedLabel;

    @FXML
    private TitledPane titledTweet;

    //Progress indicator
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private JFXToggleNode favoriteToggle;
    @FXML
    private JFXToggleNode NotfavoriteToggle;
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
        topTenLinkedList.setCellFactory(param -> new SimpleTopHashtagCell());
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
                hashtagLabel.setVisible(false);
                nbTweetsLabel.setVisible(false);
                nbUsersLabel.setVisible(false);
                tweetsLabel.setVisible(false);
                usersLabel.setVisible(false);
            }
        });
    }

    private void hideLists(boolean show) {
        vBox.setVisible(!show);
        lastAnalysedLabel.setVisible(!show);
    }

    public void refreshButtonPressed(ActionEvent actionEvent) {
        hashtagViewer.deleteCachedHashtag();
        mainController.closeCurrentTab();
        mainController.goToHashtagPane(hashtagViewer);
    }

    /**
     * Set the hashtag of the page. Get from db if exist, do search if not.
     * Prints hashtag simple infos (name, id...)
     *
     * @param hashtagViewer
     */
    public void setHashtagViewer(HashtagViewer hashtagViewer) {
        this.hashtagViewer = hashtagViewer;

        Platform.runLater(() -> initProgress(true, "Vérification si l'utilisateur est déjà dans la base."));

        //if in database, load everything from there
        if (hashtagViewer.verifyHahstagInDataBase()) {
            Platform.runLater(() -> progressLabel.setText("Récupération des données depuis la base de données."));
            hashtagViewer.setHashtagFromDataBase();
            setHashtagInfos();
            tweetList = hashtagToPrint.getTweets();
            hashtagViewer.setTweets(tweetList);
            setTops();
        } else {
            setHashtagInfos();
            //user not in db, get tweets from twitter
            threadGetTweets = new Thread(getTweets());
            threadGetTweets.setDaemon(true);
            threadGetTweets.start();
        }

    }

    private void setHashtagInfos() {
        hashtagToPrint = hashtagViewer.getHashtag();
        Platform.runLater(() -> {
            if (hashtagToPrint.getLastSearchDate() != null) {
                lastSearchLabel.setText("Dernière recherche effectuée le " + hoursAndDateFormat.format(hashtagToPrint.getLastSearchDate()));
                lastSearchLabel.setVisible(true);
            }
            refreshButton.setVisible(true);
            hashtagLabel.setText("#" + hashtagToPrint.getHashtag());
            hashtagLabel.setVisible(true);
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
        threadSetTopLinkedHashtag = new Thread(setTopLinkedHashtag());
        threadSetTopLinkedHashtag.setDaemon(true);
        threadSetTopLinkedHashtag.start();

        threadSetNumbers = new Thread(setNumberOfUniqueAccountAndNumberOfTweets());
        threadSetNumbers.setDaemon(true);
        threadSetNumbers.start();

        threadSetTopTweets = new Thread(setTopTweets());
        threadSetTopTweets.setDaemon(true);
        threadSetTopTweets.start();

        //Wait for the two other tasks
        while (threadSetTopLinkedHashtag.isAlive() || threadSetNumbers.isAlive() || threadSetTopTweets.isAlive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            lastSearchLabel.setText("Dernière recherche effectuée le " + hoursAndDateFormat.format(hashtagToPrint.getLastSearchDate()));
            if (tweetList.size() > 0) {
                String date = frenchSimpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
                lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " + date);
            } else {
                lastAnalysedLabel.setText("Aucun tweet n'est relié à ce hashtag");
            }
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            hideLists(false);
        });
    }

    /**
     * Called by setHashtag, it gets the tweets of a hashtag,
     * then create concurrent thread to analyze data
     * This task is very long.
     * Do not change the order. We have to wait to get all tweets before doing analysis
     */
    private Task<Void> getTweets() {
        Platform.runLater(() -> initProgress(false, "Récupération des tweets depuis Twitter.com"));

        try {
            //search and get tweets from hashtag first
            this.tweetList = hashtagViewer.searchByCount(hashtagToPrint.getHashtag(), progressBar, 4500, null);

            //Save the new search to DB
            hashtagToPrint.setLastSearchDate(new Date());
            hashtagToPrint.setTweets(tweetList);
            hashtagViewer.cacheHashtagToDataBase(hashtagToPrint);

            setTops();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task<Void> setTopLinkedHashtag() {
        hashtagLinked = hashtagViewer.topHashtag(hashtagViewer.getHashtagsLinked());

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagLinked.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtagLinked.get(hashtag).toString()));
            i++;
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
            topTenLinkedList.getItems().addAll(hashtagsToPrint);
            titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });

        return null;
    }

    private Task<Void> setNumberOfUniqueAccountAndNumberOfTweets() {
        Platform.runLater(() -> {
            nbUsersLabel.setText(NumberParser.spaceBetweenNumbers(hashtagViewer.getNumberOfUniqueAccounts()));
            nbUsersLabel.setVisible(true);
            nbTweetsLabel.setText(NumberParser.spaceBetweenNumbers(hashtagViewer.getNumberOfTweets()));
            nbTweetsLabel.setVisible(true);
            tweetsLabel.setVisible(true);
            usersLabel.setVisible(true);
        });
        return null;
    }

    private Task<Void> setTopTweets() {
        Map<Tweet, Integer> topTweets = hashtagViewer.topTweets(tweetList);
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

    @FXML
    private void addTweetsToList(List<Tweet> toptweets) {
        ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();
        try {
            if (hashtagViewer != null) {
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

                    tweetController.injectHashtagTabController(this);
                    tweetController.populate(tweet, true);
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
            progressLabel.setText(text);
        } else {
            progressBar.setProgress(-1);
            progressLabel.setText(text);
        }
    }

    /* ================ FAVORITES ================    */
    public void favourites() {
        //favourites
        favsViewer = new FavsViewer();
        hashtagToPrint = hashtagViewer.getHashtag();
        int fav = favsViewer.checkHashInFav(hashtagToPrint);
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
        favsViewer.addHashtagToFavourites(hashtagToPrint);
        int fav = favsViewer.checkHashInFav(hashtagToPrint);
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
        if (threadSetNumbers != null) {
            threadSetNumbers.interrupt();

        }
        if (threadSetTopLinkedHashtag != null) {
            threadSetTopLinkedHashtag.interrupt();
        }
        if (threadSetTopTweets != null) {
            threadSetTopTweets.interrupt();
        }
    }
}
