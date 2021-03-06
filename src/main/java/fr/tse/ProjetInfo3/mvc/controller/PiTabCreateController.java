package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import fr.tse.ProjetInfo3.mvc.viewer.SearchViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Sergiy
 * {@code This class acts as a Controller for the Tab which is used to create or edit Interest Points}
 */
public class PiTabCreateController {
    private MainController mainController;
    private PIViewer piViewer;
    private InterestPoint interestPointToEdit;

    /**
     * Injections
     **/
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void injectTabContainer(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void injectTab(Tab tab) {
        this.tab = tab;
    }

    public void setPiViewer(PIViewer piViewer) {
        this.piViewer = piViewer;
    }

    public void setPiToEdit(InterestPoint interestPoint) {
        this.interestPointToEdit = interestPoint;
    }

    /**
     * Time
     **/

    private Date date;

    /**
     * Tabs
     **/

    private TabPane tabPane;
    private Tab tab;

    /**
     * Lists
     **/

    private ObservableList<String> observableListHashtag;
    private ObservableList<User> observableListUser;

    private List<User> resultUsers;

    /**
     * Other
     **/

    boolean isNew; // if true, the IP is being created, else false (edition of an existing IP)
    boolean userSelected;
    boolean suppressionDone;

    private InterestPoint interestPoint;

    /**
     * PiTabCreate.fxml FXML element
     **/
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton addHashtagJFXButton;

    @FXML
    private JFXButton addUserJFXButton;

    @FXML
    private JFXButton discardJFXButton;

    @FXML
    private JFXButton saveJFXButton;

    @FXML
    private JFXTextField nameJFXTextField;

    @FXML
    private JFXTextArea descriptionJFXTextArea;

    @FXML
    private Label creationDateLabel;

    @FXML
    private JFXTextField hashtagField;

    @FXML
    private JFXTextField userField;

    @FXML
    private GridPane suivisGrid;

    @FXML
    private GridPane suivisGrid2;

    @FXML
    private JFXListView<User> propositionList;


    /**
     * JFXListView elements
     **/
    @FXML
    private JFXListView<String> hashtagListView;
    private List<Hashtag> hashtagList = new ArrayList<>();

    @FXML
    private JFXListView<User> userListView;
    private List<User> userList = new ArrayList<>();

    /**
     * Proposition Box FXML elements
     **/
    @FXML
    private VBox propositionVBox;

    private Map<String, String> usersNamesAndScreenNames;

    @FXML
    private JFXProgressBar propositionProgressBar;

    /**
     * Initialization method
     **/
    @FXML
    private void initialize() {
        JFXScrollPane.smoothScrolling(scrollPane);
        propositionProgressBar.setVisible(false);
        propositionProgressBar.setProgress(-1);

        propositionList.setCellFactory(param -> new ListObjects.SearchUser());

        userSelected = false;
        suppressionDone = true;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = new Date();

        hashtagListView.setFocusTraversable(false);
        userListView.setFocusTraversable(false);

        suivisGrid.setVisible(true);
        suivisGrid2.setVisible(true);

        creationDateLabel.setText("Créé le " + simpleDateFormat.format(date));

        hashtagField.setText("#");

        /*
         * Proposition mechanism initialization - Taken from SearchTabController.java
         */
        hashtagField.textProperty().addListener((observable, old_value, new_value) -> {
            if (hashtagField.getText().isEmpty() || !hashtagField.getText(0, 1).equals("#")) {
                hashtagField.setText("#" + new_value);
            }
        });

        propositionList.setVisible(false);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        userField.textProperty().addListener((observable, old_value, new_value) -> {
            propositionList.setVisible(false);

            // Not userSelected because we don't do search when user has chosen a user
            if (new_value.length() > 2 && !userSelected) {
                pause.setOnFinished(event -> {
                    propositionProgressBar.setVisible(true);
                    showPropositionList(new_value);
                });
                pause.playFromStart();
            }
        });

        observableListHashtag = FXCollections.observableArrayList();
        observableListUser = FXCollections.observableArrayList();

        hashtagListView.setItems(observableListHashtag);
        hashtagListView.setCellFactory(hastagListView -> new HashtagCell());

        userListView.setItems(observableListUser);
        userListView.setCellFactory(userListView -> new UserCell());
    }

    /**
     * Events
     **/
    @FXML
    public void discardJFXButtonPressed(ActionEvent event) {
        launchInfoDialog("Annulation", "La création du point d'intérêt a été annulée", "D'accord", false, true);
    }

    @FXML
    public void saveJFXButtonPressed(ActionEvent event) {
        if (!observableListHashtag.isEmpty() || !observableListUser.isEmpty()) {
            if (!isNew) {
                piViewer.deleteInterestPointFromDatabaseById(interestPointToEdit.getId());
            }

            interestPoint = new InterestPoint(nameJFXTextField.getText(), descriptionJFXTextArea.getText(), date,
                    hashtagList, userList);

            interestPoint.setUserID(mainController.getUserApp().getId());
            piViewer.addInterestPointToDatabase(interestPoint);
            launchInfoDialog("Enregistrement réussi", "Votre point d'intérêt a été enregistré", "D'accord", true, true);
        } else {
            launchInfoDialog("Point d'Interêt incomplet",
                    "Votre Point d'Interêt doit contenir au moins un Hashtag ou Utilisateur", "D'accord", false, false);
        }
    }

    @FXML
    public void addHashtagJFXButtonPressed(ActionEvent event) {
        String hashtagInput = hashtagField.getText();
        hashtagInput = hashtagInput.replaceAll("\\s", "");

        // Remove thee # on the beginning before adding
        if (hashtagInput.charAt(0) == '#') {
            hashtagInput = hashtagInput.substring(1);
        }
        Hashtag hashtag = new Hashtag(hashtagInput);
        hashtagList.add(hashtag);
        hashtagListView.getItems().add(hashtagInput);
        hashtagField.setText("#");
    }

    @FXML
    public void addUserJFXButtonPressed(ActionEvent event) {
        String query = userField.getText();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    String newResearch = query;

                    if (newResearch != null) {
                        UserViewer userViewer = new UserViewer();
                        userViewer.searchScreenName(newResearch);
                        observableListUser.add(userViewer.getUser());
                        userList.add(userViewer.getUser());

                        propositionVBox.setVisible(false);
                        userSelected = false;
                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.getStyleClass().add("snackbar");
                        if (e instanceof RequestManager.RequestManagerException) {
                            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
                                    new JFXSnackbarLayout("Désolé, l'utilisateur " + query + " n'existe pas.",
                                            "D'accord", b -> snackbar.close())));
                        } else {
                            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(
                                    "Désolé, la recherche n'a pas aboutie", "D'accord", b -> snackbar.close())));
                        }
                    });
                    System.out.println("Something went wrong : ");
                    e.printStackTrace();
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Launch dialog to inform the user
     *
     * @param header      label of the header
     * @param text        content printed inside
     * @param labelButton label inside of button
     */
    private void launchInfoDialog(String header, String text, String labelButton, boolean success,
                                  boolean exitOnClose) {
        Label headerLabel = new Label(header);
        Text bodyText = new Text(text);
        JFXButton button = new JFXButton(labelButton);

        BoxBlur blur = new BoxBlur(3, 3, 3);
        anchorPane.setEffect(blur);

        button.getStyleClass().add("dialog-button");
        if (success) {
            headerLabel.getStyleClass().add("dialog-header");
        } else {
            headerLabel.getStyleClass().add("dialog-header-fail");
        }
        bodyText.getStyleClass().add("dialog-text");

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setPadding(new Insets(10));
        JFXDialog dialog = new JFXDialog(dialogStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
            dialog.close();
        });

        dialogLayout.setHeading(headerLabel);
        dialogLayout.setBody(bodyText);
        dialogLayout.setActions(button);
        dialog.show();
        dialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                anchorPane.setEffect(null);

                if (exitOnClose) {
                    tabPane.getTabs().remove(tab);
                    mainController.goToMyPisPane();
                }
            }
        });
    }

    /**
     * This function has to be called just after initialisation of this controller
     */
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;

        if (!isNew) {
            nameJFXTextField.setText(interestPointToEdit.getName());
            descriptionJFXTextArea.setText(interestPointToEdit.getDescription());
            creationDateLabel.setText("Créé le " + interestPointToEdit.getDateOfCreation());

            List<String> hashtagNames = interestPointToEdit.getHashtagNames();

            observableListHashtag = FXCollections.observableArrayList(hashtagNames);
            observableListUser = FXCollections.observableArrayList(interestPointToEdit.getUsers());

            hashtagListView.setItems(observableListHashtag);
            hashtagListView.setCellFactory(hastagListView -> new HashtagCell());

            userListView.setItems(observableListUser);
            userListView.setCellFactory(userListView -> new UserCell());

            observableListHashtag.forEach((hashtag) -> {
                hashtagList.add(new Hashtag(hashtag));
            });

            observableListUser.forEach((user) -> {
                userList.add(user);
            });
        }
    }

    /**
     * This method displays a list of propositions based on the newValue of the
     * searchField. Taken from SearchTabController.java
     *
     * @param newValue
     */
    private void showPropositionList(String newValue) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                SearchViewer searchViewer = new SearchViewer();
                resultUsers = searchViewer.getListPropositions(newValue);

                ObservableList<User> usersToPrint = FXCollections.observableArrayList();
                usersToPrint.addAll(resultUsers);

                Platform.runLater(() -> {
                    propositionList.getItems().clear();
                    propositionList.getItems().addAll(usersToPrint);
                    if (usersToPrint.size() > 0) {
                        propositionVBox.setVisible(true);
                        propositionList.setVisible(true);
                    }
                    propositionProgressBar.setVisible(false);
                });

                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * If user choose an entry in the table, we launch the search by firing an
     * event. But we have to take the screen_name fisrt from the list
     *
     * @param event
     */
    @FXML
    private void propositionListClicked(MouseEvent event) {
        User selectedResult = propositionList.getSelectionModel().getSelectedItem();
        userSelected = true; // keep userSelected before userField.setText
        userField.setText(selectedResult.getScreen_name());
        propositionVBox.setVisible(false);

    }

    /**
     * @author Sergiy
     * A Cell element used as an entity shown in the Hashtag JFXListView
     */
    public final class HashtagCell extends ListCell<String> {
        HBox hBox = new HBox();

        Label name;
        Label users;
        Label hashtags;

        Label hashtagIconLabel;
        Label hashtagLabel;
        JFXButton removeHashtagJFXButton;

        FontIcon hashtagIcon;
        FontIcon minusIcon;

        public HashtagCell() {
            super();

            hashtagIconLabel = new Label();

            hashtagIcon = new FontIcon("fas-hashtag");
            hashtagIcon.setIconSize(18);
            hashtagIconLabel.setGraphic(hashtagIcon);
            hashtagIconLabel.getStyleClass().add("labelElements");

            hashtagLabel = new Label();
            hashtagLabel.getStyleClass().add("labelElements");
            removeHashtagJFXButton = new JFXButton();

            minusIcon = new FontIcon("fas-minus");
            minusIcon.setIconSize(18);
            minusIcon.setIconColor(Paint.valueOf("#CB7C7AFF"));

            removeHashtagJFXButton.setGraphic(minusIcon);

            removeHashtagJFXButton.setPrefSize(50, 50);

            removeHashtagJFXButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String hashtagStringObject = getItem();
                    List<Hashtag> toRemove = hashtagList.stream()
                            .filter(hashtag -> hashtag.getHashtag().equals(hashtagStringObject))
                            .collect(Collectors.toList());
                    hashtagList.removeAll(toRemove);

                    observableListHashtag.remove(hashtagStringObject);
                }
            });
            Region filler = new Region();
            HBox.setHgrow(filler, Priority.ALWAYS);
            hBox.getChildren().addAll(hashtagIconLabel, hashtagLabel, filler, removeHashtagJFXButton);
        }

        @Override
        protected void updateItem(String hashtagName, boolean empty) {
            super.updateItem(hashtagName, empty);

            if (empty || hashtagName == null) {
                setText(null);
                setGraphic(null);

            } else {
                hashtagLabel.setText(hashtagName);

                setText(null);
                hBox.setAlignment(Pos.CENTER_LEFT);// Changed the alignment to center-left
                hBox.getStyleClass().add("hbox");
                hBox.setPrefWidth(100);
                setGraphic(hBox);
            }
        }
    }

    /**
     * @author Sergiy
     * A Cell element used as an entity shown in the Users JFXListView
     */
    public final class UserCell extends ListCell<User> {
        HBox hBox = new HBox();


        ImageView profileImageView;
        Label screenNameLabel;
        Label followersCountLabel;
        JFXButton removeUserJFXButton;

        Image profilePicture;
        FontIcon minusIcon;

        public UserCell() {
            super();

            profileImageView = new ImageView();
            profileImageView.getStyleClass().add("labelElements");

            screenNameLabel = new Label();
            screenNameLabel.getStyleClass().add("labelElements");

            followersCountLabel = new Label();
            followersCountLabel.setAlignment(Pos.CENTER_LEFT);
            followersCountLabel.getStyleClass().add("labelLightElements");

            removeUserJFXButton = new JFXButton();

            minusIcon = new FontIcon("fas-minus");
            minusIcon.setIconSize(18);
            minusIcon.setIconColor(Paint.valueOf("#CB7C7AFF"));
            removeUserJFXButton.setGraphic(minusIcon);

            removeUserJFXButton.setPrefSize(50, 50);

            removeUserJFXButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ProgressIndicator progressIndicator = new ProgressIndicator();
                    VBox progressIndicatorBox = new VBox(progressIndicator);
                    progressIndicatorBox.setAlignment(Pos.CENTER);
                    dialogStackPane.getChildren().add(progressIndicatorBox);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (suppressionDone) {
                                suppressionDone = false;

                                System.out.println("Action: " + getItem());
                                User userObject = getItem();

                                hBox.setVisible(false);
                                userList.remove(userObject);
                                observableListUser.remove(userObject);
                                userListView.setItems(observableListUser);
                                userListView.setCellFactory(userListView -> new UserCell());

                                dialogStackPane.getChildren().remove(progressIndicatorBox);
                                suppressionDone = true;
                            }
                        }

                    });

                }
            });
            Region filler = new Region();
            HBox.setHgrow(filler, Priority.ALWAYS);
            hBox.getChildren().addAll(profileImageView, screenNameLabel, followersCountLabel, filler, removeUserJFXButton);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);

            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
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
                        followersCountLabel.setText("Followers: " + user.getFollowers_count());

                        setText(null);
                        hBox.setAlignment(Pos.CENTER_LEFT);// Changed the alignment to center-left
                        hBox.getStyleClass().add("hbox");
                        setGraphic(hBox);
                    }
                });

            }
        }
    }
}
