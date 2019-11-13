package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListView;

import javafx.fxml.FXML;

public class MyPIsTabController {
    private MainController mainController;
    @FXML
    private JFXListView listPIs;
    
    	/*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        
    }

}
