package fr.tse.ProjetInfo3.mwp.controller;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;

/**
 * @author Kamil CAGLAR
 * Controller of the main window
 */
public class MainController {
    /*
     *FXML elements are declared here
     */
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private SearchTabController searchTabController;

    /*This function is launched when Mainwindows is launched */
    @FXML
    private void initialize() {
        /*the controller can be used in search Tab*/
        searchTabController.injectMainController(this);
    }

}
