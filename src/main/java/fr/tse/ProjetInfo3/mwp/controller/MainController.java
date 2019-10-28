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
    private PiTabController piTabController ;
    @FXML
    private UserTabController userTabController;

    /*This function is launched when Mainwindows is launched */
    @FXML
    private void initialize() {
        /*the controller can be used in other Tabs*/
        searchTabController.injectMainController(this);
        userTabController.injectMainController(this);
        piTabController.injectMainController(this);

    }
    //public void init() {
        //tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable,
        //                                                                Tab oldValue, Tab newValue) -> {
        //    if (newValue == tab2_bar) {
        //        System.out.println("- 2.Tab bar -");
        //        System.out.println("xxx_tab2bar_xxxController=" + xxx_tab2bar_xxxController); //if =null => inject problem
        //        xxx_tab2bar_xxxController.handleTab2ButtonBar();
        //    } else if (newValue == tab1_foo) {
        //        System.out.println("- 1.Tab foo -");
        //        System.out.println("xxx_tab1foo_xxxController=" + xxx_tab1foo_xxxController); //if =null => inject problem
        //        xxx_tab1foo_xxxController.handleTab1ButtonFoo();
        //    } else {
        //        System.out.println("- another Tab -");
        //    }
        //});

}
