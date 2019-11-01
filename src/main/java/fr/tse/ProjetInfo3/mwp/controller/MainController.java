package fr.tse.ProjetInfo3.mwp.controller;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.Stack;

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
    private Tab userTabFromMain;
    @FXML
    private Tab hashtagTabFromMain;
    @FXML
    private Tab piTabFromMain;
    @FXML
    private Tab searchTabFromMain;


    /*This function is launched when Mainwindow is launched */
    @FXML
    private void initialize() {
        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this);
        userTabController.injectMainController(this);
        piTabController.injectMainController(this);

    }

    public void goToUserPane() {
        tabPane.getSelectionModel().select(userTabFromMain);
        ;
    }

    public void goToHashtagPane() {
        tabPane.getSelectionModel().select(hashtagTabFromMain);
        ;
    }


}
