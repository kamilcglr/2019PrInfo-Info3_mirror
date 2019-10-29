package fr.tse.ProjetInfo3.mwp.controller;

import com.jfoenix.controls.JFXTabPane;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

/**
 * @author Kamil CAGLAR
 * Controller of the main window, all user interactions whith the main windows (not the tabs) are handled here
 */
public class MainController {
    /*
     *FXML elements are declared here
     */
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private SearchTabController searchTabController;
    @FXML
    private PiTabController piTabController;
    @FXML
    private UserTabController userTabController;
    @FXML
    private Tab userTabFromMain;
    @FXML
    private Tab piTabFromMain;
    @FXML
    private Tab searchTabFromMain;


    /*This function is launched when Mainwindows is launched */
    @FXML
    private void initialize() {
        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this);
        userTabController.injectMainController(this);
        piTabController.injectMainController(this);

    }

    public void goToSearchPane(){
        tabPane.getSelectionModel().select(userTabFromMain);;
    }
}
