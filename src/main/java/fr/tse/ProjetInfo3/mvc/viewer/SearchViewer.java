package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.List;

import fr.tse.ProjetInfo3.mvc.controller.SearchTabController;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

public class SearchViewer {
    private User user;
    private RequestManager requestManager;

    private SearchTabController searchTabController;
    
    public void getListPropositions(String userProposition) {
    	List<User> users= requestManager.getUsersbyName(userProposition);
    	System.out.println(users);
    }
}
