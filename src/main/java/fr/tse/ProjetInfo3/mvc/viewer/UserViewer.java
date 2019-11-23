package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.controller.UserTabController;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class makes the research for an user
 * Print the result on the User Page
 */
public class UserViewer {
    private User user;
    private RequestManager requestManager;

    private UserTabController userTabController;

    public UserViewer() {
        requestManager = new RequestManager();
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
        System.out.println(user);
    }

    public List<Tweet> getTweetsByDate(String screen_name, Date date) {
        return requestManager.getTweetsFromUserByDate(screen_name, date);
    }

    public List<Tweet> getTweetsByCount(String screen_name, int count) {
        return requestManager.getTweetsFromUser(screen_name, count);
    }

    public User getUser() {
        return user;
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
        //This will not work to get hashtag from retweets
        //List<String> hashtags = tweetList.stream()
        //        .map(Tweet::getEntities)
        //        .flatMap(subHashtags -> subHashtags.getHashtags().stream())
        //        .map(Tweet.hashtags::getText)
        //        .collect(Collectors.toList());

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

                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }
}
