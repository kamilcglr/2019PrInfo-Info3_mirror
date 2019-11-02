package fr.tse.ProjetInfo3.mvc.controller;

import javafx.fxml.FXML;

public class UserTabController {
    private MainController mainController;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {

    }
}
