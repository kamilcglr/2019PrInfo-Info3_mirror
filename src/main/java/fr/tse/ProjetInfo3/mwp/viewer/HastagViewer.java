package fr.tse.ProjetInfo3.mwp.viewer;

import fr.tse.ProjetInfo3.mwp.Profile;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;
import fr.tse.ProjetInfo3.mwp.services.TwitterAPP;
import twitter4j.Status;

import java.util.List;

public class HastagViewer {
    private RequestManager requestManager;

    public HastagViewer() {
        requestManager = new RequestManager(TwitterAPP.getTwitter());
    }

    public void searchHashtag(String hashtag) throws Exception {
        List<Status> profile = requestManager.getTweets(hashtag);
    }

    public void printUserView(){

    }
}
