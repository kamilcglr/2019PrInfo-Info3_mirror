package fr.tse.ProjetInfo3.mvc.controller;

import javafx.fxml.FXML;

public class PiTabController {
    private MainController mainController;
    
    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println(this.mainController.toString());
    }
    
    @FXML
    private void initialize() {
    	
    }
}
