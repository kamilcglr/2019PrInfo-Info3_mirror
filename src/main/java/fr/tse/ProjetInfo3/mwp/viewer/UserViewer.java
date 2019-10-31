package fr.tse.ProjetInfo3.mwp.viewer;

import fr.tse.ProjetInfo3.mwp.Profile;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;
import fr.tse.ProjetInfo3.mwp.services.TwitterAPP;
import twitter4j.TwitterException;

import java.io.IOException;

/**
 * This class makes the research for an user
 * Print the result on the User Page
 * */
public class UserViewer {
    private RequestManager requestManager;

    public UserViewer() {
        requestManager = new RequestManager(TwitterAPP.getTwitter());
    }

    public void searchId(String id) throws Exception {
        Profile profile = requestManager.getProfile(id);
    }

    public void printUserView(){

    }
}
