package fr.tse.ProjetInfo3.mvc.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the Edit PI window, all user interactions whith the Edit PI windows (not the tabs) are handled here
 * 
 */
public class PiTabController {

    @FXML
    private AnchorPane PIPane;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;
    
    private MainController mainController;
    
 	/*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        
    }
}

