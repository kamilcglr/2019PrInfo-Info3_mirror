package fr.tse.ProjetInfo3.mvc;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private RequestManager mainRequestManager;

    @FXML
    private AnchorPane root;

    /**
     * Constructor
     */
    public Main() {
        mainRequestManager = new RequestManager();
    }

    public static void main(String[] args) {
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
        stage.setTitle("Twitter Analytics");

        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(""));

        Scene scene = new Scene(decorator);
        handleWindowResize(stage, scene);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(getClass().getResource("/fxml/styles/main.css").toString());
        stage.setScene(scene);

        stage.show();
        // Set the application icon and window icon
        stage.getIcons().add(new Image(getClass().getResource("/fonts/hashtag.png").toExternalForm()));
        new Thread(() -> {

            try {
                SVGGlyphLoader.loadGlyphsFont(getClass().getResourceAsStream("/fonts/icon.svg"),
                    "icon.svg");
            } catch (IOException ioExc) {
                ioExc.printStackTrace();
            }
        }).start();

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
