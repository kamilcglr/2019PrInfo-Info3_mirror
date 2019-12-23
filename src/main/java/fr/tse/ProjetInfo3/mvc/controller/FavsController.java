/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXSpinner;

import fr.tse.ProjetInfo3.mvc.controller.PiTabCreateController.UserCell;
import fr.tse.ProjetInfo3.mvc.dto.Favourite;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SearchUser;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleTopHashtagCell;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleUserCell;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/**
 * @author Laïla
 *
 */
public class FavsController {
	
	
	//THREAD
    private Thread threadGetFavs;

	//FXML
	@FXML
	private JFXListView<ResultHashtag> FavsListView;
	@FXML
	private JFXListView<String> FavsListViewUser;
	  @FXML
	    private AnchorPane anchorPane;

    //Progress indicator
    @FXML
    private JFXSpinner progressIndicator;
    @FXML
    private Label progressIndicatorLabel;

    private MainController mainController;
    private FavsViewer favsViewer;

    @FXML
    private void initialize() {
        //hide elements
    	progressIndicator.setVisible(false);
    	progressIndicatorLabel.setVisible(false);
       // compareButton.setVisible(false);
        //favoriteToggle.setVisible(true);
        //JFXScrollPane.smoothScrolling(scrollPane);

        FavsListView.setCellFactory(param -> new SimpleTopHashtagCell());
        //FavsListViewUser.setCellFactory(param-> new SearchUser());
        setTopHashtags();
        setUsers();
      //  Platform.runLater(() -> hideLists(true));

    }
	public void injectMainController(MainController mainController) {
        this.mainController = mainController;
	}
	
	@FXML
    private void setTopHashtags() {
		favsViewer=new FavsViewer();
    	Favourite favourite=favsViewer.getlistOfFavourites();
    	
        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Hashtag hashtag : favourite.getHashtags()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag.getHashtag(), hashtag.getHashtag()));
            System.out.println(hashtag.getHashtag());
            i++;
            //if (i == 5) {
              //  break;
           // }
        }
        Platform.runLater(() -> {
            FavsListView.getItems().addAll(hashtagsToPrint);
            //titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });
        
    }
	
	@FXML
	private void setUsers() {
		favsViewer=new FavsViewer();
    	Favourite favourite=favsViewer.getlistOfFavourites();
    	
        ObservableList<String> usersToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (User user : favourite.getUsers()) {
            usersToPrint.add(user.getScreen_name());
            System.out.println(user.getName());
            i++;
            //if (i == 5) {
              //  break;
           // }
        }
        Platform.runLater(() -> {
            FavsListViewUser.getItems().addAll(usersToPrint);
            //titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });
	}

	/*private Task<Void> getListOfFavs() {
		
        Platform.runLater(() -> {
            isLoading(true);
            FavsListView.getItems().clear();
        });

        //Get the PI and set them on the listView
        Favourite favourites =favsViewer.getlistOfFavourites();
        
        Platform.runLater(() -> {
            for (User user : favourites.getUsers()) {
                FavsListView.getItems().add(user.getScreen_name());
                System.out.println("haha");
            }
            for(Hashtag hashtag :favourites.getHashtags()) {
            	FavsListView.getItems().add(hashtag.getHashtag());
            }
            isLoading(false);
        });
        return null;
    }*/
    public Favourite initializeListOfFavourites() throws IOException, InterruptedException {
        return favsViewer.getlistOfFavourites();
    }
    private void isLoading(boolean isLoading) {
        if (isLoading) {
            FavsListView.setVisible(false);
           // seeButton.setVisible(false);
           // addPI.setVisible(false);
           // deletePI.setVisible(false);
            progressIndicatorLabel.setText("Récupération des sauvegardes, veuillez patienter...");
            progressIndicator.setVisible(true);
            progressIndicatorLabel.setVisible(true);

        } else {
            FavsListView.setVisible(true);
           // addPI.setVisible(true);
            progressIndicator.setVisible(false);
            progressIndicatorLabel.setVisible(false);
        }
    }
}
