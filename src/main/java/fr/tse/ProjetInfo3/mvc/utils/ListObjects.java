package fr.tse.ProjetInfo3.mvc.utils;

import com.jfoenix.controls.JFXButton;

import fr.tse.ProjetInfo3.mvc.controller.LoginController;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.stream.Collectors;

public class ListObjects {
    private static final Paint GREEN = Paint.valueOf("#48AC98FF");
    private static final Paint RED = Paint.valueOf("#CB7C7AFF");

    private static Integer currentPiId;

    /**
     * This class will represent a result of a linked hashtag
     */
    public static class SimpleTopHashtagCell extends ListCell<ResultHashtag> {
        HBox hBox = new HBox();
        Label classementLabel = new Label("");
        Label hashtagLabel = new Label("");
        Label nbTweetLabel = new Label("");

        public SimpleTopHashtagCell() {
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
        public final String hashtagName;
        private final String nbTweets;

        public ResultHashtag(String classementIndex, String hashtagName, String nbTweets) {
            this.classementIndex = classementIndex;
            this.hashtagName = hashtagName;
            this.nbTweets = nbTweets;
        }

        public String getClassementIndex() {
            return classementIndex;
        }

        public String getHashtagName() {
            return hashtagName;
        }

        public String getNbTweets() {
            return nbTweets;
        }
    }

    /**
     * This class is used in Linked Hashtag of PI,
     * Contains Plus Button to add the hashtag to PI
     */
    public static class TopHashtagCellWithPlus extends ListCell<ResultHashtag> {
        GridPane cellGridPane;
        ColumnConstraints column1;
        ColumnConstraints column2;
        ColumnConstraints column3;
        ColumnConstraints column4;

        Label classementLabel;
        Label hashtagLabel;
        Label nbTweetLabel;

        JFXButton addDeleteHashtag;
        FontIcon addDeleteIcon;

        InterestPoint interestPoint;

        public TopHashtagCellWithPlus(InterestPoint interestPointParam, PIViewer piViewer) {
            super();

            interestPoint = interestPointParam;

            if (currentPiId == null) {
            	currentPiId = interestPoint.getId();
            }
            

            System.out.println(currentPiId);
            
            cellGridPane = new GridPane();
            cellGridPane.getStyleClass().add("userCellGridPane");
            cellGridPane.setPrefSize(700, 20);
            column1 = new ColumnConstraints();
            column1.setPrefWidth(50);
            column2 = new ColumnConstraints();
            column2.setPrefWidth(250);
            column3 = new ColumnConstraints();
            column3.setPrefWidth(400);
            column4 = new ColumnConstraints();
            column4.setPrefWidth(50);

            cellGridPane.getColumnConstraints().addAll(column1, column2, column3, column4);

            classementLabel = new Label("");
            classementLabel.getStyleClass().add("indexLabel");

            hashtagLabel = new Label("");
            hashtagLabel.getStyleClass().add("hashtagTextLabel");

            nbTweetLabel = new Label("");
            nbTweetLabel.getStyleClass().add("nbTweetLabel");

            addDeleteHashtag = new JFXButton();

            addDeleteHashtag.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (interestPoint.containsHashtag(getItem().getHashtagName())) {
                        ResultHashtag currentHashtag = getItem();

                        piViewer.deleteInterestPointFromDatabaseById(currentPiId,LoginController.id);

                        //Hashtag hashtagToRemove = interestPoint.getHashtagFromName(currentHashtag.getHashtagName());
                        //
                        //interestPoint.getHashtags().remove(hashtagToRemove);
                        List<Hashtag> newListOfHashtags = interestPoint.getHashtags()
                                .stream().filter(hashtag -> !hashtag.getHashtag()
                                        .equals(currentHashtag.getHashtagName()))
                                        .collect(Collectors.toList());
                        interestPoint.setHashtags(newListOfHashtags);

                        System.out.println(interestPoint.getHashtags());
                        currentPiId = (int) piViewer.addInterestPointToDatabase(interestPoint,LoginController.id);


                        addDeleteIcon = new FontIcon("fas-plus");
                        addDeleteIcon.setIconColor(GREEN);
                        addDeleteHashtag.setGraphic(addDeleteIcon);
                    } else {
                        ResultHashtag currentHashtag = getItem();

                        piViewer.deleteInterestPointFromDatabaseById(currentPiId,LoginController.id);

                        Hashtag hashtagToAdd = new Hashtag(currentHashtag.getHashtagName());
                        interestPoint.addToInterestPoint(hashtagToAdd);
                        currentPiId = (int) piViewer.addInterestPointToDatabase(interestPoint,LoginController.id);

                        addDeleteIcon = new FontIcon("fas-minus");
                        addDeleteIcon.setIconColor(RED);
                        addDeleteHashtag.setGraphic(addDeleteIcon);

                    }
                }
            });

            cellGridPane.add(classementLabel, 0, 0);
            cellGridPane.add(hashtagLabel, 1, 0);
            cellGridPane.add(nbTweetLabel, 2, 0);
            cellGridPane.add(addDeleteHashtag, 3, 0);
        }

        public void updateItem(ResultHashtag resultHashtag, boolean empty) {
            super.updateItem(resultHashtag, empty);

            if (resultHashtag != null && !empty) {

                if (interestPoint.containsHashtag(getItem().getHashtagName())) {
                    addDeleteIcon = new FontIcon("fas-minus");
                    addDeleteIcon.setIconColor(RED);
                } else {
                    addDeleteIcon = new FontIcon("fas-plus");
                    addDeleteIcon.setIconColor(GREEN);
                }
                addDeleteIcon.setIconSize(14);

                addDeleteHashtag.setGraphic(addDeleteIcon);
                addDeleteHashtag.setPrefSize(50, 50);

                addDeleteHashtag.setGraphic(addDeleteIcon);

                classementLabel.setText(resultHashtag.getClassementIndex());
                hashtagLabel.setText("#" + resultHashtag.getHashtagName());
                nbTweetLabel.setText(resultHashtag.getNbTweets() + " tweets");
                setGraphic(cellGridPane);
            }
        }
    }

    /*
     * Used in topUser inside PITab
     */
    public static class TopUserCellWithPlus extends ListCell<User> {
        GridPane cellGridPane;
        ColumnConstraints column1;
        ColumnConstraints column2;
        ColumnConstraints column3;
        ColumnConstraints column4;

        ImageView profileImageView;
        Label nameLabel;
        Label followersCountLabel;

        Image profilePicture;

        JFXButton addDeleteUser;
        FontIcon addDeleteIcon;

        InterestPoint interestPoint;
        User userObject;

        public TopUserCellWithPlus(InterestPoint interestPointParam, PIViewer piViewer) {
            super();
            interestPoint = interestPointParam;

            if (currentPiId == null) {
            	currentPiId = interestPoint.getId();
            }

            cellGridPane = new GridPane();
            cellGridPane.getStyleClass().add("userCellGridPane");
            cellGridPane.setPrefSize(700, 50);
            column1 = new ColumnConstraints();
            column1.setPrefWidth(50);
            column2 = new ColumnConstraints();
            column2.setPrefWidth(250);
            column3 = new ColumnConstraints();
            column3.setPrefWidth(400);
            column4 = new ColumnConstraints();
            column4.setPrefWidth(50);

            cellGridPane.getColumnConstraints().addAll(column1, column2, column3, column4);

            profileImageView = new ImageView();

            nameLabel = new Label();
            followersCountLabel = new Label();

            addDeleteUser = new JFXButton();

            addDeleteUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (interestPoint.containsUser(getItem())) {
                        User currentUser = getItem();

                        piViewer.deleteInterestPointFromDatabaseById(currentPiId,LoginController.id);

                        interestPoint.getUsers().remove(currentUser);
                        currentPiId = (int) piViewer.addInterestPointToDatabase(interestPoint,LoginController.id);

                        addDeleteIcon = new FontIcon("fas-plus");
                        addDeleteIcon.setIconColor(GREEN);
                        addDeleteUser.setGraphic(addDeleteIcon);
                    } else {
                        User currentUser = getItem();

                        piViewer.deleteInterestPointFromDatabaseById(currentPiId,LoginController.id);

                        interestPoint.addToInterestPoint(currentUser);
                        currentPiId = (int) piViewer.addInterestPointToDatabase(interestPoint,LoginController.id);

                        addDeleteIcon = new FontIcon("fas-minus");
                        addDeleteIcon.setIconColor(RED);
                        addDeleteUser.setGraphic(addDeleteIcon);

                    }
                }
            });

            cellGridPane.add(profileImageView, 0, 0);
            cellGridPane.add(nameLabel, 1, 0);
            cellGridPane.add(followersCountLabel, 2, 0);
            cellGridPane.add(addDeleteUser, 3, 0);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            userObject = getItem();
            if (empty || user == null) {
                setText(null);
                setGraphic(null);

            } else {
                profilePicture = new Image(user.getProfile_image_url_https(), 40, 40, false, false);
                profileImageView.setImage(profilePicture);

                if (interestPoint.containsUser(getItem())) {
                    addDeleteIcon = new FontIcon("fas-minus");
                    addDeleteIcon.setIconColor(RED);
                } else {
                    addDeleteIcon = new FontIcon("fas-plus");
                    addDeleteIcon.setIconColor(GREEN);
                }

                addDeleteIcon.setIconSize(18);
                addDeleteUser.setGraphic(addDeleteIcon);
                addDeleteUser.setPrefSize(50, 50);

                Circle clip = new Circle(20, 20, 20);
                profileImageView.setClip(clip);

                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);

                WritableImage image = profileImageView.snapshot(parameters, null);

                profileImageView.setClip(null);
                profileImageView.setImage(image);
                profileImageView.getStyleClass().add("profileImageView");

                nameLabel.setText(user.getName());
                nameLabel.getStyleClass().add("nameLabel");

                followersCountLabel.setText("Followers : " + NumberParser.spaceBetweenNumbers(user.getFollowers_count()));
                followersCountLabel.getStyleClass().add("followersCountLabel");

                setText(null);
                setGraphic(cellGridPane);
            }
        }
    }

    /*
     * Used in topUser inside PITab
     */
    public static class SearchUser extends ListCell<User> {
        HBox hBox = new HBox();

        ImageView profileImageView;
        Label nameLabel;
        Label screenName;

        Image profilePicture;

        public SearchUser() {
            super();
            profileImageView = new ImageView();
            nameLabel = new Label();
            screenName = new Label();
            hBox.getChildren().addAll(profileImageView, nameLabel, screenName);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);

            } else {
                if (profilePicture == null) {
                    profilePicture = new Image(user.getProfile_image_url_https(), 40, 40, false, false);
                }
                profileImageView.setImage(profilePicture);

                Circle clip = new Circle(20, 20, 20);
                profileImageView.setClip(clip);

                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);

                WritableImage image = profileImageView.snapshot(parameters, null);

                profileImageView.setClip(null);
                profileImageView.setImage(image);
                profileImageView.getStyleClass().add("profileImageView");


                hBox.getStyleClass().add("hbox");
                nameLabel.setText(user.getName());
                nameLabel.getStyleClass().add("nameLabel");

                screenName.setText("@" + user.getScreen_name());
                screenName.getStyleClass().add("screenNameLabel");

                setText(null);
                setGraphic(hBox);
            }
        }
    }

    /*
     * Used in myPisList
     */
    public static class ResultInterestPoint extends ListCell<InterestPoint> {
        HBox hBox = new HBox();

        Label name;
        Label users;
        Label hashtags;

        public ResultInterestPoint() {
            super();

            name = new Label();
            users = new Label();
            hashtags = new Label();
            hBox.getChildren().addAll(name, users, hashtags);
        }

        @Override
        protected void updateItem(InterestPoint interestPoint, boolean empty) {
            super.updateItem(interestPoint, empty);

            if (empty || interestPoint == null) {
                setText(null);
                setGraphic(null);

            } else {

                hBox.getStyleClass().add("hbox");
                hBox.setPrefWidth(100);

                String userNamesInPI = interestPoint.getUsers().stream()
                        .map(user -> "@" + user.getName()).collect(Collectors.joining(" "));
                String hashtagNamesInPI = interestPoint.getHashtags().stream()
                        .map(hashtag -> "#" + hashtag.getHashtag()).collect(Collectors.joining(" "));


                name.setText(interestPoint.getName());
                name.setMinWidth(100);
                name.setMaxWidth(100);
                name.setWrapText(true);
                name.getStyleClass().add("nameOfPI");

                users.setText(userNamesInPI);
                users.setMaxWidth(300);
                users.setWrapText(true);
                hashtags.setText(hashtagNamesInPI);
                hashtags.setMaxWidth(300);
                hashtags.setWrapText(true);

                users.getStyleClass().add("usersAndHashtagsLabel");
                hashtags.getStyleClass().add("usersAndHashtagsLabel");

                setText(null);
                setGraphic(hBox);
            }
        }
    }

    /*
     * Simple hashtag with title only
     * */
    public static class SimpleHashtag extends ListCell<Hashtag> {
        HBox hBox = new HBox();

        Label name;

        public SimpleHashtag() {
            super();

            name = new Label();
            hBox.getChildren().addAll(name);
        }

        @Override
        protected void updateItem(Hashtag hashtag, boolean empty) {
            super.updateItem(hashtag, empty);

            if (empty || hashtag == null) {
                setText(null);
                setGraphic(null);

            } else {
                hBox.getStyleClass().add("hbox");

                name.setText("#" + hashtag.getHashtag());
                name.getStyleClass().add("hashtagNameLabel");

                setText(null);
                setGraphic(hBox);
            }
        }
    }

    /*
     * Used in topUser inside PITab for users in PI
     * Without + button
     */
    public static class SimpleUserCell extends ListCell<User> {
        HBox hBox = new HBox();

        ImageView profileImageView;
        Label nameLabel;
        Label followersCountLabel;

        Image profilePicture;

        public SimpleUserCell() {
            super();

            profileImageView = new ImageView();

            nameLabel = new Label();
            followersCountLabel = new Label();

            hBox.getChildren().addAll(profileImageView, nameLabel, followersCountLabel);
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
                profileImageView.getStyleClass().add("profileImageView");


                nameLabel.setText(user.getName());
                nameLabel.getStyleClass().add("nameLabel");

                followersCountLabel.setText("Followers : " + NumberParser.spaceBetweenNumbers(user.getFollowers_count()));
                followersCountLabel.getStyleClass().add("followersCountLabel");

                setText(null);
                setGraphic(hBox);
            }
        }
    }


}
