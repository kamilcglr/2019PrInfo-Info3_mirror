package fr.tse.ProjetInfo3.mvc.repository;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Test if request manager sends correct information
 */
class RequestManagerTest {

    @org.junit.jupiter.api.Test
    private List<Tweet> getTweetsFromUserTest() {
        RequestManager requestManager = new RequestManager();
        List<Tweet> tweets = requestManager.getTweetsFromUser("realdonaldtrump", 1000);
        tweets.forEach(System.out::println);
        return tweets;
    }

    @org.junit.jupiter.api.Test
    void searchTweetsTest() {
        //RequestManager requestManager = new RequestManager();
        //List<Tweet> tweets = requestManager.searchTweets("mardi");
        //tweets.forEach(tweet -> System.out.println(tweet));
        //tweets.forEach(tweet -> System.out.println(tweet.getUser()));
    }


}