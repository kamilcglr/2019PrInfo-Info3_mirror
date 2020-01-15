package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.frenchSimpleDateFormat;
import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.hoursAndDateFormat;

/**
 * @author Sobun UNG
 * @author kamilcaglar
 **/
public class UserTabController {
    private MainController mainController;

    private UserViewer userViewer;

    private FavsViewer favsViewer;

    private User userToPrint;

    private UserApp userApp;

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
    private JFXButton favoriteToggle;

    @FXML
    private FontIcon favoriteIcon;

    private boolean isFavorite;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label progressLabel;

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
    @FXML
    private HBox profileImageBox;
    @FXML
    private AreaChart<String, Number> tweetCadenceChart;


    private Map<Date, Integer> tweetsPerInterval;


    /**************************************************************/
    /*Controller can access to main Controller */
    public void injectMainController(MainController mainController, FavsViewer favsViewer) {
        this.mainController = mainController;
        if (mainController.isConnected()) {
            this.userApp = mainController.getUserApp();
            this.favsViewer = favsViewer;
        }
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
            hideLists(true);
            if (hideAll) {
                username.setVisible(false);
                twittername.setVisible(false);
                description.setVisible(false);
                nbTweet.setVisible(false);
                nbFollowers.setVisible(false);
                nbFollowing.setVisible(false);
                profileImageBox.setVisible(false);
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
        threadSetTopHashtags = new Thread(setTopHashtags());
        threadSetTopHashtags.setDaemon(true);
        threadSetTopHashtags.start();

        //Set top tweets, but it can be dangerous with the shared tweetList, we have to test
        threadSetTopTweets = new Thread(setTopTweets());
        threadSetTopTweets.setDaemon(true);
        threadSetTopTweets.start();

        tweetsPerInterval = acquireDataOfTweetsPerTimeInterval();
        generateTweetsPerIntervalChart();

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
        profileImageBox.setVisible(true);
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
    public void verifyFavorites() {
        favoriteToggle.setVisible(true);
        if (favsViewer.checkUserInFav(userToPrint)) {
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
            favsViewer.removeUserFromFavourites(userToPrint);
        } else {
            isFavorite = true;
            favoriteIcon.setIconLiteral("fas-heart");
            favsViewer.addUserToFavourites(userToPrint);
        }
    }

    /**
     * Build the chart of tweets cadence
     */
    void generateTweetsPerIntervalChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/YYYY");

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
        Platform.runLater(()->{
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

    /* Time **/

    /**
     * @return Returns a Date
     * @author Sergiy
     * {@code This method returns Date, created by adding time (in minutes) to an another Date}
     */
    public Date addMinutesToDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    /**
     * @return Returns a Date
     * @author Sergiy
     * {@code This method returns Date within the interval list that is the closest to the parameter.
     * In other terms, it allows to place the tweets in clusters of time}
     */
    private Date approximateInterval(List<Date> dateIntervals, Date tweetDate) {
        long minDifference = Long.MAX_VALUE;
        Date closestDate = null;

        for (Date date : dateIntervals) {
            long differenceInMillis = Math.abs(date.getTime() - tweetDate.getTime());

            if (differenceInMillis < minDifference) {
                minDifference = differenceInMillis;
                closestDate = date;
            }
        }
        return closestDate;
    }

    /**
     * @return Returns an integer
     * @author Sergiy
     * {@code This method calculates a difference (in minutes between two dates)}
     */
    private int minutesDifference(Date start, Date end) {
        final int MILLIS_TO_HOUR = 1000 * 60;

        return (int) ((start.getTime() - end.getTime()) / MILLIS_TO_HOUR);
    }

    /**
     * @return Returns a Date
     * @author Sergiy
     * {@code This method rounds a Date so that everything below hours is rounded to 0}
     */
    public Date roundDateToHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
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
