package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dao.Tweet;
import fr.tse.ProjetInfo3.mvc.dao.User;
import fr.tse.ProjetInfo3.mvc.services.RequestManager;

import java.util.List;

/**
 * This class makes the research for an user
 * Print the result on the User Page
 * */
public class UserViewer {
    private User user;
    private RequestManager requestManager;

    public UserViewer() {
        requestManager = new RequestManager();
    }

    /**
     * Calls request manager getUSer to find the user given as parameter
     * If the user does not exist or an exception occurs with the Requestmanager
     * we throws the exception to the controller. Then the controller alert the user
     * that something wrong occured (e.g. the user does not exist)
     * @param screen_name
     */
    public void searchScreenName(String screen_name) throws Exception {
        user = requestManager.getUser(screen_name);
        System.out.println(user);
    }

    public void getTweets(String screen_name, int count){
        List<Tweet> tweets = requestManager.getTweetsFromUSer(screen_name,count);
        System.out.println(tweets);
    }

    /**
     * After searching the user, this function will print on the screen the attributes of an user
     * Name and screen name(the @realdonaldtrump)
     * Number of tweets
     * Number of Follower/Following
     * ...*/
    public void printUserView(){
        System.out.println(this.user.getName());
    }
}