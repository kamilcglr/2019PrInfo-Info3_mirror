package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ToolBarController {
    private MainController mainController;

    @FXML
    private JFXButton favoriteButton;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        //hide unusued elements
        favoriteButton.setVisible(true);
    }

    @FXML
    private void homeButtonPressed(ActionEvent event) {
        mainController.goToHome();
    }

    @FXML
    private void piButtonPressed(ActionEvent event){
        mainController.goToMyPisPane();
    }
    
    @FXML 
    private void favouriteButtonPressed(ActionEvent event) {
    	mainController.goToMyFavsPane();
    }
}
