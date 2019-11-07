package fr.tse.ProjetInfo3.mvc.services;

import fr.tse.ProjetInfo3.mvc.dao.Tweet;

import java.util.List;

/**
 * Test if request manager sends correct information
 * */
class RequestManagerTest {

    @org.junit.jupiter.api.Test
    void getTweetsFromUSer() {
        RequestManager requestManager = new RequestManager();
        List<Tweet> tweets = requestManager.getTweetsFromUSer("realdonaldtrump", 100);
        tweets.forEach(tweet -> System.out.println(tweet));
        //tweets.forEach(tweet -> System.out.println(tweet.getUser()));
    }
    @org.junit.jupiter.api.Test
    void searchTweetsTest() {
        RequestManager requestManager = new RequestManager();
        List<Tweet> tweets = requestManager.searchTweets("mardi");
        //tweets.forEach(tweet -> System.out.println(tweet));
        //tweets.forEach(tweet -> System.out.println(tweet.getUser()));
    }
    
   
}