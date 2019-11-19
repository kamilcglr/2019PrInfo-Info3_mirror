package fr.tse.ProjetInfo3.mvc.controller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.ListOfInterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

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

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Everything is in a separated thread because it is a heavy task (calls to Databse...)
        // We do not want a frozen interface
        Platform.runLater(()-> isLoading(true));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<InterestPoint> ip = null;
                try {
                    ip = initializeListOfInterestPoints();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String spacing = "\t\t\t\t\t\t\t\t";

                //sets the datas
                for (InterestPoint interestPoint : ip) {
                    for (User us : interestPoint.getUsers()) {
                        for (Hashtag hash : interestPoint.getHashtags()) {
                            try {
                                listPI.getItems().add(interestPoint.getTitle() + spacing + sdf.format(us.parseTwitterUTC()) + "\n" + "@" + us.getName() + " " + hash.getHashtag());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                Platform.runLater(()-> isLoading(false));
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
    private void isLoading(boolean isLoading){

    }
}
