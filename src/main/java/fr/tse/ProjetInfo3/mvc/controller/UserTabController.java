package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleTopHashtagCell;

import fr.tse.ProjetInfo3.mvc.utils.NumberParser;
import fr.tse.ProjetInfo3.mvc.utils.TwitterDateParser;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.frenchSimpleDateFormat;

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
    private JFXButton compareButton;

    @FXML
    private JFXToggleNode favoriteToggle;

    @FXML
    private JFXSpinner progressIndicator;

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
    private Circle avatar;
    @FXML
    private ImageView profileImageView;

    private Image profileImage;

    /**************************************************************/
    /*Controller can access to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;

    }

    public void favourites() {
   	 //favourites
   	favsViewer=new FavsViewer();
		userToPrint = userViewer.getUser();
       int fav=favsViewer.checkUserInFav(userToPrint);
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
    /*
     * Set the user of the page
     * Prints User simple infos (name, id...)
     * Prints top #
     * */
    public void setUserViewer(UserViewer userViewer) throws InterruptedException {
        this.userViewer = userViewer;
        userToPrint = userViewer.getUser();
   
        Platform.runLater(() -> {
            username.setText("@" + userToPrint.getScreen_name());
            twittername.setText(userToPrint.getName());
            description.setText(userToPrint.getDescription());
            nbTweet.setText(String.valueOf(NumberParser.spaceBetweenNumbers(userToPrint.getStatuses_count())));
            nbFollowers.setText(String.valueOf(NumberParser.spaceBetweenNumbers(userToPrint.getFollowers_count())));
            nbFollowing.setText(String.valueOf(NumberParser.spaceBetweenNumbers(userToPrint.getFriends_count())));
            buildPicture();
        });
        LoginController loginController = new LoginController();
        if (loginController.connected == 1) {
	            favourites();

     } else {
     		favoriteToggle.setVisible(false);
     		NotfavoriteToggle.setVisible(false);

     }
        threadGetTweets = new Thread(getTweets());
        threadGetTweets.setDaemon(true);
        threadGetTweets.start();
    }

    @FXML
    private void initialize() {
        //hide elements
        compareButton.setVisible(false);
        favoriteToggle.setVisible(false);
        JFXScrollPane.smoothScrolling(scrollPane);
   		NotfavoriteToggle.setVisible(false);


        listHashtags.setCellFactory(param -> new SimpleTopHashtagCell());

        Platform.runLater(() -> hideLists(true));

    }

    //TODO
    private String numberFormatter(long value) {
        return Arrays.toString(String.valueOf(value).split("([0-9]{3})*$"));
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

    private char getTypeSearch() {
        return 'c';
        //TODO getType by user
    }
    @FXML
    private void favouriteTogglePressed() {
                
                        favsViewer=new FavsViewer();
                        favsViewer.addUserToFavourites(userToPrint);
                        int fav=favsViewer.checkUserInFav(userToPrint);
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


    

    private Date getDate() {
        Date twitterDate = null;
        try {
            twitterDate = TwitterDateParser.parseTwitterUTC("Fri Nov 11 20:00:00 CET 2019");
            //TODO getField by user
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return twitterDate;
    }

    /**
     * Called by setUser, it gets the tweets of a user,
     * then create Two concurrent thread to check the top Hashtag and top Tweets
     */
    private Task<Void> getTweets() throws InterruptedException {
        Platform.runLater(() -> {
            initProgress(false);
        });

        long numberOfRequest = userToPrint.getStatuses_count();
        if (numberOfRequest > 3194) {
            numberOfRequest = 3194;
        }
        tweetList = userViewer.getTweetsByCount(userToPrint.getScreen_name(), (int) numberOfRequest, progressBar);

        //Tweet are collected
        Platform.runLater(() -> {
            initProgress(true);
        });

        threadSetTopHashtags = new Thread(setTopHashtags());
        threadSetTopHashtags.setDaemon(true);
        threadSetTopHashtags.start();

        //Set top tweets, but it can be dangerous with the shared tweetlist, we have to test
        threadSetTopTweets = new Thread(setTopTweets());
        threadSetTopTweets.setDaemon(true);
        threadSetTopTweets.start();

        //Wait for the two other tasks
        while (threadSetTopHashtags.isAlive() || threadSetTopTweets.isAlive()) {
            Thread.sleep(1000);
        }
        Platform.runLater(() -> {
        	if(tweetList.size()>0) {
            String date = frenchSimpleDateFormat.format(tweetList.get(tweetList.size() - 1).getCreated_at());
            lastAnalysedLabel.setText(tweetList.size() + " tweets ont été analysés depuis le " +
                    date);

            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            hideLists(false);
        	}
        	else {
        		 lastAnalysedLabel.setText("Cet utilisateur n'a aucun tweet"
                         );
                 progressBar.setVisible(false);
                 progressLabel.setVisible(false);
                 hideLists(false);
        		
        	}
        });

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

    private void hideLists(boolean show) {
        vBox.setVisible(!show);
        lastAnalysedLabel.setVisible(!show);
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
