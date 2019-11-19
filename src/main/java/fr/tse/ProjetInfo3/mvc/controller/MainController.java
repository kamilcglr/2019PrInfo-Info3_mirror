package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * @author Kamil CAGLAR
 * Controller of the main window, all user interactions whith the main windows (not the tabs) are handled here
 * Subcontrollers (searchTabController, userTabController, userTabFromMain) are called here
 */
public class MainController {
    /*
     *FXML elements are declared here
     */
    @FXML
    private AnchorPane root;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private SearchTabController searchTabController;
    @FXML
    private PiTabController piTabController;
    @FXML
    private UserTabController userTabController;
    @FXML
    private HashtagTabController hashtagTabController;
    @FXML
    private MyPIsTabController myPIsTabController;
    
    @FXML
    private Tab userTabFromMain;
    //@FXML
    //private Tab hashtagTabFromMain;
    @FXML
    private Tab piTabFromMain;
    @FXML
    private Tab searchTabFromMain;
    @FXML
    private Tab loginTabFromMain;
   
    
    /*This function is launched when Mainwindow is launched */
    @FXML
    private void initialize() {
        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this);
        userTabController.injectMainController(this);
        piTabController.injectMainController(this);

        //hashtagTabController.injectMainController(this);
    }

    /**
     * Called by searchButton
     * Pass the userViewer as parameters to use it in the controller of UserTab
     * @param userViewer
     */
    public void goToUserPane(UserViewer userViewer) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UserTab.fxml"));
        try {
            AnchorPane newUserTab = fxmlLoader.load();
            UserTabController userTabController = fxmlLoader.getController();
            userTabController.injectMainController(this);
            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(newUserTab);
                tab.setText(userViewer.getUser().getName());
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                userTabController.setUserViewer(userViewer);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToHashtagPane(HastagViewer hastagViewer) {
        //tabPane.getSelectionModel().select(hashtagTabFromMain);
        //hashtagTabController.setHastagViewer(hastagViewer);
    }

    public void goToLoginPane() {
        tabPane.getSelectionModel().select(loginTabFromMain);
    }

	public void goToMyPisPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MyPIsTab.fxml"));
        try {
            AnchorPane myPiTab = fxmlLoader.load();
            MyPIsTabController myPisTabController = fxmlLoader.getController();
            myPisTabController.injectMainController(this);
            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(myPiTab);
                tab.setText("Mes Points d'interets");
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void goToEditPiPane(InterestPoint interestPoint) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PiTabEdit.fxml"));
        try {
            AnchorPane editPiTab = fxmlLoader.load();
            PiTabController piTabController = fxmlLoader.getController();
            piTabController.injectMainController(this);
            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(editPiTab);
                tab.setText("éditer le Point");
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            });
          
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
    
    
}

