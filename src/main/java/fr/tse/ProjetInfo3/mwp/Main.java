package fr.tse.ProjetInfo3.mwp;

import fr.tse.ProjetInfo3.mwp.services.TwitterAPP;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twitter4j.User;

import java.io.IOException;

public class Main extends Application {
    private Parent rootNode;

    /**
     * Constructor
     */
    public Main() {

    }

    public static void main(String[] args) {
        //Ceci est un test permettant d'essayer l'api twitter4j
        TwitterAPP twitter = new TwitterAPP();
        User test = twitter.getUserFromId("realdonaldtrump");
        System.out.println(test.getScreenName());
        System.out.println(test.getStatus());
        System.out.println(test.getId());
        System.out.println(test.getDescription());
    }

    public void init(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            rootNode = loader.load();

            //stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            //    controller.resizeTab();
            //});
            //stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            //    controller.resizeTab();
            //});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {

        // Set the application icon.
        //this.primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));
        init(stage);
        stage.setScene(new Scene(rootNode));
        stage.setTitle("Main");
        stage.show();

    }

}
