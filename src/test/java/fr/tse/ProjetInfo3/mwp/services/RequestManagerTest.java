package fr.tse.ProjetInfo3.mwp.services;

import com.google.gson.internal.bind.util.ISO8601Utils;
import fr.tse.ProjetInfo3.mwp.dao.Tweet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestManagerTest {

    @org.junit.jupiter.api.Test
    void getTweetsFromUSer() {
        RequestManager requestManager = new RequestManager();
        List<Tweet> tweets = requestManager.getTweetsFromUSer("realdonaldtrump", 100);
        tweets.forEach(tweet -> System.out.println(tweet));
        tweets.forEach(tweet -> System.out.println(tweet.getUser()));
    }
}