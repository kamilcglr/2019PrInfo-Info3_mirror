package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXToggleNode;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import com.jfoenix.controls.JFXProgressBar;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleTopHashtagCell;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.NumberParser;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Map;

public class HashtagTabController {
    /**
     * Non FXML elements
     */
    private MainController mainController;

    private HastagViewer hastagViewer;

    private Hashtag hashtagToPrint;

    private FavsViewer favsViewer;
    //Used to know how many tweets we have during search
    private int numberOfTweetReceived;

    Map<String, Integer> hashtagUsed;

    Map<Tweet, Integer> Tweeted;


    private List<Tweet> tweetList;

    int favourite=0;
    @FXML
    private ScrollPane scrollPane;

    /*
     * THREADS
     * every thread should be declared here to kill them when exiting
     */
    private Thread threadGetTweetFromHashtag;

    private Thread threadGetTopLinkedHashtag;

    private Thread threadDetNumbers;

    private Thread threadTopTweets;

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
    private VBox vbox;
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

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        showHashtagElements(false);
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        NotfavoriteToggle.setVisible(false);
       
        //favourites();

        
        JFXScrollPane.smoothScrolling(scrollPane);
        
        topTenLinkedList.setCellFactory(param -> new SimpleTopHashtagCell());
    }

    private void showHashtagElements(boolean hide) {
        vbox.setVisible(hide);
        nbTweetsLabel.setVisible(hide);
        nbUsersLabel.setVisible(hide);
        tweetsLabel.setVisible(hide);
        usersLabel.setVisible(hide);
        lastAnalysedLabel.setVisible(hide);
    }

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
        

       
    }

    public void favourites() {
    	 //favourites
    	favsViewer=new FavsViewer();
		hashtagToPrint = hastagViewer.getHashtag();
        int fav=favsViewer.checkHashInFav(hashtagToPrint);
        System.out.println(hashtagToPrint.getHashtag());
        System.out.println("/n"+fav);
    	if(fav==1) {       
    		favoriteToggle.setVisible(false);
    		NotfavoriteToggle.setVisible(true);
    	}
    	else
    	{
    	    favoriteToggle.setVisible(true);
    	    NotfavoriteToggle.setVisible(false);

    	}
    }
    public void setHastagViewer(HastagViewer hastagViewer) throws Exception {
        this.hastagViewer = hastagViewer;
        hashtagToPrint = hastagViewer.getHashtag();

        hastagViewer.getSearchProgression();
        LoginController loginController = new LoginController();
        if (loginController.connected == 1) {
	            favourites();

     } else {
     		favoriteToggle.setVisible(false);
     		NotfavoriteToggle.setVisible(false);

     }
        Platform.runLater(() -> {
            hashtagLabel.setText("#" + hashtagToPrint.getHashtag());
        });

        threadGetTweetFromHashtag = new Thread(getTweetFromHashtag());
        threadGetTweetFromHashtag.setDaemon(true);
        threadGetTweetFromHashtag.start();
    }
    @FXML
    private void favouriteTogglePressed() {
    	favsViewer.addHashtagToFavourites(hashtagToPrint);
    	int fav=favsViewer.checkHashInFav(hashtagToPrint);
    	if(fav==1) {       
    		favoriteToggle.setVisible(false);
        NotfavoriteToggle.setVisible(true);
    	}
    	else
    	{
    	       favoriteToggle.setVisible(true);
    	        NotfavoriteToggle.setVisible(false);
    	        favourite=0;

    	}


    }

    /**
     * Called by setHashtag, it gets the tweets of a hashtag,
     * then create concurrent thread to analyze data
     * This task is very long.
     * Do not change the order. We have to wait to get all tweets before doing analysis
     */
    private Task<Void> getTweetFromHashtag() {
        Platform.runLater(() -> {
            initProgress(false);
        });
        try {
            //search and get tweets from hashtag first
            this.tweetList = hastagViewer.searchByCount(hashtagToPrint.getHashtag(), progressBar, 4500, null);

            //Tweet are collected
            Platform.runLater(() -> {
                initProgress(true);
            });

            threadGetTopLinkedHashtag = new Thread(setTopLinkedHashtag());
            threadGetTopLinkedHashtag.setDaemon(true);
            threadGetTopLinkedHashtag.start();

            threadDetNumbers = new Thread(setNumberOfUniqueAccountAndNumberOfTweets());
            threadDetNumbers.setDaemon(true);
            threadDetNumbers.start();

            threadTopTweets = new Thread(setTopTweets());
            threadTopTweets.setDaemon(true);
            threadTopTweets.start();

            //Wait for the two other tasks
            while (threadGetTopLinkedHashtag.isAlive() || threadDetNumbers.isAlive() || threadTopTweets.isAlive()) {
                Thread.sleep(1000);
            }
            Platform.runLater(() -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if(tweetList.size()>0) {
                String date = simpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
                lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                        date);

                showHashtagElements(true);
                progressBar.setVisible(false);
                progressLabel.setVisible(false);
                }
                else {
                	 lastAnalysedLabel.setText("Aucun tweet n'est relié à ce hashtag");
                     showHashtagElements(true);
                     progressBar.setVisible(false);
                     progressLabel.setVisible(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task<Void> setTopLinkedHashtag() {
        List<String> hashtags = hastagViewer.getHashtagsLinked();
        hashtagUsed = hastagViewer.topHashtag(hashtags);

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtagUsed.get(hashtag).toString()));
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
            nbUsersLabel.setText(NumberParser.spaceBetweenNumbers(hastagViewer.getNumberOfUniqueAccounts()));
            nbTweetsLabel.setText(NumberParser.spaceBetweenNumbers(hastagViewer.getNumberOfTweets()));
        });
        return null;
    }

    private Task<Void> setTopTweets() {
        Tweeted = hastagViewer.topTweets(tweetList);
        ObservableList<Tweet> tweetsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Tweet tweet : Tweeted.keySet()) {
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
            if (hastagViewer != null) {
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
                    tweetController.populate(tweet,true);
                    listTweets.getItems().add(jfxListCell);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void initProgress(boolean isIndeterminate) {
        if (!isIndeterminate) {
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            progressLabel.setVisible(true);
            progressLabel.setText("Récupération des tweets depuis Twitter.com");
        } else {
            progressBar.setProgress(-1);
            progressLabel.setText("Analyse des tweets");
        }
    }

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        if (threadGetTweetFromHashtag != null) {
            threadGetTweetFromHashtag.interrupt();
        }
        if (threadDetNumbers != null) {
            threadDetNumbers.interrupt();

        }
        if (threadGetTopLinkedHashtag != null) {
            threadGetTopLinkedHashtag.interrupt();
        }
        if (threadTopTweets != null) {
            threadTopTweets.interrupt();
        }
    }
}
