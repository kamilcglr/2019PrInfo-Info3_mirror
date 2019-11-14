package fr.tse.ProjetInfo3.mvc.controller;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.ListOfInterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MyPIsTabController extends ListView<String> implements Initializable {
    private MainController mainController;
    
    @FXML
    private JFXListView<String> listPI;
    
    @FXML
    private JFXPopup popup;

    @FXML
    private JFXButton addPI;

    @FXML
    private JFXButton editPI;
    
    	/*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		InterestPoint ip = initializeListOfInterestPoints();
		
		for(InterestPoint interestPoint : ip.getInterestPoints()) {
			for(User us : interestPoint.getUsers()) {
				listPI.getItems().add(us.getName());
			}
			
		}
		
	}

	@FXML
	private void load(ActionEvent event) {
		if(!listPI.isExpanded()) {
			listPI.setExpanded(true);
			listPI.depthProperty().set(1);
		}else {
			listPI.setExpanded(false);
			listPI.depthProperty().set(0);
		}
	}
	
	@FXML
    void addPIPressed(ActionEvent event) {
		// will go to the create PI class
		System.out.println("Class create PI");
    }
	
	@FXML
    void editPIPressed(ActionEvent event) {
		// will go to the edit Pi class
		System.out.println("Class edit PI");
    }
	
	public InterestPoint initializeListOfInterestPoints() {
		List<Hashtag> hashtags = new ArrayList<>();
		Hashtag president = new Hashtag("#president");
		Hashtag congres = new Hashtag("#congrés");
		Hashtag meetup = new Hashtag("#meetup");
		hashtags.add(president);
		hashtags.add(congres);
		hashtags.add(meetup);
		List<User> users = new ArrayList<>();
        RequestManager requestManager = new RequestManager();
		User trump = requestManager.getUser("realdonaldtrump");
		User sob = requestManager.getUser("SobunUng");
		users.add(trump);
		users.add(sob);
		InterestPoint ip = new InterestPoint(hashtags, users,"Politique");
		List<InterestPoint> interestPoints = new ArrayList<InterestPoint>();
		interestPoints.add(ip);
		ip.setInterestPoints(interestPoints);
		return ip;
	}
}
