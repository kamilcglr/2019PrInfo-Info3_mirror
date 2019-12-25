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

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
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
 *         {@code This class acts as a Controller for the Tab which is used to display Interest Point statistics}
 */
public class StatisticsTabController {
	/** Data **/
	Map<User, Map<Date, Integer>> tweetsPerIntervalForEachUserMap;
	Map<Hashtag, Map<Date, Integer>> tweetsPerIntervalForEachHashtagMap;
	Map<Date, Integer> tweetsPerInterval;

	/** Time **/
	Date oldestTweet;

	private List<Tweet> bigTweetList;
	private List<Tweet> reducedTweetList;

	private PIViewer piViewer;

	private Thread threadGetTweets;

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
	private Pane pane1;

	@FXML
	private Pane pane2;

	@FXML
	private Pane pane3;

	@FXML
	private Pane pane4;

	public StatisticsTabController() {

	}

	@FXML
	private void initialize() {
		List<Pane> chartContainers = new ArrayList<>();
		
		chartContainers.add(pane1);
		chartContainers.add(pane2);
		chartContainers.add(pane3);
		chartContainers.add(pane4);
	}

	public void setDatas(PIViewer piViewer) {
		this.piViewer = piViewer;

		threadGetTweets = new Thread(getTweets());
		threadGetTweets.setDaemon(true);
		threadGetTweets.start();
	}

	private Task<Void> getTweets() {
		BoxBlur blur = new BoxBlur(3, 3, 3);
		anchorPane.setEffect(blur);

		ProgressIndicator progressIndicator = new ProgressIndicator();
		VBox progressIndicatorBox = new VBox(progressIndicator);
		progressIndicatorBox.setAlignment(Pos.CENTER);
		dialogStackPane.getChildren().add(progressIndicatorBox);

		try {
			bigTweetList = piViewer.getTweets(new Label());

			Platform.runLater(() -> {
				oldestTweet = bigTweetList.stream().min(Comparator.comparing(Tweet::getCreated_at)).get()
						.getCreated_at();

				tweetsPerIntervalForEachUserMap = acquireDataOfFiveMostActiveUsers();
				tweetsPerInterval = acquireDataOfTweetsPerTimeInterval();
				
				generateFiveMostActiveUserChart();
				generateTweetsPerIntervalChart();
				
				anchorPane.setEffect(null);
				dialogStackPane.getChildren().remove(progressIndicatorBox);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// Tweets per time of 5 most active users
	// Tweets per time of 5 most popular hashtags
	// Tweets per time
	/** Data **/
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
			Entry<User, Integer> e = iterator1.next();
			System.out.println(e.getKey() + " : " + e.getValue());

			topFiveActiveUsers.add(e.getKey());
			iter++;
		}

		/** Get the tweets we will be working with **/
		// A Predicate that predicates that a user is part of the five most active users
		Predicate<Tweet> byAppartenance = tweet -> topFiveActiveUsers.contains(tweet.getUser());

		// Filter the tweew list so that the remaining tweets are posted by one of
		// the 5 most active users.
		reducedTweetList = bigTweetList.stream().filter(byAppartenance).collect(Collectors.toList());

		System.out.println("Sizes before and after");
		System.out.println(bigTweetList.size());
		System.out.println(reducedTweetList.size());

		/** Timestamps **/
		// Get current date
		Date currentDate = new Date(System.currentTimeMillis());
		int hoursDifference = minutesDifference(currentDate, oldestTweet);
		double interval = hoursDifference / 20;

		System.out.println(hoursDifference);

		// Create 20 dates that will serve as intervals
		System.out.println("Intervals");
		List<Date> dateIntervals = new LinkedList<Date>();
		for (int i = 1; i < 21; i++) {
			dateIntervals.add(roundDateToHour(addMinutesToDate(oldestTweet, (int) (i * interval))));
			System.out.println(dateIntervals.get(i - 1));
		}

		// Let's now map each Date interval and the number of tweets with the
		// corresponding user
		Map<User, Map<Date, Integer>> tweetsPerIntervalForEachUserMap = new HashMap<>();

		for (Tweet tweet : reducedTweetList) {
			User user = tweet.getUser();
			System.out.println(user.getName());

			Date dateTweet = tweet.getCreated_at();
			Date intervalDate = approximateInterval(dateIntervals, dateTweet);

			System.out.println("Created at " + dateTweet);
			System.out.println("Closest to " + intervalDate);

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
					.sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).collect(Collectors.toMap(
							Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			
			tweetsPerIntervalForEachUserMap.put(entry2.getKey(), sortedTweetsNumberPerDate);
		}

		return tweetsPerIntervalForEachUserMap;
	}

	public Map<Date, Integer> acquireDataOfTweetsPerTimeInterval() {
		Map<Date, Integer> tweetsTimeInterval = new HashMap<>();

		/** Timestamps **/
		// Get current date
		Date currentDate = new Date(System.currentTimeMillis());
		int hoursDifference = minutesDifference(currentDate, oldestTweet);
		double interval = hoursDifference / 20;

		System.out.println(hoursDifference);

		// Create 20 dates that will serve as intervals
		System.out.println("Intervals");
		List<Date> dateIntervals = new LinkedList<Date>();
		for (int i = 1; i < 21; i++) {
			dateIntervals.add(roundDateToHour(addMinutesToDate(oldestTweet, (int) (i * interval))));
			System.out.println(dateIntervals.get(i - 1));
		}

		for (Tweet tweet : bigTweetList) {
			Date dateTweet = tweet.getCreated_at();
			Date intervalDate = approximateInterval(dateIntervals, dateTweet);

			System.out.println("Created at " + dateTweet);
			System.out.println("Closest to " + intervalDate);

			if (tweetsTimeInterval.containsKey(intervalDate)) {
				tweetsTimeInterval.put(intervalDate, tweetsTimeInterval.get(intervalDate) + 1);
			} else {
				tweetsTimeInterval.put(intervalDate, 0);
			}
		}
		
		Map<Date, Integer> sortedTweetsTimeInterval = tweetsTimeInterval.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

		return sortedTweetsTimeInterval;
	}

	/** Time **/
	public Date addMinutesToDate(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);

		return calendar.getTime();
	}

	private int minutesDifference(Date start, Date end) {
		final int MILLIS_TO_HOUR = 1000 * 60;
		return (int) (start.getTime() - end.getTime()) / MILLIS_TO_HOUR;
	}

	// Get the Date within the interval list that is the closest to the parameter
	// Date
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

	public Date roundDateToHour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/** Animations **/
	void rotateNode(Node node) {
		RotateTransition rotate = new RotateTransition();

		// Setting Axis of rotation
		rotate.setAxis(Rotate.Y_AXIS);

		// setting the angle of rotation
		rotate.setByAngle(90);

		// setting cycle count of the rotation
		// rotate.setCycleCount(500);

		// Setting duration of the transition
		rotate.setDuration(Duration.millis(1000));

		// the transition will be auto reversed by setting this to true
		// rotate.setAutoReverse(true);

		// setting Rectangle as the node onto which the
		// transition will be applied
		rotate.setNode(node);

		// playing the transition
		rotate.play();
	}

	void makeChartAppear(Pane pane, AreaChart<?, ?> ac) {
		RotateTransition rotate2 = new RotateTransition();
		rotate2.setAxis(Rotate.Y_AXIS);
		rotate2.setByAngle(90);
		rotate2.setDuration(Duration.millis(500));
		rotate2.setAutoReverse(true);
		rotate2.setNode(pane);

		RotateTransition rotate1 = new RotateTransition();
		rotate1.setAxis(Rotate.Y_AXIS);
		rotate1.setByAngle(90);
		rotate1.setDuration(Duration.millis(500));
		rotate1.setAutoReverse(false);
		rotate1.setNode(pane);

		rotate1.setOnFinished(e -> {
			rotate2.play();
			pane.getChildren().add(ac);
		});

		RotateTransition rotateInitial = new RotateTransition();
		rotateInitial.setAxis(Rotate.Y_AXIS);
		rotateInitial.setByAngle(180);
		rotateInitial.setDuration(Duration.millis(1));
		rotateInitial.setAutoReverse(false);
		rotateInitial.setNode(pane);

		rotateInitial.setOnFinished(e -> {
			rotate1.play();
		});

		rotateInitial.play();
	}

	void makeAllChartsAppear(List<Pane> chartContainers, AreaChart<Number, Number> ac) {
		/*
		 * Platform.runLater(() -> { for (int i = 0; i < chartContainers.size(); i++) {
		 * 
		 * 
		 * }
		 * 
		 * });
		 */

		makeChartAppear(chartContainers.get(0), ac);
		makeChartAppear(chartContainers.get(1), ac);

	}

	/** Processing **/
	void generateFiveMostActiveUserChart() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();

		final AreaChart<String, Number> ac = new AreaChart<String, Number>(xAxis, yAxis);
		ac.setTitle("Number of Tweets of the most active users");

		ac.setPrefSize(500, 600);
		ac.setMinSize(500, 600);
		ac.setMaxSize(500, 600);

		xAxis.setLabel("Time");
		yAxis.setLabel("Number of Tweets");

		Set<Entry<User, Map<Date, Integer>>> setEntry1 = tweetsPerIntervalForEachUserMap.entrySet();
		Iterator<Entry<User, Map<Date, Integer>>> iterator1 = setEntry1.iterator();

		while (iterator1.hasNext()) {
			Entry<User, Map<Date, Integer>> entry1 = iterator1.next();

			XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
			series.setName(entry1.getKey().getScreen_name());

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
		
		makeChartAppear(pane1, ac);
	}

	void generateTweetsPerIntervalChart() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();

		final AreaChart<String, Number> ac = new AreaChart<String, Number>(xAxis, yAxis);
		ac.setTitle("Number of Tweets per Time Interval");

		ac.setPrefSize(500, 600);
		ac.setMinSize(500, 600);
		ac.setMaxSize(500, 600);

		xAxis.setLabel("Time");
		yAxis.setLabel("Number of Tweets");

		Set<Entry<Date, Integer>> setEntry1 = tweetsPerInterval.entrySet();
		Iterator<Entry<Date, Integer>> iterator1 = setEntry1.iterator();

		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("Nombre de Tweets");
		
		while (iterator1.hasNext()) {
			Entry<Date, Integer> entry1 = iterator1.next();

			
			series.getData().add(new XYChart.Data<String, Number>(simpleDateFormat.format(entry1.getKey()), entry1.getValue()));
			
			
		}
		ac.getData().add(series);
		
		makeChartAppear(pane2, ac);
	}

	void dynamicallyCompleteCharts() {

	}
}
