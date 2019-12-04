package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXTabPane;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.PITabViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
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
    private MyPIsTabController myPIsTabController;

    private MyPIController myPIController;
    @FXML
    private ToolBarController toolBarController;


    /*
     * These are necessary for tests
     * */
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

    /*
     * Viewers
     */
    private PIViewer piViewer;
    private PITabViewer piTabViewer;

    private Tab myPisTab;

    /*This function is launched when Mainwindow is launched */
    @FXML
    private void initialize() {
        piViewer = new PIViewer();
        piTabViewer = new PITabViewer();
        //TABS can be closed
        tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);

        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this);
        //userTabController.injectMainController(this);
        //piTabController.injectMainController(this);
        //hashtagTabController.injectMainController(this);

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/HashtagTab.fxml"));
        try {
            AnchorPane newHashtagTab = fxmlLoader.load();
            HashtagTabController hashtagTabController = fxmlLoader.getController();
            hashtagTabController.injectMainController(this);
            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(newHashtagTab);
                tab.setText("#" + hastagViewer.getHashtag().getHashtagName());
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            });

            //Heavy task inside this thread, we go to user pane before
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    hashtagTabController.setHastagViewer(hastagViewer);
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

    public void goToLoginPane() {
        tabPane.getSelectionModel().select(loginTabFromMain);
    }


    public void goToMyPisPane() {
        //We declare this controller here, it will be used when the tab already exist in the else
        if (myPisTab == null) { //the tab is not initialised/charged in memory
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MyPIsTab.fxml"));
            try {
                AnchorPane myPiTab = fxmlLoader.load();
                myPIsTabController = fxmlLoader.getController();
                myPIsTabController.injectMainController(this);
                Platform.runLater(() -> {
                    myPisTab = new Tab();
                    myPisTab.setContent(myPiTab);
                    myPisTab.setText("Mes Points d'interets");
                    tabPane.getTabs().add(myPisTab);
                    tabPane.getSelectionModel().select(myPisTab);
                    //do this task at the end !
                    myPIsTabController.setPiViewer(piViewer, piTabViewer);
                    myPisTab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event e) {
                            tabPane.getTabs().remove(myPisTab);
                            myPisTab = null;
                        }
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { //the tab is already initialized
            Platform.runLater(() -> {
                tabPane.getSelectionModel().select(myPisTab);
                myPIsTabController.refreshPIs();
            });
        }
        if (!drawer.isClosed()) {
            drawer.close();
        }
    }

    public void goToPICreateOrEditPane(boolean isNew, PIViewer piViewer) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PiTabCreate.fxml"));
        try {
            AnchorPane newUserTab = fxmlLoader.load();
            PiTabCreateController piTabCreateController = fxmlLoader.getController();
            piTabCreateController.injectMainController(this);
            piTabCreateController.setPiViewer(piViewer);
            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(newUserTab);
                tab.setText("Création d'un Point d'Intérêt");
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                piTabCreateController.setIsNew(isNew);
                piTabCreateController.injectTabContainer(tabPane);
                piTabCreateController.injectTab(tab);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToHome() {
        tabPane.getSelectionModel().select(searchTabFromMain);
        drawer.close();
    }

    public void goToSelectedPi(PIViewer piViewer) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PiTab.fxml"));
        try {
            AnchorPane piTab = fxmlLoader.load();
            PiTabController piTabController = fxmlLoader.getController();
            Tab tab = new Tab();
            Platform.runLater(() -> {
                tab.setContent(piTab);
                tab.setText(piViewer.getSelectedInterestPoint().getName());
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                //do this task at the end !
                piTabController.setDatas(piViewer);
            });
            tab.setOnCloseRequest(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

