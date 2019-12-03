package fr.tse.ProjetInfo3.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.Cell;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.viewer.PITabViewer;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the Edit PI window, all user interactions whith the Edit PI windows (not the tabs) are handled here
 */
public class PiTabController {

    @FXML
    private AnchorPane PIPane;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label piNameLabel;

    @FXML
    private Accordion accordion;

    @FXML
    private Label nbTweetsLabel;

    @FXML
    private Label nbTweets;

    @FXML
    private JFXButton editButton;


    private MainController mainController;
    
    @FXML
    private JFXListView<ResultHashtag> topTenLinkedList;
    @FXML
    private JFXProgressBar progressBar;

    private PIViewer piViewer;
    private PITabViewer piTabViewer;
    private UserViewer userViewer=new UserViewer();
    Map<String,Integer> hashtags;
    List<String> myHashtags=new ArrayList<>(); 
    List<User> myUsers=new ArrayList<>(); 
   // User user =new User(0, null, "realDonaldTrump", null, null, null, null, 0, 0, 0, 0, 0, null, null, null, null);
    

    private InterestPoint interestPointToPrint;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }


    @FXML
    private void initialize() {
        //hide unused elements
     //   accordion.setVisible(false);
       // nbTweets.setVisible(false);
       // nbTweetsLabel.setVisible(false);
   //     editButton.setVisible(false);
    	topTenLinkedList.setCellFactory(param -> new Cell());


    }


    public void setMyPITabViewer(PITabViewer piTabViewer,PIViewer piViewer) {
    	myHashtags.add("macron");
    	myHashtags.add("trump");
    	//myUsers.add("realdonaldtrump");
    	try {
			myUsers.add(userViewer.searchScreenNameU("@realdonaldtrump"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        this.piTabViewer = piTabViewer;
        this.piViewer=piViewer;
		piTabViewer.getMyHashtags().forEach(ha->System.out.println(ha));

        this.interestPointToPrint = piViewer.getSelectedInterestPoint();
        hashtags=piTabViewer.getListOfHashtagsforPI(progressBar,myHashtags,myUsers);

        Platform.runLater(() -> {
            piNameLabel.setText(interestPointToPrint.getName());
        });

        Thread thread = new Thread(setTopLinkedHashtag());
        thread.setDaemon(true);
        thread.start();
    }
    
    private Task<Void> setTopLinkedHashtag() {
       // List<String> hashtagPI = piTabViewer.getHashtagsOfHashtags();
        hashtags = piTabViewer.getListOfHashtagsforPI(progressBar,myHashtags,myUsers);

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtags.keySet()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag, hashtags.get(hashtag).toString()));
            i++;
            if (i == 10) {
                break;
            }
        }
        Platform.runLater(() -> {
        	topTenLinkedList.getItems().addAll(hashtagsToPrint);
            //titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });

        return null;
    }
}

