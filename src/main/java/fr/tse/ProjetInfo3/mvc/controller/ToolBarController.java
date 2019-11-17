package fr.tse.ProjetInfo3.mvc.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ToolBarController {
    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void homeButtonPressed(ActionEvent event) {
        mainController.goToHome();
    }
}
