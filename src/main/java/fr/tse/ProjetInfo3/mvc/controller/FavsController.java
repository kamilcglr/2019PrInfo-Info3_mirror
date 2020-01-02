package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import fr.tse.ProjetInfo3.mvc.dto.Favourite;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.ResultHashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects.SimpleTopHashtagCell;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * @author Laïla
 */
public class FavsController {

    @FXML
    private JFXListView<ResultHashtag> favsListViewHashtag;
    @FXML
    private JFXListView<User> favsListViewUser;

    //Progress indicator
    @FXML
    private JFXSpinner progressIndicator;
    @FXML
    private Label progressLabel;

    @FXML
    private Label usersLabel;

    @FXML
    private Label hashtagsLabel;

    private MainController mainController;
    private FavsViewer favsViewer;

    @FXML
    private void initialize() throws Exception {
        favsListViewHashtag.setCellFactory(param -> new SimpleTopHashtagCell());
        favsListViewUser.setCellFactory(param -> new ListObjects.SimpleUserCell());
    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setFavsViewer() {

        Platform.runLater(() -> {
            isLoading(true);
            favsListViewHashtag.getItems().clear();
        });

        try {
            setTopHashtags();
            setUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            isLoading(false);
        });
    }

    public Favourite initializeListOfFavourites() throws Exception {
        return favsViewer.getlistOfFavourites();
    }

    private void setTopHashtags() throws Exception {
        favsViewer = new FavsViewer();
        Favourite favourite = favsViewer.getlistOfFavourites();

        ObservableList<ResultHashtag> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (Hashtag hashtag : favourite.getHashtags()) {
            hashtagsToPrint.add(new ResultHashtag(String.valueOf(i + 1), hashtag.getHashtag(), hashtag.getHashtag()));
            System.out.println(hashtag.getHashtag());
            i++;
        }
        Platform.runLater(() -> {
            favsListViewHashtag.getItems().addAll(hashtagsToPrint);
            //titledHashtag.setMaxHeight(50 * hashtagsToPrint.size());
        });

    }

    public void userClick(MouseEvent arg0) throws Exception {
        UserViewer userViewer = new UserViewer();
        String research = favsListViewUser.getSelectionModel().getSelectedItem().getScreen_name();
        if (favsListViewUser.getSelectionModel().getSelectedIndex() != -1) {
            userViewer.searchScreenName(research);
            mainController.goToUserPane(userViewer);
        }
    }

    public void hashtagClick(MouseEvent arg0) throws Exception {
        HastagViewer hashtagViewer = new HastagViewer();
        ResultHashtag research = favsListViewHashtag.getSelectionModel().getSelectedItem();
        if (favsListViewHashtag.getSelectionModel().getSelectedIndex() != -1) {
            hashtagViewer.setHashtag(research.getHashtagName());
            mainController.goToHashtagPane(hashtagViewer);
        }
    }

    private void setUsers() throws Exception {
        favsViewer = new FavsViewer();
        List<User> users = favsViewer.getlistOfFavourites().getUsers();
        ObservableList<User> usersToPrint = FXCollections.observableArrayList();
        for (User user : users) {
            usersToPrint.add(user);
        }
        Platform.runLater(() -> {
            favsListViewUser.getItems().addAll(usersToPrint);
            //topFiveUserList.setMaxHeight(50 * usersToPrint.size());
        });
    }

    private void isLoading(boolean isLoading) {
        if (isLoading) {
            favsListViewHashtag.setVisible(false);
            favsListViewUser.setVisible(false);
            progressLabel.setText("Récupération des favoris, veuillez patienter...");
            progressIndicator.setVisible(true);
            progressLabel.setVisible(true);
            hashtagsLabel.setVisible(false);
            usersLabel.setVisible(false);

        } else {
            favsListViewHashtag.setVisible(true);
            favsListViewUser.setVisible(true);
            progressIndicator.setVisible(false);
            progressLabel.setVisible(false);
            hashtagsLabel.setVisible(true);
            usersLabel.setVisible(true);
        }
    }

    /**
     * Called when tab is closed
     */
    public void killThreads() {
        //if (threadGetFavs != null) {
        //    threadGetFavs.interrupt();
        //}
    }
}