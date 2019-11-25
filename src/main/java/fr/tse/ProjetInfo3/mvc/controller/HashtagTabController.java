package fr.tse.ProjetInfo3.mvc.controller;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;

import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.SearchViewer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.Icon;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

//import java.awt.Label;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class HashtagTabController {
    private MainController mainController;

    private HastagViewer hastagViewer;
    Map<String, Integer> hashtagUsed;

    @FXML
    private Label hashtag;
    
    @FXML
    private Icon expandTweetsIcon;

    @FXML
    private Icon expandHashtagIcon;

    @FXML
    private JFXListView topFiveList;
    @FXML
    private JFXSpinner progressIndicator;
    @FXML
    private Label nbTweetLabel;
    @FXML
    private Label nbUserLabel;
    @FXML
    private JFXListView topTenLinkedList;
    @FXML
    private TitledPane titledHashtag;
    @FXML
    private GridPane gridPane;

    private String ourHashtag;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setHastagViewer(HastagViewer hastagViewer) throws Exception {
        this.hastagViewer = hastagViewer;
        ourHashtag = hastagViewer.getHashtag();

      Platform.runLater(() -> {
            hashtag.setText("#" + ourHashtag);
           nbUserLabel.setText(hastagViewer.getNumberOfUniqueAccounts(ourHashtag).toString());
           nbTweetLabel.setText(hastagViewer.getNumberOfTweets(ourHashtag).toString());
            List<String> hashtags=hastagViewer.getHashtagsLinked(ourHashtag);


        });
      Thread thread = new Thread(setTopHashtags());
      thread.setDaemon(true);
      thread.start();
    }
    private Task<Void> setTopHashtags() {
        Platform.runLater(() -> {
           // progressIndicator.setVisible(true);
        });
        List<String> hashtags=hastagViewer.getHashtagsLinked(ourHashtag);
        hashtagUsed=hastagViewer.topHashtag(hashtags);
        
        ObservableList<Label> hashtagsToPrint = FXCollections.observableArrayList();
        int i = 0;
        for (String hashtag : hashtagUsed.keySet()) {
            hashtagsToPrint.add(new Label(hashtag + "\t\t\t" + hashtagUsed.get(hashtag)));
            i++;
            if (i == 5) {
                break;
            }
        }
        Platform.runLater(() -> {
            topTenLinkedList.getItems().addAll(hashtagsToPrint);
            titledHashtag.setMaxHeight(50*hashtagsToPrint.size());
           // progressIndicator.setVisible(false);
        });
        return null;
    }
}
