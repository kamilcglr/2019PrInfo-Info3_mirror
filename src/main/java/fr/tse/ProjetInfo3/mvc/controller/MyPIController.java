package fr.tse.ProjetInfo3.mvc.controller;

import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;

import fr.tse.ProjetInfo3.mvc.viewer.PITabViewer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/***
 *
 * @author Sergiy
 *
 */
public class MyPIController {
    private MainController mainController;
    private PITabViewer piTabViewer;
    Map<String,Integer> hashtags;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }



    @FXML
    private void initialize() {

    }

 
}
