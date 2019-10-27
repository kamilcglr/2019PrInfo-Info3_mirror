package fr.tse.ProjetInfo3.mwp;

import java.io.IOException;

import fr.tse.ProjetInfo3.mwp.controller.MainController;
import fr.tse.ProjetInfo3.mwp.services.RequestManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Main extends Application {
    private Parent rootNode;

    /**
     * Constructor
     */
    public Main() {
    }

    public static void main(String[] args) {
        Twitter twitter = buildTwitter();

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

        // Set the application icon.
        // this.primaryStage.getIcons().add(new
        // Image("file:resources/images/address_book_32.png"));
        init(stage);
        stage.setScene(new Scene(rootNode));
        stage.setTitle("Main");
        stage.show();

    }

    public static Twitter buildTwitter() {
        Twitter twitter;
        String consumer = "PahWHDFSZ02bTaqFUVamZ0iBI";
        String consumerSecret = "mGDqU2cwWrw85cMvj7YOBSczI8qZQM0IKKymdbRL82sXqtyhhr";
        String accessToken = "4664421557-y8N6WL3BVrhBTIfuzZcHqRmNZeGDkt0TbAFoz9g";
        String accessTokensecret = "riGJEs4QhZWjOgQyQJY4jQJM8nCRnsfHisU1Vnq1VpDiv";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumer).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokensecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();

        return twitter;
    }
}
