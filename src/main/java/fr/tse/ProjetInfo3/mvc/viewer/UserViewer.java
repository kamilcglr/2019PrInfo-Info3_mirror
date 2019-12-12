package fr.tse.ProjetInfo3.mvc.viewer;

import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.controller.UserTabController;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
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
    private List<Tweet> tweets;

    private UserTabController userTabController;

    public UserViewer() {
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
        user.setListoftweets(new ArrayList<>());
    }

    public User searchScreenNameU(String screen_name) throws Exception {
        return user = requestManager.getUser(screen_name);
    }

    public Integer getTweetsByDate(User user, int nbRequestMax, Date untilDate, Long maxId, int alreadyGot, JFXProgressBar progressBar) {
        int nbRequestDone;
        if (tweets.size() < user.getStatuses_count()) {
            Pair<List<Tweet>, Integer> pair = requestManager.getTweetsFromUserNBRequest(user.getScreen_name(), nbRequestMax, untilDate, maxId, alreadyGot, progressBar);
            tweets.addAll(pair.getKey());
            nbRequestDone = pair.getValue();
        } else {
            //TODO Handle JUL or not enough tweets
            //All tweets for the user are collected
            user.setAllTweetsCollected(true);
            nbRequestDone = 0;
        }
        return nbRequestDone;
    }

    public List<Tweet> getTweetsByCount(String screen_name, int count, JFXProgressBar progressBar) {
        tweets.addAll(requestManager.getTweetsFromUser(screen_name, count, progressBar));
        return tweets;
    }

    public List<Tweet> getListOfTweets() {
        return tweets;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.tweets = user.getListoftweets();
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


    public Map<String, Integer> topHashtag(List<Tweet> tweetList) {
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
            Integer occurence = hashtagUsed.get(theme);
            hashtagUsed.put(theme, (occurence == null) ? 1 : occurence + 1);
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
}
