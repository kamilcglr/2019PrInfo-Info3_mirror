package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class PIViewerTest {
    PIViewer piViewer = new PIViewer();

    @Test
    void getTweets() throws Exception {
        piViewer.getlistOfInterestPoint(13);
        piViewer.setSelectedInterestPoint(0);

        Label label = new Label();
        List<Tweet> tweets = piViewer.getTweets(label);

        assertTrue(tweets.size()!=0);
    }

}