package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author ALAMI IDRISSI Taha
 * Controller of the List PI window, all user interactions whith the LIST PI windows (not the tabs) are handled here
 */
public class MyPIsTabController extends ListView<String> implements Initializable {
    private MainController mainController;

    @FXML
    private JFXListView<String> listPI;

    @FXML
    private JFXPopup popup;

    @FXML
    private JFXButton addPI;

    @FXML
    private JFXButton editPI;

    @FXML

    private InterestPoint ip;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Everything is in a separated thread because it is a heavy task (calls to Databse...)
        // We do not want a frozen interface
        Platform.runLater(() -> isLoading(true));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<InterestPoint> ip = null;
                List<InterestPoint> ipSaved = null;
                try {
                    ip = initializeListOfInterestPoints();
                    PIViewer piViewer = new PIViewer();
                    ipSaved = piViewer.getListOfInterestPointFromDataBase();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                //sets the datas
                for (InterestPoint interestPoint : ip) {
                    listPI.getItems().add(interestPoint.toStringMinimal());
                }
                for (InterestPoint interestPoint : ipSaved) {
                    listPI.getItems().add(interestPoint.toStringMinimal());
                }
                Platform.runLater(() -> isLoading(false));
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        //This buttons will be visible when editing will be possible
        editPI.setVisible(false);
    }

    @FXML
    private void load(ActionEvent event) {
        if (!listPI.isExpanded()) {
            listPI.setExpanded(true);
            listPI.depthProperty().set(1);
        } else {
            listPI.setExpanded(false);
            listPI.depthProperty().set(0);
        }
    }


    private void editButtonClicked() {
        // En cliquant sur le point d'interet de notre choix puis edit les informations de ce dernier
        // seront transmis via cette methode vers le mainController

        InterestPoint returnedInterestPoint = new InterestPoint();
        for (InterestPoint i : ip.getInterestPoints()) {
            returnedInterestPoint.setHashtags(i.getHashtags());
            returnedInterestPoint.setTitle(i.getTitle());
            returnedInterestPoint.setTweets(i.getTweets());
        }
        mainController.goToPICreateOrEditPane(false, returnedInterestPoint);
    }

    @FXML
    void addPIPressed(ActionEvent event) {
        //New PI, then we add true in the parameters
        mainController.goToPICreateOrEditPane(true, null);
    }

    @FXML
    void editPIPressed(ActionEvent event) {
        //Not new, then we add true in the parameters
        listPI.getSelectionModel().getSelectedItem();
        //TODO pass PI to modify as argument
        mainController.goToPICreateOrEditPane(false, null);
    }

    /**
     * calls the viewer to get IP from database
     *
     * @return List of user IPs
     * @throws IOException
     * @throws InterruptedException
     */
    public List<InterestPoint> initializeListOfInterestPoints() throws IOException, InterruptedException {
        PIViewer piViewer = new PIViewer();
        return piViewer.getlistOfInterestPoint();
    }

    /**
     * TODO add spinner or progressBar during loading
     */
    private void isLoading(boolean isLoading) {

    }
}
