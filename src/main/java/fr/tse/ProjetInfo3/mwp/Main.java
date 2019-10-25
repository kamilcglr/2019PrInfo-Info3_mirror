package fr.tse.ProjetInfo3.mwp;

import fr.tse.ProjetInfo3.mwp.services.Twitter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private Parent rootNode;

    /**
     * Constructor
     */
    public Main() {

    }

    public static void main(String[] args) {
        try {
            String consumer = "PahWHDFSZ02bTaqFUVamZ0iBI";
            String consumerSecret = "mGDqU2cwWrw85cMvj7YOBSczI8qZQM0IKKymdbRL82sXqtyhhr";
            String accessToken = "4664421557-y8N6WL3BVrhBTIfuzZcHqRmNZeGDkt0TbAFoz9g";
            String Accesstokensecret = "riGJEs4QhZWjOgQyQJY4jQJM8nCRnsfHisU1Vnq1VpDiv";
            Twitter.run(consumer, consumerSecret, accessToken, Accesstokensecret);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
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
