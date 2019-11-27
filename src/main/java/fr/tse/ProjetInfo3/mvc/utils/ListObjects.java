package fr.tse.ProjetInfo3.mvc.utils;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class ListObjects {
    /**
     * This class will represent a result of a linked hashtag
     */
    public static class Cell extends ListCell<ResultHashtag> {
        HBox hBox = new HBox();
        Label classementLabel = new Label("");
        Label hashtagLabel = new Label("");
        Label nbTweetLabel = new Label("");

        public Cell() {
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
}
