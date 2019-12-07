package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.viewer.PITabViewer;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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

    /* THREADS
     * Every thread should be declared here to kill them when exiting
     */
    private Thread threadGetPIs;

    @FXML
    private JFXListView<String> PIListView;

    @FXML
    private JFXPopup popup;

    @FXML
    private JFXButton addPI;
    
    @FXML
    private JFXButton editPI;
    
    @FXML
    private JFXSpinner deletionTime;
    
    @FXML
    private JFXButton deletePI;
    
    @FXML
    private JFXButton seeButton;

    private PIViewer piViewer;

    //Progress indicator
    @FXML
    private JFXSpinner progressIndicator;
    @FXML
    private Label progressIndicatorLabel;

    public MyPIsTabController() {
    }

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //This buttons will be visible when editing will be possible
        editPI.setVisible(false);
        deletePI.setVisible(false);
        deletionTime.setVisible(false);
        //While user has not selected an Interest Point, we hide edit or show button
        seeButton.setVisible(false);
        editPI.setVisible(false);

        //When user select an item, we change the selected Interest Point inside PIVIerwer
        //Then, when editing or showing it, we only pass PIViewer as Argument
        PIListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (PIListView.getSelectionModel().getSelectedIndex() != -1) {
                    piViewer.setSelectedInterestPoint(PIListView.getSelectionModel().getSelectedIndex());
                    if (newValue != null) {
                        seeButton.setVisible(true);
                        deletePI.setVisible(true);
                    }
                }
            }
        });
    }

    /*
     * Loads the Pis from database
     */
    public void setPiViewer(PIViewer piViewer) {
        this.piViewer = piViewer;

        // Everything is in a separated thread because it is a heavy task (calls to Databse...)
        // We do not want a frozen interface
        threadGetPIs = new Thread(getListOfPIs());
        threadGetPIs.setDaemon(true);
        threadGetPIs.start();
    }

    /*
     * Task where we get the Pis from database and print them on the listView
     * */
    private Task<Void> getListOfPIs() {
        Platform.runLater(() -> isLoading(true));

        //Get the PI and set them on the listView
        List<InterestPoint> interestPoints = piViewer.getlistOfInterestPoint();
        PIListView.getItems().clear();

        for (InterestPoint interestPoint : interestPoints) {
            PIListView.getItems().add(interestPoint.toStringMinimal());
        }

        Platform.runLater(() -> isLoading(false));
        return null;
    }

    @FXML
    private void load(ActionEvent event) {
        if (!PIListView.isExpanded()) {
            PIListView.setExpanded(true);
            PIListView.depthProperty().set(1);
        } else {
            PIListView.setExpanded(false);
            PIListView.depthProperty().set(0);
        }
    }

    @FXML
    void addPIPressed(ActionEvent event) {
        mainController.goToPICreateOrEditPane(true, piViewer);
    }

    @FXML
    void editPIPressed(ActionEvent event) {
        //Not new, then we add true in the parameters
        mainController.goToPICreateOrEditPane(false, piViewer);
    }
    
    /**
     * This function call the maincontroller who will delete the selected PI
     * and then refresh the page
     */
    @FXML
    void deletePIPressed(ActionEvent event) {
    	mainController.goToPIDelete(piViewer);
    	mainController.goToMyPisPane();
    }

    @FXML
    void seeButtonPressed(ActionEvent event) {
        mainController.goToSelectedPi(piViewer);
    }

    /**
     * This function gets
     * index of selected interest Point inside PIViewer
     */
    private void setIndexOfSelectedPi() {
        if (PIListView.getSelectionModel().getSelectedIndex() != 0) {
            piViewer.setSelectedInterestPoint(PIListView.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * Calls the viewer to get IP from database
     *
     * @return List of user IPs
     * @throws IOException
     * @throws InterruptedException
     */
    public List<InterestPoint> initializeListOfInterestPoints() throws IOException, InterruptedException {
        return piViewer.getlistOfInterestPoint();
    }

    /**
     *
     */
    private void isLoading(boolean isLoading) {
        if (isLoading){
            progressIndicatorLabel.setText("Récupération des sauvegardes, veuillez patienter...");
            progressIndicator.setVisible(true);
            progressIndicatorLabel.setVisible(true);
        }
        else{
            progressIndicator.setVisible(false);
            progressIndicatorLabel.setVisible(false);
        }
    }

    /**
     * Calls the viewer to get PIs
     */
    public void refreshPIs() {
        if (threadGetPIs != null) {
            threadGetPIs.interrupt();
        }
        threadGetPIs = new Thread(getListOfPIs());
        threadGetPIs.setDaemon(true);
        threadGetPIs.start();
    }

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        if (threadGetPIs != null) {
            threadGetPIs.interrupt();
        }
    }
    // prepared a spinner for the loading time of the deletion
    private void deletionIsRunning(boolean deleting) {
        if (deleting) {
            deletionTime.setVisible(true);
            deletePI.setVisible(false);
        } else {
        	deletionTime.setVisible(false);
            deletePI.setVisible(true);
        }

    }
    

}
