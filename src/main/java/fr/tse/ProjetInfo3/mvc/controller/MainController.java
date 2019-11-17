package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private JFXDrawer drawer;
    @FXML
    private JFXHamburger hamburger;
    /*
    * Controllers
    * */
    @FXML
    private SearchTabController searchTabController;
    @FXML
    private PiTabController piTabController;
    @FXML
    private UserTabController userTabController;
    @FXML
    private HashtagTabController hashtagTabController;
    @FXML
    private MyPIController myPIController;
    @FXML
    private ToolBarController toolBarController;

    //@FXML
    //private Tab hashtagTabFromMain;
    @FXML
    private Tab userTabFromMain;
    @FXML
    private Tab piTabEditFromMain;
    @FXML
    private Tab piTabFromMain;
    @FXML
    private Tab myPITabFromMain;
    @FXML
    private Tab searchTabFromMain;
    @FXML
    private Tab loginTabFromMain;

    /*This function is launched when Mainwindow is launched */
    @FXML
    private void initialize() {
        tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);

        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this);
        userTabController.injectMainController(this);
        piTabController.injectMainController(this);
        //hashtagTabController.injectMainController(this);
        //myPIController.injectMainController(this);
        
        //goToPICreatePane();

        initDrawer();
    }

    /**
     * Contains the toolbar on the right
     */
    private void initDrawer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ToolBar.fxml"));
            VBox toolbar = loader.load();
            drawer.setSidePane(toolbar);
            ToolBarController toolBarController = loader.getController();
            toolBarController.injectMainController(this);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            drawer.toggle();
        });
        drawer.setOnDrawerOpening((event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.toFront();
        });
        drawer.setOnDrawerClosed((event) -> {
            drawer.toBack();
            task.setRate(task.getRate() * -1);
            task.play();
        });
    }

    /**
     * Called by searchButton
     * Pass the userViewer as parameters to use it in the controller of UserTab
     *
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
            });

            //Heavy task inside this thread, we go to user pane before
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    userTabController.setUserViewer(userViewer);
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

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
    
    public void goToPICreatePane() {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PiTabCreate.fxml"));
    	try {
            AnchorPane newUserTab = fxmlLoader.load();
            PiTabCreateController piTabCreateController = fxmlLoader.getController();
            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(newUserTab);
                tab.setText("Création d'un Point d'Intêret");
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                
                piTabCreateController.injectTabContainer(tabPane);
                piTabCreateController.injectTab(tab);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToHome(){
        tabPane.getSelectionModel().select(searchTabFromMain);
        drawer.close();
    }
}

