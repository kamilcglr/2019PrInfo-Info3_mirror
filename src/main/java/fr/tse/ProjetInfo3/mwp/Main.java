package fr.tse.ProjetInfo3.mwp;

import java.io.IOException;

import fr.tse.ProjetInfo3.mwp.controller.MainController;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;
//import fr.tse.ProjetInfo3.mwp.services.TwitterAPP;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private Parent rootNode;

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

    public void init(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            rootNode = loader.load();

            /* Give the controller access to the main app.*/
            MainController mainController = loader.getController();
            //TODO verify utility : controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        //TODO create app icon
        // Set the application icon.
        // this.primaryStage.getIcons().add(new
        // Image("file:resources/images/address_book_32.png"));
        init(stage);
        stage.setScene(new Scene(rootNode));
        stage.setTitle("Main");
        stage.show();

    }
}
