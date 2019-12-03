package fr.tse.ProjetInfo3.mvc.utils;

import com.jfoenix.controls.JFXButton;
import fr.tse.ProjetInfo3.mvc.dto.User;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ListObjects {
    /**
     * This class will represent a result of a linked hashtag
     */
    public static class HashtagCell extends ListCell<ResultHashtag> {
        HBox hBox = new HBox();
        Label classementLabel = new Label("");
        Label hashtagLabel = new Label("");
        Label nbTweetLabel = new Label("");

        public HashtagCell() {
            super();
            classementLabel.getStyleClass().add("indexLabel");
            hashtagLabel.getStyleClass().add("hashtagTextLabel");
            nbTweetLabel.getStyleClass().add("nbTweetLabel");
            hBox.getChildren().addAll(classementLabel, hashtagLabel, nbTweetLabel);
        }

        public void updateItem(ResultHashtag resultHashtag, boolean empty) {
            super.updateItem(resultHashtag, empty);

            if (resultHashtag != null && !empty) {
                classementLabel.setText(resultHashtag.getClassementIndex());
                hashtagLabel.setText(resultHashtag.getHashtagName());
                nbTweetLabel.setText(resultHashtag.getNbTweets() + " tweets");
                setGraphic(hBox);
            }
        }
    }

    /**
     * This class will represent a result of a linked hashtag
     */
    public static class ResultHashtag {
        private final String classementIndex;
        private final String hashtagName;
        private final String nbTweets;

        public ResultHashtag(String classementIndex, String hashtagName, String nbTweets) {
            this.classementIndex = classementIndex;
            this.hashtagName = hashtagName;
            this.nbTweets = nbTweets;
        }

        String getClassementIndex() {
            return classementIndex;
        }

        String getHashtagName() {
            return hashtagName;
        }

        String getNbTweets() {
            return nbTweets;
        }
    }

    /*
     * Used in topUser inside PITab
     */
    public static class TopUserCell extends ListCell<User> {
        //GridPane cellGridPane;
        //ColumnConstraints column1;
        //ColumnConstraints column2;
        //ColumnConstraints column3;
        HBox hBox = new HBox();

        ImageView profileImageView;
        Label screenNameLabel;
        Label followersCountLabel;

        Image profilePicture;

        public TopUserCell() {
            super();

            //cellGridPane = new GridPane();
            //cellGridPane.getStyleClass().add("cellGridPane");
            //cellGridPane.setPrefSize(550, 50);
            //column1 = new ColumnConstraints();
            //column1.setPrefWidth(50);
            //column2 = new ColumnConstraints();
            //column2.setPrefWidth(150);
            //column3 = new ColumnConstraints();
            //column3.setPrefWidth(300);
            //cellGridPane.getColumnConstraints().addAll(column1, column2, column3);

            profileImageView = new ImageView();

            screenNameLabel = new Label();
            followersCountLabel = new Label();

            //cellGridPane.add(profileImageView, 0, 0);
            //cellGridPane.add(screenNameLabel, 1, 0);
            //cellGridPane.add(followersCountLabel, 2, 0);

            hBox.getChildren().addAll(profileImageView, screenNameLabel, followersCountLabel);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);

            } else {
                profilePicture = new Image(user.getProfile_image_url_https(), 40, 40, false, false);
                profileImageView.setImage(profilePicture);

                Circle clip = new Circle(20, 20, 20);
                profileImageView.setClip(clip);

                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);

                WritableImage image = profileImageView.snapshot(parameters, null);

                profileImageView.setClip(null);
                profileImageView.setImage(image);


                screenNameLabel.setText(user.getScreen_name());
                followersCountLabel.setText("Folowers: " + Long.toString(user.getFollowers_count()));

                setText(null);
                setGraphic(hBox);
            }
        }
    }
}
