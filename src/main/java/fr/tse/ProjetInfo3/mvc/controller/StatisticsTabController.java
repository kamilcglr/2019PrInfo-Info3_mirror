package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

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

    /**
     * Lists of Graphical elements
     **/

    private List<Pane> chartContainers; // List of Pane chart containers
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

    /** Data **/

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

        /** Get the tweets we will be working with **/
        // A Predicate that predicates that a user is part of the five most active users
        Predicate<Tweet> byAppartenance = tweet -> topFiveActiveUsers.contains(tweet.getUser());

        // Filter the tweew list so that the remaining tweets are posted by one of
        // the 5 most active users.
        reducedTweetListUsers = bigTweetList.stream().filter(byAppartenance).collect(Collectors.toList());

        System.out.println("Sizes before and after");
        System.out.println(bigTweetList.size());
        System.out.println(reducedTweetListUsers.size());

        /** Timestamps **/
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

            // System.out.println("Created at " + dateTweet);
            // System.out.println("Closest to " + intervalDate);

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

        /** Get the tweets we will be working with **/

        /** Now working with all tweets **/

        // A Predicate that predicates that a hashtag is used by a tweet

        // Predicate<Tweet> byAppartenance = tweet ->
        // topFiveHashtags.stream().anyMatch(tweet.getEntities().getHashtags()
        // .stream().map(Tweet.hashtags::getText).collect(Collectors.toSet())::contains);

        // Filter the tweew list so that the remaining tweets contain at least one of
        // the top 5 hashtags.

        // reducedTweetListHashtags =
        // bigTweetList.stream().filter(byAppartenance).collect(Collectors.toList());

        // System.out.println("Sizes before and after");
        // System.out.println(bigTweetList.size());
        // System.out.println(reducedTweetListHashtags.size());

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

    /** Time **/

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
     * @return Returns an integer
     * @author Sergiy
     * {@code This method calculates a difference (in minutes between two dates)}
     */
    private int minutesDifference(Date start, Date end) {
        final int MILLIS_TO_HOUR = 1000 * 60;

        int difference = (int) ((start.getTime() - end.getTime()) / MILLIS_TO_HOUR);
        return difference;
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

    /** Animations **/

    /**
     * @param pane, chart
     * @author Sergiy
     * {@code This method puts all charts in their panes, one after another, thus creating an animated sequence}
     */
    void makeChartsAppear(List<Pane> chartContainers, List<Chart> charts, int counter) {
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
            rotate2.play();
            chartContainers.get(i).getChildren().add(charts.get(i));

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

        /** Axis creation **/

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        /** Chart creation **/

        final AreaChart<String, Number> ac = new AreaChart<String, Number>(xAxis, yAxis);
        ac.setTitle("Nombre de tweets des 5 utilisateurs les plus actifs");
        ac.setMaxHeight(Integer.MAX_VALUE);
        ac.setMaxHeight(Integer.MAX_VALUE);
        AnchorPane.setBottomAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setTopAnchor(ac, 0.0);

        // xAxis.setLabel("Temps");
        yAxis.setLabel("Nombre de tweets");

        /** Iterator configuration **/

        Set<Entry<User, Map<Date, Integer>>> setEntry1 = tweetsPerIntervalForEachUserMap.entrySet();
        Iterator<Entry<User, Map<Date, Integer>>> iterator1 = setEntry1.iterator();

        /** Iterate over the data and fill the chart **/

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

            ac.getData().add(series);
        }

        ac.setLegendSide(Side.RIGHT); // Put the legend on the right

        // makeChartAppear(pane1, ac);
        charts.add(ac);
    }

    /**
     * @author Sergiy
     * {@code This method builds the chart of the five most used hashtags}
     */
    void generatTopFiveHashtagChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM");

        /** Axis creation **/

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        /** Chart creation **/

        final AreaChart<String, Number> ac = new AreaChart<String, Number>(xAxis, yAxis);
        ac.setTitle("Nombre de tweets des 5 hashtags les plus utilisés");
        ac.setMaxHeight(Integer.MAX_VALUE);
        ac.setMaxHeight(Integer.MAX_VALUE);
        AnchorPane.setBottomAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setTopAnchor(ac, 0.0);
        // xAxis.setLabel("Temps");
        yAxis.setLabel("Nombre de tweets");

        /** Iterator configuration **/

        Set<Entry<String, Map<Date, Integer>>> setEntry1 = tweetsPerIntervalForEachHashtagMap.entrySet();
        Iterator<Entry<String, Map<Date, Integer>>> iterator1 = setEntry1.iterator();

        /** Iterate over the data and fill the chart **/

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

            ac.getData().add(series);
        }

        ac.setLegendSide(Side.RIGHT);
        // makeChartAppear(pane3, ac);
        charts.add(ac);
    }

    /**
     * @author Sergiy
     * {@code This method builds the chart of the amount of tweets in time}
     */
    void generateTweetsPerIntervalChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM");

        /** Axis creation **/

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        /** Chart creation **/

        final AreaChart<String, Number> ac = new AreaChart<String, Number>(xAxis, yAxis);
        ac.setTitle("Nombre de tweets total par intervalle de temps");
        ac.setMaxHeight(Integer.MAX_VALUE);
        ac.setMaxHeight(Integer.MAX_VALUE);
        AnchorPane.setBottomAnchor(ac, 0.0);
        AnchorPane.setLeftAnchor(ac, 0.0);
        AnchorPane.setRightAnchor(ac, 0.0);
        AnchorPane.setTopAnchor(ac, 0.0);

        yAxis.setLabel("Nombre de tweets");

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
        ac.getData().add(series);
        ac.setLegendVisible(false);

        charts.add(ac);
    }

    /**
     * @author Sergiy, Sobun
     * {@code This method builds the pie chart of the ten most used hashtags}
     */
    void generateTopLinkedHashtagChart() {

        /* Chart creation **/
        final PieChart pc = new PieChart();
        pc.setTitle("Répartition du top des hashtags liés");
        pc.setMaxHeight(Integer.MAX_VALUE);
        pc.setMaxWidth(Integer.MAX_VALUE);
        AnchorPane.setBottomAnchor(pc, 0.0);
        AnchorPane.setLeftAnchor(pc, 0.0);
        AnchorPane.setRightAnchor(pc, 0.0);
        AnchorPane.setTopAnchor(pc, 0.0);

        /* Fill chart with data */
        topTenLinkedHashtags.forEach((String, Integer) -> {
            pc.getData().add(new PieChart.Data(String, Integer));
        });

        pc.setLegendSide(Side.RIGHT);

        charts.add(pc);
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
