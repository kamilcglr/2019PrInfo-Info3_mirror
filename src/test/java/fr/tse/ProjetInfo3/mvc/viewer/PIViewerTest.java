package fr.tse.ProjetInfo3.mvc.viewer;

import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class PIViewerTest {
    PIViewer piViewer = new PIViewer();

    @Test
    void getlistOfInterestPoint() {
        List<InterestPoint> listOfInterestPoints = new ArrayList<>();
        listOfInterestPoints = piViewer.getListOfInterestPointFromDataBase();

        assertTrue(listOfInterestPoints.size()>0);
    }

    /**
     * Verify if limitReached sends correct boolean depending on the states of hashtags and users
     * */
    @Test
    private void limitsReachedTest(){

    }

    @Test
    void getTweets() throws Exception {
        List<Hashtag> hashtags = new ArrayList<>();
        Hashtag h1 = new Hashtag("#otan");
        Hashtag h2 = new Hashtag("#nato");
        hashtags.add(h1);
        hashtags.add(h2);

        List<User> users = new ArrayList<>();
        RequestManager requestManager = new RequestManager();
        User u1 = requestManager.getUser("realdonaldtrump");
        User u2 = requestManager.getUser("emmanuelmacron");
        users.add(u1);
        users.add(u2);

        Date date = new Date();
        InterestPoint ip1 = new InterestPoint("test", "", date);
        ip1.setHashtags(hashtags);
        ip1.setUsers(users);


        piViewer.getlistOfInterestPoint();
        piViewer.setSelectedInterestPoint(0);

        Label label = new Label();
        List<Tweet> tweets = piViewer.getTweets(label);

        assertTrue(tweets.size()!=0);
    }

    @Test
    void getTweetsFromHashtagTest() throws Exception {
        //Hashtag h1 = new Hashtag("#nato");
        //List<Hashtag> hashtags = new ArrayList<>();
        //hashtags.add(h1);
        //Date date = new Date();
        //InterestPoint ip1 = new InterestPoint("test", "", date);
        //ip1.setHashtags(hashtags);
//
        //LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        //localDateTime = localDateTime.minusDays(9);
        //Date maxDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        ////Verify date Limit
//
        //Integer list = piViewer.getTweetsFromHashtag(hashtags.get(0), 1000, maxDate, new Label());
//
        //assertEquals(hashtags.get(0).isGlobalTweetsLimit(), true);
    }
}