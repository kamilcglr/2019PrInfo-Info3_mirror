package fr.tse.ProjetInfo3.mvc;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private Parent rootNode;

    @FXML
    private AnchorPane root;

    /**
     * Constructor
     */
    public Main() {
    }

    public static void main(String[] args) {
        //Twitter twitter = TwitterAPP.buildTwitter();
        /* Sergiy Tests - Commented to not use calls to API

        RequestManager rm = new RequestManager(twitter);
        rm.getTweets("#ASSE");

        try {
            Profile macron = rm.getProfile("EmmanuelMacron");
            System.out.println(macron.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
		*/
        launch();
    }


    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Twitter Anlatytics");
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(true);


        Scene scene = new Scene(decorator);
        handleWindowResize(stage, scene);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(getClass().getResource("/fxml/styles/main.css").toString());
        stage.setScene(scene);
        stage.show();

       /* //TODO create app icon
        // Set the application icon.
        new Thread(() -> {
            try {
                SVGGlyphLoader.loadGlyphsFont(MainDemo.class.getResourceAsStream("/fonts/icomoon.svg"),
                    "icomoon.svg");
            } catch (IOException ioExc) {
                ioExc.printStackTrace();
            }
        }).start();
        */
    }

    /**
     * Change the size of sub elements when the size of window is modified
     */
    private void handleWindowResize(Stage stage, Scene scene) {

        //This part is called when window is resized by user or by full screen mode
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            root.prefWidthProperty().bind(scene.widthProperty());
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            root.prefHeightProperty().bind(scene.heightProperty());
        });
    }
}
