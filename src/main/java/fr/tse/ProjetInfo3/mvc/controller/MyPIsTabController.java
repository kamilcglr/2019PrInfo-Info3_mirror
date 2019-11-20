package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private JFXButton seeButton;

    private InterestPoint ip;

    private PIViewer piViewer;

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

        //While user has not selected an Interest Point, we hide edit or show button
        seeButton.setVisible(false);
        editPI.setVisible(false);

        //When user select an item, we change the selected Interest Point inside PIVIerwer
        //Then, when editing or showing it, we only pass PIViewer as Argument
        listPI.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (listPI.getSelectionModel().getSelectedIndex() != -1) {
                    piViewer.setSelectedInterestPoint(listPI.getSelectionModel().getSelectedIndex());
                    if (newValue != null) {
                        seeButton.setVisible(true);
                        editPI.setVisible(true);
                    }
                }
            }
        });
    }

    public void setPiViewer(PIViewer piViewer) {
        this.piViewer = piViewer;
        // Everything is in a separated thread because it is a heavy task (calls to Databse...)
        // We do not want a frozen interface
        Platform.runLater(() -> isLoading(true));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<InterestPoint> interestPoints = piViewer.getlistOfInterestPoint();
                //sets the datas
                for (InterestPoint interestPoint : interestPoints) {
                    listPI.getItems().add(interestPoint.toStringMinimal());
                }
                Platform.runLater(() -> isLoading(false));
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
        mainController.goToPICreateOrEditPane(true, piViewer);
    }

    @FXML
    void editPIPressed(ActionEvent event) {
        //Not new, then we add true in the parameters
        mainController.goToPICreateOrEditPane(false, piViewer);
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
        if (listPI.getSelectionModel().getSelectedIndex() != 0) {
            piViewer.setSelectedInterestPoint(listPI.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * calls the viewer to get IP from database
     *
     * @return List of user IPs
     * @throws IOException
     * @throws InterruptedException
     */
    public List<InterestPoint> initializeListOfInterestPoints() throws IOException, InterruptedException {
        return piViewer.getlistOfInterestPoint();
    }

    /**
     * TODO add spinner or progressBar during loading
     */
    private void isLoading(boolean isLoading) {

    }

    public void refreshPIs() {
        List<InterestPoint> interestPoints = piViewer.getlistOfInterestPoint();
        //sets the datas
        listPI.getItems().clear();
        for (InterestPoint interestPoint : interestPoints) {
            listPI.getItems().add(interestPoint.toStringMinimal());
        }
    }

}
