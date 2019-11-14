package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.List;

import com.jfoenix.controls.JFXListView;

import fr.tse.ProjetInfo3.mvc.controller.SearchTabController;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.collections.ObservableList;

public class SearchViewer {
    private User user;
    private RequestManager requestManager;

    private SearchTabController searchTabController;
    
    public SearchViewer() {
        requestManager = new RequestManager();

    }
    
    public List<String> getListPropositions(String userProposition) {
        //ObservableList<String> items =listView.getItems();;
    	List<String> users= requestManager.getUsersbyName(userProposition);
    	//users.forEach(user->items.add(user));
    	//items.forEach(item->System.out.println(item));
    	return users;
    	    }
    
    public User getUser() {
        return user;
    }
}
