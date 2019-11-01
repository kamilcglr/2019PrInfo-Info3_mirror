package fr.tse.ProjetInfo3.mwp.viewer;

import fr.tse.ProjetInfo3.mwp.dao.User;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;

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

    public void printUserView(){

    }
}
