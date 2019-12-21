package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
import javafx.scene.chart.NumberAxis;
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
 *         {@code This class acts as a Controller for the Tab which is used to display Interest Point statistics}
 */
public class StatisticsTabController {
	/** Time **/
	Date oldestTweet;

	private List<Tweet> bigTweetList;

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

	@FXML
	private Pane pane5;

	@FXML
	private Pane pane6;

	public StatisticsTabController() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FXML
	private void initialize() {
		final NumberAxis xAxis = new NumberAxis(1, 31, 1);
		final NumberAxis yAxis = new NumberAxis();
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Temperature Monitoring (in Degrees C)");

		ac.setPrefSize(500, 250);
		ac.setMinSize(500, 250);
		ac.setMaxSize(500, 250);

		XYChart.Series seriesApril = new XYChart.Series();
		seriesApril.setName("April");
		seriesApril.getData().add(new XYChart.Data(1, 4));
		seriesApril.getData().add(new XYChart.Data(3, 10));
		seriesApril.getData().add(new XYChart.Data(6, 15));
		seriesApril.getData().add(new XYChart.Data(9, 8));
		seriesApril.getData().add(new XYChart.Data(12, 5));
		seriesApril.getData().add(new XYChart.Data(15, 18));
		seriesApril.getData().add(new XYChart.Data(18, 15));
		seriesApril.getData().add(new XYChart.Data(21, 13));
		seriesApril.getData().add(new XYChart.Data(24, 19));
		seriesApril.getData().add(new XYChart.Data(27, 21));
		seriesApril.getData().add(new XYChart.Data(30, 21));

		XYChart.Series seriesMay = new XYChart.Series();
		seriesMay.setName("May");
		seriesMay.getData().add(new XYChart.Data(1, 20));
		seriesMay.getData().add(new XYChart.Data(3, 15));
		seriesMay.getData().add(new XYChart.Data(6, 13));
		seriesMay.getData().add(new XYChart.Data(9, 12));
		seriesMay.getData().add(new XYChart.Data(12, 14));
		seriesMay.getData().add(new XYChart.Data(15, 18));
		seriesMay.getData().add(new XYChart.Data(18, 25));
		seriesMay.getData().add(new XYChart.Data(21, 25));
		seriesMay.getData().add(new XYChart.Data(24, 23));
		seriesMay.getData().add(new XYChart.Data(27, 26));
		seriesMay.getData().add(new XYChart.Data(31, 26));

		ac.getData().addAll(seriesApril, seriesMay);

		final AreaChart<Number, Number> ac2 = new AreaChart<Number, Number>(xAxis, yAxis);
		ac2.setTitle("Temperature Monitoring (in Degrees C)");

		ac2.setPrefSize(500, 250);
		ac2.setMinSize(500, 250);
		ac2.setMaxSize(500, 250);

		ac2.getData().addAll(seriesApril, seriesMay);

		List<Pane> chartContainers = new ArrayList<>();
		chartContainers.add(pane1);
		chartContainers.add(pane2);
		chartContainers.add(pane3);
		chartContainers.add(pane4);
		chartContainers.add(pane5);
		chartContainers.add(pane6);

		makeChartAppear(pane1, ac);
		makeChartAppear(pane2, ac2);
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

				acquireDataFromTweets();

				anchorPane.setEffect(null);
				dialogStackPane.getChildren().remove(progressIndicatorBox);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	void acquireDataFromTweets() {
		Map<User, Integer> tweetsPerUserMap = new HashMap<>();

		for (Tweet tweet : bigTweetList) {
			User user = tweet.getUser();

			if (tweetsPerUserMap.containsKey(user)) {
				tweetsPerUserMap.put(user, tweetsPerUserMap.get(user) + 1);
			} else {
				tweetsPerUserMap.put(user, 0);
			}
		}

		Map<User, Integer> sortedTweetsPerUserMap = tweetsPerUserMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

		List<User> topFiveActiveUsers = new ArrayList<User>();

		Set<Entry<User, Integer>> setLhm = sortedTweetsPerUserMap.entrySet();
		Iterator<Entry<User, Integer>> it2 = setLhm.iterator();

		int iter = 0;
		while (iter < 5) {
			Entry<User, Integer> e = it2.next();
			// System.out.println(e.getKey() + " : " + e.getValue());

			topFiveActiveUsers.add(e.getKey());
			iter++;
		}

		/** Timestamps **/
		Date currentDate = new Date(System.currentTimeMillis());
		int hoursDifference = hoursDifference(currentDate, oldestTweet);
		
		System.out.println(hoursDifference);
	}
	
	/** Animations **/
	public Date addHoursToDate(Date date, int hours) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(Calendar.HOUR_OF_DAY, hours);
	    
	    return calendar.getTime();
	}
	
	private int hoursDifference(Date start, Date end) {
	    final int MILLIS_TO_HOUR = 1000 * 60 * 60;
	    return (int) (start.getTime() - end.getTime()) / MILLIS_TO_HOUR;
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

	void makeChartAppear(Pane pane, AreaChart<Number, Number> ac) {
		RotateTransition rotate2 = new RotateTransition();
		rotate2.setAxis(Rotate.Y_AXIS);
		rotate2.setByAngle(90);
		rotate2.setDuration(Duration.millis(250));
		rotate2.setAutoReverse(true);
		rotate2.setNode(pane);

		RotateTransition rotate1 = new RotateTransition();
		rotate1.setAxis(Rotate.Y_AXIS);
		rotate1.setByAngle(90);
		rotate1.setDuration(Duration.millis(250));
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

	void dynamicallyCompleteCharts() {

	}

}
