package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test if request manager sends correct information
 */
class UserViewerTest {
    private User user;
    private RequestManager requestManager;
    private UserViewer userViewer;

    public UserViewerTest() {
        requestManager = new RequestManager();
    }

    /**
     * TODO We have to add more unit test
     */
    @Test
    void searchScreenName() throws IOException, InterruptedException {
        user = requestManager.getUser("realDonaldTrump");
        assertEquals(user.getScreen_name(), "realDonaldTrump");

        user = requestManager.getUser("unittest");
        assertEquals(user.getScreen_name(), "unittest");
        assertEquals(user.getFollowers_count(), 35);
        assertEquals(user.getCreated_at(), "Fri Apr 13 01:45:08 +0000 2007");
        assertEquals(user.getFriends_count(), 84);
    }

    @org.junit.jupiter.api.Test
    void topHashtagTest() {
        UserViewer userViewer = new UserViewer();
        //List<Tweet> tweets = requestManager.getTweetsFromUser("kamilcglr", 200);
        //Map<String, Integer> hashtagUsed = userViewer.topHashtag(tweets);


        List<Tweet> tweetsByDate = new ArrayList<>();
        try {
            tweetsByDate = requestManager.getTweetsFromUserByDate("MayleenTheOne",
                    TwitterDateParser.parseTwitterUTC("Fri Nov 11 20:00:00 CET 2019"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, Integer> hashtagUsedByDate = userViewer.topHashtag(tweetsByDate);

        System.out.println(hashtagUsedByDate);
    }


}