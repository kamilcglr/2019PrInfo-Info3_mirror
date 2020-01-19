package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.UserApp;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.HashtagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Kamil CAGLAR Controller of the main window, all user interactions
 * whith the main windows (not the tabs) are handled here Subcontrollers
 * (searchTabController, userTabController, userTabFromMain) are called
 * here
 */
public class MainController {
    private Scene scene;
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
    private MyPIsTabController myPIsTabController;

    @FXML
    private FavsController favsController;

    @FXML
    private StatisticsTabController statisticsTabController;

    @FXML
    private SearchTabController searchTabController;

    @FXML
    private Tab searchTabFromMain;


    /*
     * Viewers
     */
    private PIViewer piViewer;
    private FavsViewer favsViewer;

    /*
     * DatabaseManager
     */
    private DatabaseManager databaseManager;

    private boolean connected;
    private UserApp userApp;

    private Tab myPisTab;
    private Tab myFavsTab;


    /*This function is launched when Mainwindow is launched */
    @FXML
    private void initialize() {
        databaseManager = new DatabaseManager();
        connected = false;
        piViewer = new PIViewer();
        favsViewer = new FavsViewer(databaseManager);

        //TABS can be closed
        tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);

        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this, hamburger);

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
        if (!isAlreadyOpen(userViewer.getUser().getName())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UserTab.fxml"));
            try {
                AnchorPane newUserTab = fxmlLoader.load();
                UserTabController userTabController = fxmlLoader.getController();
                userTabController.injectMainController(this, favsViewer);
                Tab tab = new Tab();
                Platform.runLater(() -> {
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

                tab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        userTabController.killThreads();
                        thread.interrupt();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToHashtagPane(HashtagViewer hashtagViewer) {
        if (!isAlreadyOpen("#" + hashtagViewer.getHashtag().getHashtag())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/HashtagTab.fxml"));
            try {
                AnchorPane newHashtagTab = fxmlLoader.load();
                HashtagTabController hashtagTabController = fxmlLoader.getController();
                hashtagTabController.injectMainController(this, favsViewer);
                Tab tab = new Tab();
                Platform.runLater(() -> {
                    tab.setContent(newHashtagTab);
                    tab.setText("#" + hashtagViewer.getHashtag().getHashtag());
                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                });

                //Heavy task inside this thread, we go to user pane before
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        hashtagTabController.setHashtagViewer(hashtagViewer);
                        return null;
                    }
                };

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

                tab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        hashtagTabController.killThreads();
                        thread.interrupt();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToLoginPane() {
        if (!isAlreadyOpen("Login")) { //The string must be the same in tab.setText
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            try {
                AnchorPane loginTab = fxmlLoader.load();
                LoginController loginController = fxmlLoader.getController();
                loginController.injectMainController(this, databaseManager, favsViewer);
                Tab tab = new Tab();
                Platform.runLater(() -> {
                    if (!isConnected()) {
                        tab.setContent(loginTab);
                        tab.setText("Login");
                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToMyPisPane() {
        //We declare this controller here, it will be used when the tab already exist in the else
        if (myPisTab == null) { //the tab is not initialised/charged in memory
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MyPIsTab.fxml"));
            try {
                AnchorPane myPiTab = fxmlLoader.load();
                myPIsTabController = fxmlLoader.getController();
                myPIsTabController.injectMainController(this);
                myPisTab = new Tab();
                Platform.runLater(() -> {
                    myPisTab.setContent(myPiTab);
                    myPisTab.setText("Mes Points d'interets");
                    tabPane.getTabs().add(myPisTab);
                    tabPane.getSelectionModel().select(myPisTab);
                    myPisTab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event e) {
                            tabPane.getTabs().remove(myPisTab);
                            myPisTab = null;
                        }
                    });
                });

                //Heavy task inside this thread, we go to user pane before
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        myPIsTabController.setPiViewer(piViewer);
                        return null;
                    }
                };

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                myPisTab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        myPIsTabController.killThreads();
                        thread.interrupt();
                        myPisTab = null;
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //the tab is already initialized, so we just refresh the list of PIs
            Platform.runLater(() -> {
                tabPane.getSelectionModel().select(myPisTab);
                myPIsTabController.refreshPIs();
            });
        }
        if (!drawer.isClosed()) {
            drawer.close();
        }
    }

    public void goToMyFavsPane() {
        //We declare this controller here, it will be used when the tab already exist in the else
        if (myFavsTab == null) { //the tab is not initialised/charged in memory
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FavsTab.fxml"));
            try {
                AnchorPane newFavTab = fxmlLoader.load();
                favsController = fxmlLoader.getController();
                favsController.injectMainController(this, favsViewer);
                myFavsTab = new Tab();
                Platform.runLater(() -> {
                    myFavsTab.setContent(newFavTab);
                    myFavsTab.setText("Mes Favoris");
                    tabPane.getTabs().add(myFavsTab);
                    tabPane.getSelectionModel().select(myFavsTab);
                });

                //Heavy task inside this thread, we go to user pane before
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        favsController.setFavsViewer();
                        return null;
                    }
                };

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

                myFavsTab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        favsController.killThreads();
                        thread.interrupt();
                        myFavsTab = null;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //the tab is already initialized, so we just refresh the list of PIs
            Platform.runLater(() -> {
                tabPane.getSelectionModel().select(myFavsTab);
                //Heavy task inside this thread, we go to user pane before
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        favsController.refreshFavs();
                        return null;
                    }
                };
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            });
        }
        if (!drawer.isClosed()) {
            drawer.close();
        }
    }

    public void goToHome() {
        tabPane.getSelectionModel().select(searchTabFromMain);
        drawer.close();
    }

    public void goToHomeRefresh() {
        tabPane.getTabs().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SearchTab.fxml"));
        try {

            AnchorPane searchTab = fxmlLoader.load();
            SearchTabController searchController = fxmlLoader.getController();
            searchController.injectMainController(this, hamburger);
            Tab tab = new Tab();
            searchTabFromMain = tab;
            tab.setContent(searchTab);
            tab.setText("Rechercher");
            tab.closableProperty().set(false);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);


        } catch (Exception e) {
            // TODO: handle exception
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

                if (!isNew) {
                    System.out.println(piViewer.getSelectedInterestPoint().getHashtags());
                    piTabCreateController.setPiToEdit(piViewer.getSelectedInterestPoint());
                    System.out.println("Is not new");
                }

                piTabCreateController.setIsNew(isNew);
                piTabCreateController.injectTabContainer(tabPane);
                piTabCreateController.injectTab(tab);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToSigninTab() {
        if (!isAlreadyOpen("SignIn")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SignInTab.fxml"));
            try {
                AnchorPane loginTab = fxmlLoader.load();
                SignInController loginController = fxmlLoader.getController();
                loginController.injectMainController(this);
                Tab tab = new Tab();
                Platform.runLater(() -> {
                    // if(loginController.connected==0) {
                    tab.setContent(loginTab);
                    tab.setText("SignIn");
                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);

                    loginController.injectTabContainer(tabPane);
                    loginController.injectTab(tab);
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToSelectedPi(PIViewer piViewer) {
        if (!isAlreadyOpen(piViewer.getSelectedInterestPoint().getName())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PiTab.fxml"));
            try {
                AnchorPane piTab = fxmlLoader.load();
                PiTabController piTabController = fxmlLoader.getController();
                piTabController.injectMainController(this);
                Tab tab = new Tab();
                Platform.runLater(() -> {
                    tab.setContent(piTab);
                    tab.setText(piViewer.getSelectedInterestPoint().getName());
                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                });

                //Heavy task inside this thread, we go to user pane before
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        piTabController.setDatas(piViewer);
                        return null;
                    }
                };

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

                tab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        piTabController.killThreads();
                        thread.interrupt();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param piViewer
     * @param bigTweetList
     * @author Sergiy
     * This method is used to load the fxml document of the Statistics Tab,
     * to get the corresponding controller and to open a new tab containing the Charts.
     */
    public void goToStatistics(PIViewer piViewer, List<Tweet> bigTweetList) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StatisticsTab.fxml"));

        try {
            AnchorPane statisticsPane = fxmlLoader.load();
            System.out.println(statisticsPane);
            statisticsTabController = fxmlLoader.getController();
            statisticsTabController.setTweetList(bigTweetList);

            Platform.runLater(() -> {
                Tab tab = new Tab();
                tab.setContent(statisticsPane);
                tab.setText("Statistiques");
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);

                tab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        statisticsTabController.killThreads();
                    }
                });
            });

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    statisticsTabController.setDatas(piViewer);
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

    public void closeCurrentTab() {
        EventHandler handler = tabPane.getSelectionModel().getSelectedItem().getOnCloseRequest();
        if (handler != null) {
            handler.handle(null);
        }
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
    }

    private boolean isAlreadyOpen(String name) {
        List<Tab> matchingTabs = tabPane.getTabs().stream().filter(tab -> tab.getText().equals(name)).collect(Collectors.toList());
        if (matchingTabs.size() > 0) {
            tabPane.getSelectionModel().select(matchingTabs.get(0));
            return true;
        } else {
            return false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public UserApp getUserApp() {
        return userApp;
    }

    public void setUserApp(UserApp userApp) {
        this.userApp = userApp;
    }

    public Scene getScene() {
        return hamburger.getScene();
    }
}
