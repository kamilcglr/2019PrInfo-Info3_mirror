//package fr.tse.ProjetInfo3.mvc.repository;
//
//import com.jfoenix.controls.JFXProgressBar;
//import fr.tse.ProjetInfo3.mvc.dto.Tweet;
//import fr.tse.ProjetInfo3.mvc.dto.User;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.List;
//import java.util.Map;
//
///**
// * Test if request manager sends correct information
// */
//class RequestManagerTest {
//
//    @org.junit.jupiter.api.Test
//    void getTweetsFromUserTest() {
//        Instant start = Instant.now();
//
//        RequestManager requestManager = new RequestManager();
//        List<Tweet> tweets = requestManager.getTweetsFromUser("sobunung", 3194, new JFXProgressBar());
//        //tweets.forEach(System.out::println);
//
//        Instant finish = Instant.now();
//        long timeElapsed = Duration.between(start, finish).toSeconds();
//        System.out.println(timeElapsed);
//        System.out.println(tweets.get(tweets.size() - 1));
//        System.out.println(tweets.size());
//    }
//
//    @org.junit.jupiter.api.Test
//    void searchTweetsTest() {
//        //RequestManager requestManager = new RequestManager();
//        //List<Tweet> tweets = requestManager.searchTweets("mardi");
//        //tweets.forEach(tweet -> System.out.println(tweet));
//        //tweets.forEach(tweet -> System.out.println(tweet.getUser()));
//    }
//
//    @org.junit.jupiter.api.Test
//    void searchUsersTest() throws IOException, InterruptedException {
//        RequestManager requestManager = new RequestManager();
//        List<User> users = requestManager.getUsersbyName("donald trump");
//        for (User user: users) {
//            System.out.println(user.getName() + "\t\t\t" + user.getScreen_name());
//        }
//    }
//
//}