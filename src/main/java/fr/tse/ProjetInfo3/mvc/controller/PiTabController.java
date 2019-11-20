package fr.tse.ProjetInfo3.mvc.controller;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the Edit PI window, all user interactions whith the Edit PI windows (not the tabs) are handled here
 */
public class PiTabController {

    @FXML
    private AnchorPane PIPane;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label piNameLabel;

    @FXML
    private Accordion accordion;

    @FXML
    private Label nbTweetsLabel;

    @FXML
    private Label nbTweets;


    private MainController mainController;

    private PIViewer piViewer;

    private InterestPoint interestPointToPrint;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }


    @FXML
    private void initialize() {
        //hide unused elements
        accordion.setVisible(false);
        nbTweets.setVisible(false);
        nbTweetsLabel.setVisible(false);
    }

    public void setDatas(PIViewer piViewer) {
        this.piViewer = piViewer;
        this.interestPointToPrint = piViewer.getSelectedInterestPoint();
        Platform.runLater(() -> {
            piNameLabel.setText(interestPointToPrint.getName());
        });
    }
}

