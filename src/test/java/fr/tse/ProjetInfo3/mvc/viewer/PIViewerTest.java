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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PIViewerTest {

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

        PIViewer piViewer = new PIViewer();

        piViewer.getlistOfInterestPoint();
        piViewer.setSelectedInterestPoint(0);
        List<Tweet> tweets = piViewer.getTweets(null, null);


    }
}