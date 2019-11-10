package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;

/***
 * 
 * @author Sergiy
 *
 */
public class MyPIController {
	private MainController mainController;
    
    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    
    @FXML
    private JFXButton createPI;
    
    @FXML
	private void initialize() {

	}
}
