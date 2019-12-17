package fr.tse.ProjetInfo3.mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * @author ALAMI IDRISSI Taha Controller of the Edit PI window, all user
 *         interactions whith the Edit PI windows (not the tabs) are handled
 *         here
 */
public class PiTabController {
	private List<Tweet> bigTweetList;

	private MainController mainController;

	private PIViewer piViewer;

	private InterestPoint interestPointToPrint;

	@FXML
	private JFXButton editButton;

	@FXML
	private JFXListView<User> topFiveUserList;

	@FXML
	private AnchorPane PIPane;

	@FXML
	private StackPane dialogStackPane;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Label piNameLabel;

	@FXML
	private Accordion accordion;

	@FXML
	private Label nbTweetsLabel;

	@FXML
	private Label nbTweets;

	/*
	 * THREADS every thread should be declared here to kill them when exiting
	 */
	private Thread threadGetTweets;

	private Thread threadTopFiveUsers;

	private Thread threadTopLinkedHashtags;

	private Thread threadTopTweets;

	@FXML
	private JFXListView<ListObjects.ResultHashtag> topTenLinkedList;
	@FXML
	private JFXListView listTweets;
	@FXML
	private TitledPane titledTweet;

	private UserViewer userViewer;
	Map<String, Integer> hashtags;
	List<String> myHashtags = new ArrayList<>();
	List<Tweet> tweetlist = new ArrayList<>();
	Map<Tweet, Integer> Tweeted = new HashMap<Tweet, Integer>();
	List<String> ListOfUsers = new ArrayList<>();

	// Progress indicator
	@FXML
	private JFXProgressBar progressBar;
	@FXML
	private Label progressLabel;

	/* Controller can acces to this Tab */
	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@FXML
	private void initialize() {
		// hide unused elements
		nbTweets.setVisible(false);
		nbTweetsLabel.setVisible(false);
		editButton.setVisible(false);

		topFiveUserList.setFocusTraversable(false);
		topTenLinkedList.setFocusTraversable(false);

		// topTenLinkedList.setCellFactory(param ->
		// (ListCell<ListObjects.ResultHashtag>) new Cell());

		topFiveUserList.setCellFactory(param -> new ListObjects.TopUserCell(interestPointToPrint));
		topTenLinkedList.setCellFactory(param -> new ListObjects.HashtagCellPI(interestPointToPrint));
	}

	public void setDatas(PIViewer piViewer) {
		this.piViewer = piViewer;
		this.interestPointToPrint = piViewer.getSelectedInterestPoint();
		Platform.runLater(() -> {
			piNameLabel.setText(interestPointToPrint.getName());
		});

		threadGetTweets = new Thread(getTweets());
		threadGetTweets.setDaemon(true);
		threadGetTweets.start();
	}

	private Task<Void> getTweets() {
		Platform.runLater(() -> {
			initProgress(false);
		});
		try {
			bigTweetList = piViewer.getTweets(progressBar, progressLabel);

			// Tweet are collected
			Platform.runLater(() -> {
				initProgress(true);
			});

			threadTopFiveUsers = new Thread(setTopFiveUsers());
			threadTopFiveUsers.setDaemon(true);
			threadTopFiveUsers.start();

			threadTopLinkedHashtags = new Thread(setTopLinkedHashtags());
			threadTopLinkedHashtags.setDaemon(true);
			threadTopLinkedHashtags.start();

			threadTopTweets = new Thread(setTopTweets());
			threadTopTweets.setDaemon(true);
			threadTopTweets.start();

			// Wait for the two other tasks
			while (threadTopFiveUsers.isAlive()) {
				Thread.sleep(1000);
			}
			Platform.runLater(() -> {
				// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				// String date = simpleDateFormat.format(tweetList.get(tweetList.size() -
				// 1).getCreated_at());
				// lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis
				// le " +
				// date);

				// showHashtagElements(true);
				progressBar.setVisible(false);
				progressLabel.setVisible(false);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Task<Void> setTopFiveUsers() throws Exception {
		// user in parameters to find what to exclude
		List<User> users = piViewer.getTopFiveUsers(bigTweetList, interestPointToPrint.getUsers());

		ObservableList<User> usersToPrint = FXCollections.observableArrayList();
		int i = 0;
		for (User user : users) {
			usersToPrint.add(user);
			i++;
			if (i == 5) {
				break;
			}
		}
		Platform.runLater(() -> {
			topFiveUserList.getItems().addAll(usersToPrint);
			// topFiveUserList.setMaxHeight(50 * usersToPrint.size());
		});

		return null;
	}

	private Task<Void> setTopLinkedHashtags() {
		hashtags = piViewer.topHashtag(bigTweetList, interestPointToPrint.getHashtags());

		int i = 0;
		ObservableList<ListObjects.ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
		for (String hashtag : hashtags.keySet()) {
			i++;
			hashtagsToPrint
					.add(new ListObjects.ResultHashtag(String.valueOf(i), hashtag, hashtags.get(hashtag).toString()));
			if (i == 10) {
				break;
			}
		}
		Platform.runLater(() -> {
			topTenLinkedList.getItems().addAll(hashtagsToPrint);
		});
		// titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
		return null;
	}

	@FXML
	private void addTweetsToList(List<Tweet> toptweets) {
		ObservableList<JFXListCell> listTweetCell = FXCollections.observableArrayList();
		try {
			if (piViewer != null) {
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

					tweetController.injectPiTabController(this);
					tweetController.populate(tweet, true);
					listTweets.getItems().add(jfxListCell);
				}
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Task<Void> setTopTweets() {
		ObservableList<Tweet> tweetsToPrint = FXCollections.observableArrayList();
		Tweeted = piViewer.topTweets(bigTweetList, progressBar);
		int i = 0;
		for (Tweet tweet : Tweeted.keySet()) {
			tweetsToPrint.add(tweet);
			i++;
			System.out.println(tweet);
			if (i == 10) {
				break;
			}
		}
		Platform.runLater(() -> {
			addTweetsToList(tweetsToPrint);
		});
		titledTweet.setMaxHeight(70 * tweetsToPrint.size());
		return null;
	}

	private void initProgress(boolean isIndeterminate) {
		if (!isIndeterminate) {
			progressBar.setVisible(true);
			progressBar.setProgress(0);
			progressLabel.setVisible(true);
		} else {
			progressBar.setProgress(-1);
			progressLabel.setText("Analyse des tweets");
		}
	}

	/**
	 * Called when tab is closed
	 */
	public void killThreads() {
		if (threadGetTweets != null) {
			threadGetTweets.interrupt();
		}
		if (threadTopFiveUsers != null) {
			threadTopFiveUsers.interrupt();
		}
		if (threadTopTweets != null) {
			threadTopTweets.interrupt();
		}
	}
}
