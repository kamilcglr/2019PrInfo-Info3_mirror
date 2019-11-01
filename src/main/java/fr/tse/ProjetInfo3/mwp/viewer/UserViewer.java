package fr.tse.ProjetInfo3.mwp.viewer;

import fr.tse.ProjetInfo3.mwp.dao.Tweet;
import fr.tse.ProjetInfo3.mwp.dao.User;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;

import java.util.List;

/**
 * This class makes the research for an user
 * Print the result on the User Page
 * */
public class UserViewer {
    private RequestManager requestManager;

    public UserViewer() {
        requestManager = new RequestManager();
    }

    public void searchScreenName(String screen_name) throws Exception {
        User user = requestManager.getUser(screen_name);
        System.out.println(user);
    }

    public void getTweets(String screen_name, int count){
        List<Tweet> tweets = requestManager.getTweetsFromUSer(screen_name,count);
        System.out.println(tweets);
    }
    public void printUserView(){

    }
}
