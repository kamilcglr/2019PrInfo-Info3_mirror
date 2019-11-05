package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXScrollPane;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class HashtagTabController {
    private MainController mainController;


    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {

    }
}
