package fr.tse.ProjetInfo3.mvc.controller;

import java.io.FileInputStream;
import java.net.URL;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
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
import javafx.collections.ObservableList;
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

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the List PI window, all user interactions whith the LIST PI windows (not the tabs) are handled here
 * 
 */
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
				for(Hashtag hash : interestPoint.getHashtags()) {
					try {
						listPI.getItems().add(interestPoint.getTitle()+"\t "+us.getDate()+"\n"+"@"+us.getName()+" "+hash.getHashtag());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		editPI.setOnAction(e -> editButtonClicked());
		
	}
	
	

	private void editButtonClicked() {
		//InterestPoint ip = new InterestPoint();
		String message = "";
		ObservableList<String> ips = listPI.getSelectionModel().getSelectedItems();
		for(String s : ips) {
			message +=s+"\n";
		}
		System.out.println(message);
		mainController.goToEditPiPane(message);
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
		//mainController.goToEditPiPane();
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
		User macron = requestManager.getUser("EmmanuelMacron");
		users.add(trump);
		users.add(macron);
		InterestPoint ip = new InterestPoint(hashtags, users,"Politique");
		List<InterestPoint> interestPoints = new ArrayList<InterestPoint>();
		interestPoints.add(ip);
		ip.setInterestPoints(interestPoints);
		return ip;
	}
}
