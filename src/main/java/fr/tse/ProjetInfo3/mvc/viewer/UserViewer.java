package fr.tse.ProjetInfo3.mvc.viewer;

import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * This class makes the research for an user
 * Print the result on the User Page
 */
public class UserViewer {
    private User user;
    private RequestManager requestManager;
    DatabaseManager databaseManager;
    private List<Tweet> tweets;

    public UserViewer() {
        databaseManager = new DatabaseManager();
        requestManager = new RequestManager();
        tweets = new ArrayList<>();
    }

    /**
     * Calls request manager getUSer to find the user given as parameter
     * If the user does not exist or an exception occurs with the Requestmanager
     * we throws the exception to the controller. Then the controller alert the user
     * that something wrong occurred (e.g. the user does not exist)
     *
     * @param screen_name
     */
    public void searchScreenName(String screen_name) throws Exception {
        user = requestManager.getUser(screen_name);
        user.setTweets(new ArrayList<>());
    }

    public User searchScreenNameU(String screen_name) throws Exception {
        return user = requestManager.getUser(screen_name);
    }

    public Pair<List<Tweet>, Integer> getTweetsByDate(User user, int nbRequestMax, Date untilDate, Long maxId, int alreadyGot) {
        int nbRequestDone;
        List<Tweet> tweetList = new ArrayList<>();
        if (user.getTweets().size() < user.getStatuses_count()) {
            Pair<List<Tweet>, Integer> pair = requestManager.getTweetsFromUserNBRequest(user.getScreen_name(), nbRequestMax, untilDate, maxId, alreadyGot);
            tweetList = pair.getKey();
            nbRequestDone = pair.getValue();
        } else {
            //TODO Handle JUL or not enough tweets
            //All tweets for the user are collected
            user.setAllTweetsCollected(true);
            nbRequestDone = 0;
        }
        return new Pair<>(tweetList, nbRequestDone);
    }

    public List<Tweet> getTweetsByCount(String screen_name, int count, JFXProgressBar progressBar) {
        return requestManager.getTweetsFromUser(screen_name, count, progressBar);
    }

    public List<Tweet> getListOfTweets() {
        return tweets;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.tweets = user.getTweets();
    }

    public Map<Tweet, Integer> topTweets(List<Tweet> tweetList) {
        Map<Tweet, Integer> TweetsSorted;
        Map<Tweet, Integer> Tweeted = new HashMap<Tweet, Integer>();

        for (Tweet tweet : tweetList) {
            if (!Tweeted.containsKey(tweet) && tweet.getRetweeted_status() == null) {
                int PopularCount = (int) tweet.getRetweet_count() + (int) tweet.getFavorite_count();
                Tweeted.put(tweet, PopularCount);
            }
        }

        TweetsSorted = Tweeted
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        return TweetsSorted;
    }


    /**
     * From a big list of tweets, return the top Hashtags, including this hashtag
     *
     * @param tweetList
     * @return
     */
    public Map<String, Integer> getTopTenHashtags(List<Tweet> tweetList) {
        Map<String, Integer> hashtagUsedSorted;
        Map<String, Integer> hashtagUsed = new HashMap<String, Integer>();

        List<String> hashtags = new ArrayList<>();
        //Method to get hashtag from retweets
        for (Tweet tweet : tweetList) {

            //if the tweet is retweeted, then we get the #'s of retweeted tweet
            if (tweet.getRetweeted_status() != null) {
                hashtags.addAll(tweet
                        .getRetweeted_status().getEntities().getHashtags()
                        .stream().map(Tweet.hashtags::getText).collect(Collectors.toList()));
            } else
                //else, if the tweet is quoted, then we get the #'s of quoted tweet
                if (tweet.getQuoted_status() != null) {
                    hashtags.addAll(tweet
                            .getQuoted_status().getEntities().getHashtags()
                            .stream().map(Tweet.hashtags::getText).collect(Collectors.toList()));
                }
            hashtags.addAll(tweet.getEntities().getHashtags()
                    .stream().map(Tweet.hashtags::getText).collect(Collectors.toList()));

        }

        for (String theme : hashtags) {
            Integer occurrence = hashtagUsed.get(theme);
            hashtagUsed.put(theme, (occurrence == null) ? 1 : occurrence + 1);
        }

        hashtagUsedSorted = sortByValue(hashtagUsed);

        return hashtagUsedSorted;
    }

    public Map<String, Integer> sortByValue(final Map<String, Integer> hashtagCounts) {

        return hashtagCounts.entrySet()

                .stream()

                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))

                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    /*=======================================DATABASE FUNCTIONS ========================================*/

    /**
     * Set this.user to the user got from DB.
     *
     * @return boolean
     */
    public void setUserFromDataBase() {
        // look first in the Database if there is an user.
        user = databaseManager.getCachedUserFromDatabase(user.getScreen_name());
    }

    /**
     * Search in the DB if user exist. If exist, it sends true.
     *
     * @return boolean
     */
    public boolean verifyUserInDataBase() {
        return databaseManager.cachedUserInDataBase(user.getScreen_name());
    }

    public void cacheUserToDataBase(User user) {
        databaseManager.cacheUserToDataBase(user);
    }

    public void deleteCachedUser() {
        databaseManager.deleteCachedUserFromDataBase(user.getScreen_name());
    }
}
