package fr.tse.ProjetInfo3.mvc.viewer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXListView;

import fr.tse.ProjetInfo3.mvc.controller.SearchTabController;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.collections.ObservableList;


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
    public Map<String, String> getListPropositions(String userProposition) {
        Map<String,String> usersNamesAndScreenNames = null;
        try {
            usersNamesAndScreenNames = requestManager.getUsersbyName(userProposition);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return usersNamesAndScreenNames;
    }

    public User getUser() {
        return user;
    }
}
