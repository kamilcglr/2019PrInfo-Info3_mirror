package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.controller.SearchTabController;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import java.io.IOException;
import java.util.List;

/**
 * @author Laïla
 */
public class SearchViewer {
    private User user;
    private RequestManager requestManager;

    private SearchTabController searchTabController;

    public SearchViewer() {
        requestManager = new RequestManager();
    }

    /**
     * This method gets the list of propositions based on a String, and use the method of the RequestManager class
     *
     * @param userProposition name
     * @return usersNamesAndScreenNames : Map of names and screen_names of user
     */
    public List<User> getListPropositions(String userProposition) {
        List<User> users = null;
        try {
            users = requestManager.getUsersbyName(userProposition);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUser() {
        return user;
    }
}
