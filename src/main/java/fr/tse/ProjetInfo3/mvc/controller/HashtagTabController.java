package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static fr.tse.ProjetInfo3.mvc.utils.Dates.*;

public class HashtagTabController {
    /**
     * Non FXML elements
     */
    private MainController mainController;

    private HashtagViewer hashtagViewer;

    private Hashtag hashtagToPrint;

    private FavsViewer favsViewer;

    private UserApp userApp = new UserApp();

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
    private JFXListView<JFXListCell> listTweets;
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
    private JFXButton favoriteToggle;
    @FXML
    private FontIcon favoriteIcon;

    private boolean isFavorite;
    @FXML
    private Label lastSearchLabel;

    @FXML
    private AreaChart<String, Number> tweetCadenceChart;

    private Map<Date, Integer> tweetsPerInterval;

    /**************************************************************/
    /*Controller can access to main Controller */
    public void injectMainController(MainController mainController, FavsViewer favsViewer) {
        this.mainController = mainController;
        if (mainController.isConnected()){
            this.userApp = mainController.getUserApp();
            this.favsViewer = favsViewer;
        }
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
        if (mainController.isConnected()) {
            verifyFavorites();
        } else {
            favoriteToggle.setVisible(false);
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

        tweetsPerInterval = acquireDataOfTweetsPerTimeInterval();
        generateTweetsPerIntervalChart();

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
            progressLabel.setText(text);
        } else {
            progressBar.setProgress(-1);
            progressLabel.setText(text);
        }
    }

    /* ================ FAVORITES ================    */
    public void verifyFavorites() {
        favoriteToggle.setVisible(true);
        if (favsViewer.checkHashInFav(hashtagToPrint)) {
            isFavorite = true;
            favoriteIcon.setIconLiteral("fas-heart");
        } else {
            isFavorite = false;
            favoriteIcon.setIconLiteral("far-heart");
        }
    }

    @FXML
    private void favouriteTogglePressed() {
        if (isFavorite) {
            isFavorite = false;
            favoriteIcon.setIconLiteral("far-heart");
            favsViewer.removeHashtagFromFavourites(hashtagToPrint);
        } else {
            isFavorite = true;
            favoriteIcon.setIconLiteral("fas-heart");
            favsViewer.addHashtagToFavourites(hashtagToPrint);
        }
    }

    /**
     * Build the chart of tweets cadence
     */
    void generateTweetsPerIntervalChart() {
        Date oldestTweet = tweetList.stream().min(Comparator.comparing(Tweet::getCreated_at)).get()
                .getCreated_at();

        Date newestTweet = tweetList.stream().max(Comparator.comparing(Tweet::getCreated_at)).get()
                .getCreated_at();
        /* Timestamps **/
        // Get current date
        int minutesDifference = minutesDifference(newestTweet, oldestTweet);
        SimpleDateFormat simpleDateFormat;
        if (minutesDifference < 24 * 60 ) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        } else {
            if(minutesDifference < 24 * 60 * 20) {
                simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/YYYY");
            }else{
                simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
            }
        }

        /* Iterator configuration **/

        Set<Map.Entry<Date, Integer>> setEntry1 = tweetsPerInterval.entrySet();
        Iterator<Map.Entry<Date, Integer>> iterator1 = setEntry1.iterator();

        /* Create series **/

        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();

        /* Iterate over the data and fill the chart **/
        while (iterator1.hasNext()) {
            Map.Entry<Date, Integer> entry1 = iterator1.next();
            series.getData()
                    .add(new XYChart.Data<String, Number>(simpleDateFormat.format(entry1.getKey()), entry1.getValue()));

        }
        Platform.runLater(() -> {
            tweetCadenceChart.getData().add(series);
            tweetCadenceChart.setLegendVisible(false);
        });
    }

    /**
     * @return Returns a Map<Date, Integer>
     * @author Sergiy
     * {@code This method returns an overall amount of tweets per time interval}
     */
    public Map<Date, Integer> acquireDataOfTweetsPerTimeInterval() {
        Map<Date, Integer> tweetsTimeInterval = new HashMap<>();
        Date oldestTweet = tweetList.stream().min(Comparator.comparing(Tweet::getCreated_at)).get()
                .getCreated_at();

        Date newestTweet = tweetList.stream().max(Comparator.comparing(Tweet::getCreated_at)).get()
                .getCreated_at();
        /* Timestamps **/
        // Get current date
        int minutesDifference = minutesDifference(newestTweet, oldestTweet);
        double interval = minutesDifference / 20.0d;

        List<Date> dateIntervals = new LinkedList<Date>();

        if (minutesDifference > 1440) {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(roundDateToHour(addMinutesToDate(oldestTweet, (int) (i * interval))));
            }
        } else {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(addMinutesToDate(oldestTweet, (int) (i * interval)));
            }
        }

        for (Tweet tweet : tweetList) {
            Date dateTweet = tweet.getCreated_at();
            Date intervalDate = approximateInterval(dateIntervals, dateTweet);

            if (tweetsTimeInterval.containsKey(intervalDate)) {
                tweetsTimeInterval.put(intervalDate, tweetsTimeInterval.get(intervalDate) + 1);
            } else {
                tweetsTimeInterval.put(intervalDate, 0);
            }
        }

        return tweetsTimeInterval.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
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
