package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.controller.UserTabController;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import java.util.List;

/**
 * This class makes the research for an user
 * Print the result on the User Page
 */
public class UserViewer {
    private User user;
    private RequestManager requestManager;

    private UserTabController userTabController;

    public UserViewer() {
        requestManager = new RequestManager();
    }

    /**
     * Calls request manager getUSer to find the user given as parameter
     * If the user does not exist or an exception occurs with the Requestmanager
     * we throws the exception to the controller. Then the controller alert the user
     * that something wrong occured (e.g. the user does not exist)
     *
     * @param screen_name
     */
    public void searchScreenName(String screen_name) throws Exception {
        user = requestManager.getUser(screen_name);
        System.out.println(user);
    }

    public void getTweets(String screen_name, int count) {
        List<Tweet> tweets = requestManager.getTweetsFromUSer(screen_name, count);
        System.out.println(tweets);
    }

    public User getUser() {
        return user;
    }
    

}
