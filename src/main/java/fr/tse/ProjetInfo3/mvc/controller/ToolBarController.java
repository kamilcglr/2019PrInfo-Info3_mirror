package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;

public class ToolBarController {
    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;

    }

    @FXML
    private JFXButton piButton;

    @FXML
    private JFXButton favoriteButton;

    @FXML
    private void initialize() {
       }

    @FXML
    private void homeButtonPressed(ActionEvent event) {
        mainController.goToHome();
    }

    @FXML
    private void piButtonPressed(ActionEvent event) {
        mainController.goToMyPisPane();
    }

    @FXML
    private void favouriteButtonPressed(ActionEvent event) {
        mainController.goToMyFavsPane();
    }
}
