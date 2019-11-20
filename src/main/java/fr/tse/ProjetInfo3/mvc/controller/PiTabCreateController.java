package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * @author Sergiy
 */
public class PiTabCreateController {
    private MainController mainController;

    /* Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void injectTabContainer(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void injectTab(Tab tab) {
        this.tab = tab;
    }

    Date date;
    TabPane tabPane;
    Tab tab;

    boolean isNew; //if true, it is teh creation of a PI, else false (edition of existing PI)

    private InterestPoint interestPoint;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton addHashtagJFXButton;

    @FXML
    private JFXButton addUserJFXButton;

    @FXML
    private JFXButton discardJFXButton;

    @FXML
    private JFXButton saveJFXButton;

    @FXML
    private JFXTextField nameJFXTextField;

    @FXML
    private JFXTextArea descriptionJFXTextArea;

    @FXML
    private JFXTextField creationDateJFXTextField;

    @FXML
    private void initialize() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = new Date();

        creationDateJFXTextField.setEditable(false);
        creationDateJFXTextField.setText("Créé le " + simpleDateFormat.format(date));
    }

    /**
     * This function has to be called just after initialisation of this controller
     */
    public void setPI(InterestPoint interestPoint) {
        this.isNew = isNew;
        this.interestPoint = interestPoint;
    }


    /**
     * Events
     **/
    @FXML
    public void discardJFXButtonPressed(ActionEvent event) {
        BoxBlur blur = new BoxBlur(2, 2, 2);

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(new Text("La création du point d'intêret a été annulée"));

        JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        anchorPane.setEffect(blur);
        dialog.show();
        dialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                tabPane.getTabs().remove(tab);
            }
        });
    }

    @FXML
    public void saveJFXButtonPressed(ActionEvent event) {
        interestPoint = new InterestPoint(nameJFXTextField.getText(), descriptionJFXTextArea.getText(),
                date);
        PIViewer piViewer = new PIViewer();
        piViewer.addInterestPointToDatabase(interestPoint);
        BoxBlur blur = new BoxBlur(2, 2, 2);
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(new Text("Votre point d'intêret a été enregistré"));

        JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        anchorPane.setEffect(blur);
        dialog.show();
        dialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                tabPane.getTabs().remove(tab);
            }
        });
    }

    @FXML
    public void addHashtagJFXButtonPressed(ActionEvent event) {

    }

    @FXML
    public void addUserJFXButtonPressed(ActionEvent event) {

    }
}
