package fr.tse.ProjetInfo3.mvc.controller;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.tse.ProjetInfo3.mvc.utils.Dates.*;

/**
 * @author Sergiy
 * {@code This class acts as a Controller for the Tab which is used to display Interest Point statistics}
 */
public class StatisticsTabController {
    /**
     * Data
     **/

    private Map<User, Map<Date, Integer>> tweetsPerIntervalForEachUserMap;
    private Map<String, Map<Date, Integer>> tweetsPerIntervalForEachHashtagMap;
    private Map<Date, Integer> tweetsPerInterval;
    private Map<String, Integer> topTenLinkedHashtags;
    private InterestPoint interestPointToPrint;

    private List<Tweet> bigTweetList;
    private List<Tweet> reducedTweetListUsers;
    private List<Tweet> reducedTweetListHashtags;

    /**
     * Time
     **/

    private Date oldestTweet;
    private Date newestTweet;

    /**
     * Threads
     **/

    private Thread threadGetTweets;

    /**
     * Viewers
     **/
    private PIViewer piViewer;

    /**
     * StatisticsTab.fxml FXML elements
     **/

    @FXML
    private AnchorPane statisticsPane;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane pane1;

    @FXML
    private AnchorPane pane2;

    @FXML
    private AnchorPane pane3;

    @FXML
    private AnchorPane pane4;

    @FXML
    private Label piNameLabel;

    @FXML
    private AreaChart<String, Number> topFiveUserCadenceChart;

    @FXML
    private AreaChart<String, Number> topFiveHashtagCadenceChart;

    @FXML
    private AreaChart<String, Number> tweetCadenceChart;

    @FXML
    private PieChart pieChart;

    /**
     * Lists of Graphical elements
     **/

    private List<AnchorPane> chartContainers; // List of Pane chart containers
    private List<Chart> charts; // List of Charts

    /**
     * Constructors
     **/

    public StatisticsTabController() {
    }

    @FXML
    private void initialize() {
        chartContainers = new LinkedList<>();
        charts = new LinkedList<>();

        chartContainers.add(pane1);
        chartContainers.add(pane2);
        chartContainers.add(pane3);
        chartContainers.add(pane4);

        charts.add(topFiveHashtagCadenceChart);
        charts.add(topFiveUserCadenceChart);
        charts.add(tweetCadenceChart);
        charts.add(pieChart);

        showGraphs(false);
    }

    private void showGraphs(boolean show) {
        topFiveUserCadenceChart.setVisible(show);
        topFiveHashtagCadenceChart.setVisible(show);
        tweetCadenceChart.setVisible(show);
        pieChart.setVisible(show);
    }

    /**
     * This method launches a new Thread which executes the getTweets() method.
     *
     * @param piViewer
     */
    public void setDatas(PIViewer piViewer) {
        this.piViewer = piViewer;
        this.interestPointToPrint = piViewer.getSelectedInterestPoint();

        threadGetTweets = new Thread(getTweets());
        threadGetTweets.setDaemon(true);
        threadGetTweets.start();
        piNameLabel.setText(interestPointToPrint.getName());
    }

    /**
     * This method can acquire tweets and is used to build Charts.
     *
     * @return Task<Void>
     */
    private Task<Void> getTweets() {
        // Set a Blur effect
        BoxBlur blur = new BoxBlur(3, 3, 3);
        anchorPane.setEffect(blur);

        // Add a Progress Indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox progressIndicatorBox = new VBox(progressIndicator);
        progressIndicatorBox.setAlignment(Pos.CENTER);
        dialogStackPane.getChildren().add(progressIndicatorBox);

        try {
            // bigTweetList = piViewer.getTweets(new Label());

            Platform.runLater(() -> {
                // Get the date of the oldest tweet
                oldestTweet = bigTweetList.stream().min(Comparator.comparing(Tweet::getCreated_at)).get()
                        .getCreated_at();

                newestTweet = bigTweetList.stream().max(Comparator.comparing(Tweet::getCreated_at)).get()
                        .getCreated_at();

                // Acquire Data
                tweetsPerIntervalForEachUserMap = acquireDataOfFiveMostActiveUsers();
                tweetsPerIntervalForEachHashtagMap = acquireDataOfFiveTopHashtags();
                tweetsPerInterval = acquireDataOfTweetsPerTimeInterval();
                topTenLinkedHashtags = acquireDataOfTopLinkedHashtags();

                // Use the Data to generate Charts
                generateFiveMostActiveUserChart();
                generatTopFiveHashtagChart();
                generateTweetsPerIntervalChart();
                generateTopLinkedHashtagChart();

                makeChartsAppear(chartContainers, charts, 0);

                // Remove the Blur effect and the Progress Indicator
                anchorPane.setEffect(null);
                dialogStackPane.getChildren().remove(progressIndicatorBox);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* Data */

    /**
     * @return Returns a Map<User, Map<Date, Integer>>
     * @author Sergiy
     * {@code This method maps the 5 most active users to a an amount of tweets per interval of time}
     */
    public Map<User, Map<Date, Integer>> acquireDataOfFiveMostActiveUsers() {
        Map<User, Integer> tweetsPerUserMap = new HashMap<>();

        /** Fill the Map with a user as key and it's number of tweets **/
        for (Tweet tweet : bigTweetList) {
            User user = tweet.getUser();

            if (tweetsPerUserMap.containsKey(user)) {
                tweetsPerUserMap.put(user, tweetsPerUserMap.get(user) + 1);
            } else {
                tweetsPerUserMap.put(user, 0);
            }
        }

        /** Sort the map **/
        Map<User, Integer> sortedTweetsPerUserMap = tweetsPerUserMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<User> topFiveActiveUsers = new ArrayList<User>();

        Set<Entry<User, Integer>> setEntry1 = sortedTweetsPerUserMap.entrySet();
        Iterator<Entry<User, Integer>> iterator1 = setEntry1.iterator();

        /** Get top five active users **/
        int iter = 0;
        while (iter < 5) {

            try {
                Entry<User, Integer> e = iterator1.next();

                System.out.println(e.getKey() + " : " + e.getValue());

                topFiveActiveUsers.add(e.getKey());
            } catch (Exception e) {

            } finally {
                iter++;
            }
        }

        /* Get the tweets we will be working with */
        // A Predicate that predicates that a user is part of the five most active users
        Predicate<Tweet> byAppartenance = tweet -> topFiveActiveUsers.contains(tweet.getUser());

        // Filter the tweet list so that the remaining tweets are posted by one of
        // the 5 most active users.
        reducedTweetListUsers = bigTweetList.stream().filter(byAppartenance).collect(Collectors.toList());

        System.out.println("Sizes before and after");
        System.out.println(bigTweetList.size());
        System.out.println(reducedTweetListUsers.size());

        /* Timestamps */
        // Get current date
        //Date currentDate = new Date(System.currentTimeMillis());
        int minutesDifference = minutesDifference(newestTweet, oldestTweet);
        System.out.println(minutesDifference);

        double interval = minutesDifference / 20.0d;

        // Create 20 dates that will serve as intervals
        System.out.println("Intervals");
        System.out.println(oldestTweet);
        List<Date> dateIntervals = new LinkedList<Date>();

        if (minutesDifference > 1440) {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(roundDateToHour(addMinutesToDate(oldestTweet, (int) (i * interval))));
                System.out.println((int) (i * interval));
            }
        } else {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(addMinutesToDate(oldestTweet, (int) (i * interval)));
                System.out.println((int) (i * interval));
            }
        }


        // Let's now map each Date interval and the number of tweets with the
        // corresponding user
        Map<User, Map<Date, Integer>> tweetsPerIntervalForEachUserMap = new HashMap<>();

        for (Tweet tweet : reducedTweetListUsers) {
            User user = tweet.getUser();
            System.out.println(user.getName());

            Date dateTweet = tweet.getCreated_at();
            Date intervalDate = approximateInterval(dateIntervals, dateTweet);


            // Check if a User entry exists
            if (tweetsPerIntervalForEachUserMap.containsKey(user)) {
                if (tweetsPerIntervalForEachUserMap.get(user).containsKey(intervalDate)) {
                    tweetsPerIntervalForEachUserMap.get(user).put(intervalDate,
                            tweetsPerIntervalForEachUserMap.get(user).get(intervalDate) + 1);
                } else {
                    tweetsPerIntervalForEachUserMap.get(user).put(intervalDate, 0);
                }

            } else {
                tweetsPerIntervalForEachUserMap.put(user, new LinkedHashMap<Date, Integer>());

                for (int i = 0; i < dateIntervals.size(); i++) {
                    tweetsPerIntervalForEachUserMap.get(user).put(dateIntervals.get(i), 0);
                }

                tweetsPerIntervalForEachUserMap.get(user).put(intervalDate, 1);
            }
        }

        Set<Entry<User, Map<Date, Integer>>> setEntry2 = tweetsPerIntervalForEachUserMap.entrySet();
        Iterator<Entry<User, Map<Date, Integer>>> iterator2 = setEntry2.iterator();

        while (iterator2.hasNext()) {
            Entry<User, Map<Date, Integer>> entry2 = iterator2.next();
            Map<Date, Integer> tweetsNumberPerDate = entry2.getValue();

            /** Sort the map **/
            Map<Date, Integer> sortedTweetsNumberPerDate = tweetsNumberPerDate.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new));

            tweetsPerIntervalForEachUserMap.put(entry2.getKey(), sortedTweetsNumberPerDate);
        }

        return tweetsPerIntervalForEachUserMap;
    }

    /**
     * @return Returns a Map<User, Map<Date, Integer>>
     * @author Sergiy
     * {@code This method maps the 5 top Hashtags to a an amount of tweets per interval of time}
     */
    public Map<String, Map<Date, Integer>> acquireDataOfFiveTopHashtags() {
        Map<String, Integer> tweetsPerHashtagMap = new HashMap<>();

        List<String> hashtags = new ArrayList<>();

        // Method to get hashtag from retweets
        for (Tweet tweet : bigTweetList) {

            // if the tweet is retweeted, then we get the #'s of retweeted tweet
            if (tweet.getRetweeted_status() != null) {
                hashtags.addAll(tweet.getRetweeted_status().getEntities().getHashtags().stream()
                        .map(Tweet.hashtags::getText).collect(Collectors.toList()));
            } else
                // else, if the tweet is quoted, then we get the #'s of quoted tweet
                if (tweet.getQuoted_status() != null) {
                    hashtags.addAll(tweet.getQuoted_status().getEntities().getHashtags().stream()
                            .map(Tweet.hashtags::getText).collect(Collectors.toList()));
                }
            hashtags.addAll(tweet.getEntities().getHashtags().stream().map(Tweet.hashtags::getText)
                    .collect(Collectors.toList()));

        }

        for (String hashtagName : hashtags) {
            Integer occurrence = tweetsPerHashtagMap.get(hashtagName);
            tweetsPerHashtagMap.put(hashtagName, (occurrence == null) ? 1 : occurrence + 1);
        }

        /** Sort the map **/

        Map<String, Integer> sortedTweetsPerHashtagMap = tweetsPerHashtagMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<String> topFiveHashtags = new ArrayList<String>();

        Set<Entry<String, Integer>> setEntry1 = sortedTweetsPerHashtagMap.entrySet();
        Iterator<Entry<String, Integer>> iterator1 = setEntry1.iterator();

        /** Get top five hashtags **/

        int iter = 0;
        while (iter < 5) {
            try {
                Entry<String, Integer> e = iterator1.next();

                System.out.println(e.getKey() + " : " + e.getValue());

                topFiveHashtags.add(e.getKey());
            } catch (Exception e) {

            } finally {
                iter++;
            }
        }

        /** Timestamps **/
        // Get current date
        //Date currentDate = new Date(System.currentTimeMillis());
        int minutesDifference = minutesDifference(newestTweet, oldestTweet);
        double interval = minutesDifference / 20.0d;

        System.out.println(minutesDifference);

        // Create 20 dates that will serve as intervals
        System.out.println("Intervals");
        List<Date> dateIntervals = new LinkedList<Date>();

        if (minutesDifference > 1440) {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(roundDateToHour(addMinutesToDate(oldestTweet, (int) (i * interval))));
                System.out.println((int) (i * interval));
            }
        } else {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(addMinutesToDate(oldestTweet, (int) (i * interval)));
                System.out.println((int) (i * interval));
            }
        }

        // Let's now map each Date interval and the number of tweets with the
        // corresponding user
        Map<String, Map<Date, Integer>> tweetsPerIntervalForEachHashtagMap = new HashMap<>();

        for (Tweet tweet : bigTweetList) {
            Date dateTweet = tweet.getCreated_at();
            Date intervalDate = approximateInterval(dateIntervals, dateTweet);

            // System.out.println("Created at " + dateTweet);
            // System.out.println("Closest to " + intervalDate);

            for (Tweet.hashtags hashtag : tweet.getEntities().getHashtags()) {
                String hashtagName = hashtag.getText();

                if (topFiveHashtags.contains(hashtagName)) {
                    // Check if a Hashtag entry exists
                    if (tweetsPerIntervalForEachHashtagMap.containsKey(hashtagName)) {
                        if (tweetsPerIntervalForEachHashtagMap.get(hashtagName).containsKey(intervalDate)) {
                            tweetsPerIntervalForEachHashtagMap.get(hashtagName).put(intervalDate,
                                    tweetsPerIntervalForEachHashtagMap.get(hashtagName).get(intervalDate) + 1);
                        } else {
                            tweetsPerIntervalForEachHashtagMap.get(hashtagName).put(intervalDate, 0);
                        }

                    } else {
                        tweetsPerIntervalForEachHashtagMap.put(hashtagName, new LinkedHashMap<Date, Integer>());

                        for (int i = 0; i < dateIntervals.size(); i++) {
                            tweetsPerIntervalForEachHashtagMap.get(hashtagName).put(dateIntervals.get(i), 0);
                        }

                        tweetsPerIntervalForEachHashtagMap.get(hashtagName).put(intervalDate, 1);
                    }
                }
            }
        }

        Set<Entry<String, Map<Date, Integer>>> setEntry2 = tweetsPerIntervalForEachHashtagMap.entrySet();
        Iterator<Entry<String, Map<Date, Integer>>> iterator2 = setEntry2.iterator();

        while (iterator2.hasNext()) {
            Entry<String, Map<Date, Integer>> entry2 = iterator2.next();
            Map<Date, Integer> tweetsNumberPerDate = entry2.getValue();

            /** Sort the map **/
            Map<Date, Integer> sortedTweetsNumberPerDate = tweetsNumberPerDate.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new));

            tweetsPerIntervalForEachHashtagMap.put(entry2.getKey(), sortedTweetsNumberPerDate);
        }

        return tweetsPerIntervalForEachHashtagMap;
    }

    /**
     * @return Returns a Map<Date, Integer>
     * @author Sergiy
     * {@code This method returns an overall amount of tweets per time interval}
     */
    public Map<Date, Integer> acquireDataOfTweetsPerTimeInterval() {
        Map<Date, Integer> tweetsTimeInterval = new HashMap<>();

        /** Timestamps **/
        // Get current date
        //Date currentDate = new Date(System.currentTimeMillis());
        int minutesDifference = minutesDifference(newestTweet, oldestTweet);
        double interval = minutesDifference / 20.0d;

        System.out.println(minutesDifference);

        // Create 20 dates that will serve as intervals
        System.out.println("Intervals");
        List<Date> dateIntervals = new LinkedList<Date>();

        if (minutesDifference > 1440) {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(roundDateToHour(addMinutesToDate(oldestTweet, (int) (i * interval))));
                System.out.println((int) (i * interval));
            }
        } else {
            for (int i = 1; i < 21; i++) {
                dateIntervals.add(addMinutesToDate(oldestTweet, (int) (i * interval)));
                System.out.println((int) (i * interval));
            }
        }

        for (Tweet tweet : bigTweetList) {
            Date dateTweet = tweet.getCreated_at();
            Date intervalDate = approximateInterval(dateIntervals, dateTweet);

            // System.out.println("Created at " + dateTweet);
            // System.out.println("Closest to " + intervalDate);

            if (tweetsTimeInterval.containsKey(intervalDate)) {
                tweetsTimeInterval.put(intervalDate, tweetsTimeInterval.get(intervalDate) + 1);
            } else {
                tweetsTimeInterval.put(intervalDate, 0);
            }
        }

        Map<Date, Integer> sortedTweetsTimeInterval = tweetsTimeInterval.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return sortedTweetsTimeInterval;
    }

    /**
     * @return Returns a Map<String, Integer>
     * @author Sergiy, Sobun
     * {@code This method maps the 10 most popular hashtags to a an amount of occurences of these hashtags in tweets}
     */
    public Map<String, Integer> acquireDataOfTopLinkedHashtags() {
        Map<String, Integer> tweetsPerHashtagMap = new HashMap<>();

        List<String> hashtags = new ArrayList<>();

        // Method to get hashtag from retweets
        for (Tweet tweet : bigTweetList) {

            // if the tweet is retweeted, then we get the #'s of retweeted tweet
            if (tweet.getRetweeted_status() != null) {
                hashtags.addAll(tweet.getRetweeted_status().getEntities().getHashtags().stream()
                        .map(Tweet.hashtags::getText).collect(Collectors.toList()));
            } else
                // else, if the tweet is quoted, then we get the #'s of quoted tweet
                if (tweet.getQuoted_status() != null) {
                    hashtags.addAll(tweet.getQuoted_status().getEntities().getHashtags().stream()
                            .map(Tweet.hashtags::getText).collect(Collectors.toList()));
                }
            hashtags.addAll(tweet.getEntities().getHashtags().stream().map(Tweet.hashtags::getText)
                    .collect(Collectors.toList()));

        }

        for (String hashtagName : hashtags) {
            Integer occurrence = tweetsPerHashtagMap.get(hashtagName);
            tweetsPerHashtagMap.put(hashtagName, (occurrence == null) ? 1 : occurrence + 1);
        }

        /** Sort the map **/

        Map<String, Integer> sortedTweetsPerHashtagMap = tweetsPerHashtagMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        Map<String, Integer> topTenHashtags = new HashMap<>();

        Set<Entry<String, Integer>> setEntry1 = sortedTweetsPerHashtagMap.entrySet();
        Iterator<Entry<String, Integer>> iterator1 = setEntry1.iterator();

        /** Get top ten hashtags **/

        int iter = 0;
        while (iter < 10) {

            try {
                Entry<String, Integer> e = iterator1.next();
                System.out.println(e.getKey() + " : " + e.getValue());

                topTenHashtags.put(e.getKey(), e.getValue());
            } catch (Exception e) {

            } finally {
                iter++;
            }
        }

        /** Finally sort the map **/

        Map<String, Integer> sortedTopTenHashtags = topTenHashtags.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return sortedTopTenHashtags;
    }

    /* Animations */

    /**
     * @param chartContainers, chart
     * @author Sergiy
     * {@code This method puts all charts in their panes, one after another, thus creating an animated sequence}
     */
    void makeChartsAppear(List<AnchorPane> chartContainers, List<Chart> charts, int counter) {
        int i = counter;

        /** Transition 2 **/
        RotateTransition rotate2 = new RotateTransition();
        rotate2.setAxis(Rotate.Y_AXIS);
        rotate2.setByAngle(90);
        rotate2.setDuration(Duration.millis(250));
        rotate2.setAutoReverse(true);
        rotate2.setNode(chartContainers.get(i));

        /** Transition 1 **/
        RotateTransition rotate1 = new RotateTransition();
        rotate1.setAxis(Rotate.Y_AXIS);
        rotate1.setByAngle(90);
        rotate1.setDuration(Duration.millis(250));
        rotate1.setAutoReverse(false);
        rotate1.setNode(chartContainers.get(i));

        rotate1.setOnFinished(e -> {
            charts.get(i).setVisible(true);
            rotate2.play();
            //chartContainers.get(i).getChildren().add(charts.get(i));

            if (i < 3) {
                makeChartsAppear(chartContainers, charts, i + 1);
            }
        });

        /** Transition 0 **/
        RotateTransition rotateInitial = new RotateTransition();
        rotateInitial.setAxis(Rotate.Y_AXIS);
        rotateInitial.setByAngle(180);
        rotateInitial.setDuration(Duration.millis(1));
        rotateInitial.setAutoReverse(false);
        rotateInitial.setNode(chartContainers.get(i));

        rotateInitial.setOnFinished(e -> {
            rotate1.play();
        });

        rotateInitial.play();
    }

    /** Processing **/

    /**
     * @author Sergiy
     * {@code This method builds the chart of the five most active users}
     */
    void generateFiveMostActiveUserChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM");

        /* Iterator configuration **/
        Set<Entry<User, Map<Date, Integer>>> setEntry1 = tweetsPerIntervalForEachUserMap.entrySet();
        Iterator<Entry<User, Map<Date, Integer>>> iterator1 = setEntry1.iterator();

        /* Iterate over the data and fill the chart **/
        while (iterator1.hasNext()) {
            Entry<User, Map<Date, Integer>> entry1 = iterator1.next();

            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(entry1.getKey().getScreen_name());

            Map<Date, Integer> tweetsOverTime = entry1.getValue();

            Set<Entry<Date, Integer>> setEntry2 = tweetsOverTime.entrySet();
            Iterator<Entry<Date, Integer>> iterator2 = setEntry2.iterator();

            while (iterator2.hasNext()) { // Since the map maps a map, we need to reiterate each time inside the map
                Entry<Date, Integer> entry2 = iterator2.next();

                series.getData().add(
                        new XYChart.Data<String, Number>(simpleDateFormat.format(entry2.getKey()), entry2.getValue()));
            }

            Platform.runLater(() -> {
                topFiveUserCadenceChart.getData().add(series);
            });
        }

    }

    /**
     * @author Sergiy
     * {@code This method builds the chart of the five most used hashtags}
     */
    void generatTopFiveHashtagChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM");

        /* Iterator configuration **/
        Set<Entry<String, Map<Date, Integer>>> setEntry1 = tweetsPerIntervalForEachHashtagMap.entrySet();
        Iterator<Entry<String, Map<Date, Integer>>> iterator1 = setEntry1.iterator();

        /* Iterate over the data and fill the chart **/
        while (iterator1.hasNext()) {
            Entry<String, Map<Date, Integer>> entry1 = iterator1.next();

            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(entry1.getKey());

            Map<Date, Integer> tweetsOverTime = entry1.getValue();

            Set<Entry<Date, Integer>> setEntry2 = tweetsOverTime.entrySet();
            Iterator<Entry<Date, Integer>> iterator2 = setEntry2.iterator();

            while (iterator2.hasNext()) {
                Entry<Date, Integer> entry2 = iterator2.next();

                series.getData().add(
                        new XYChart.Data<String, Number>(simpleDateFormat.format(entry2.getKey()), entry2.getValue()));
            }

            Platform.runLater(() -> {
                topFiveHashtagCadenceChart.getData().add(series);
            });
        }
    }

    /**
     * @author Sergiy
     * {@code This method builds the chart of the amount of tweets in time}
     */
    void generateTweetsPerIntervalChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM");

        // Iterator configuration
        Set<Entry<Date, Integer>> setEntry1 = tweetsPerInterval.entrySet();
        Iterator<Entry<Date, Integer>> iterator1 = setEntry1.iterator();

        //Create series
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();

        //Iterate over the data and fill the chart
        while (iterator1.hasNext()) {
            Entry<Date, Integer> entry1 = iterator1.next();

            series.getData()
                    .add(new XYChart.Data<String, Number>(simpleDateFormat.format(entry1.getKey()), entry1.getValue()));

        }
        Platform.runLater(() -> {
            tweetCadenceChart.getData().add(series);
        });
    }

    /**
     * @author Sergiy, Sobun
     * {@code This method builds the pie chart of the ten most used hashtags}
     */
    void generateTopLinkedHashtagChart() {
        Platform.runLater(() -> {
            /* Fill chart with data */
            topTenLinkedHashtags.forEach((String, Integer) -> {
                pieChart.getData().add(new PieChart.Data(String, Integer));
            });
        });
    }

    /**
     * Kill Threads
     */
    public void killThreads() {
        if (threadGetTweets != null) {
            threadGetTweets.interrupt();
        }
    }

    /**
     * Getters and Setters
     */
    void setTweetList(List<Tweet> bigTweetList) {
        this.bigTweetList = bigTweetList;
    }
}
