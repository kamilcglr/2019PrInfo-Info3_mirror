package fr.tse.ProjetInfo3.mwp.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.Icon;

/**
 * @author Kamil CAGLAR
 * Controller of the SearchTab, all user interactions are handled here
 *
 */
public class SearchTabController {
    private MainController mainController;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /*
    * We re-declare fxml variable here (fx:id="hashtagToggle"), whith the SAME name ot use them
    * */
    @FXML
    private JFXToggleNode hashtagToggle;

    @FXML
    private JFXToggleNode userToggle;

    @FXML
    private Icon hashtagIcon;

    @FXML
    private Icon userIcon;

    @FXML
    private JFXButton searchButton;

    /*This function is launched when this tab is launched */
    @FXML
    private void initialize() {
        userIcon.setIconColor(Paint.valueOf("#48ac98ff"));
        hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
    }

    /*Only one Toggle can be pressed, so we change the color of the second Toggle */
    @FXML
    private void hashtagTogglePressed(ActionEvent event) {
        userIcon.setIconColor(Paint.valueOf("#48ac98ff"));
        userToggle.setSelected(false);
        hashtagIcon.setIconColor(Paint.valueOf("#ffffff"));
    }

    @FXML
    private void userTogglePressed(ActionEvent event) {
        hashtagIcon.setIconColor(Paint.valueOf("#48ac98ff"));
        hashtagToggle.setSelected(false);
        userIcon.setIconColor(Paint.valueOf("#ffffff"));
    }
}
