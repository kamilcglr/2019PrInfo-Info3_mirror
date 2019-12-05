package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.utils.ListObjects;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
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
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Sergiy
 */
public class PiTabCreateController {
    private MainController mainController;

    private PIViewer piViewer;

    /* Controller can acces to this Tab */
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

    /**
     * Controller variables
     **/

    boolean isNew; // if true, it is teh creation of a PI, else false (edition of existing PI)
    boolean userSelected;
    boolean suppressionDone;

    private Date date;

    private TabPane tabPane;
    private Tab tab;
    private InterestPoint interestPoint;

    private ObservableList<String> observableListHashtag;
    private ObservableList<User> observableListUser;

    /**
     * PiTabCreate.fxml FXML elements
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

    //We separate ListViews from Lsit of object, because we will need them when savign
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

    @FXML
    private JFXTreeTableView<ListObjects.ResultObject> treeView;

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

        propositionVBox.setVisible(false);
        initTreeView();
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        userField.textProperty().addListener((observable, old_value, new_value) -> {
            treeView.setRoot(null);
            treeView.setVisible(false);

            //Not userSelected because we don't do search when user has chosen a user
            if (new_value.length() > 2 && !userSelected) {
                propositionVBox.setVisible(false);
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
     * If user choose an entry in the table, we launch the search by firing an event.
     * But we have to take the screen_name fisrt from the list
     *
     * @param event
     */
    @FXML
    private void treeViewClicked(MouseEvent event) {
        TreeItem<ListObjects.ResultObject> selectedResult = treeView.getSelectionModel().getSelectedItem();
        userSelected = true; //keep userSelected before userField.setText
        userField.setText(selectedResult.getValue().getScreen_name().get());
        treeView.setVisible(false);
        propositionVBox.setVisible(false);
        propositionProgressBar.setVisible(false);
    }

    /**
     * Sets the column of treeView*
     */
    private void initTreeView() {
        JFXTreeTableColumn<ListObjects.ResultObject, String> name = new JFXTreeTableColumn<>("");
        name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ListObjects.ResultObject, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ListObjects.ResultObject, String> resultObjectStringCellDataFeatures) {
                return resultObjectStringCellDataFeatures.getValue().getValue().getName();
            }
        });

        JFXTreeTableColumn<ListObjects.ResultObject, String> screen_name = new JFXTreeTableColumn<>("");
        screen_name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ListObjects.ResultObject, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ListObjects.ResultObject, String> resultObjectStringCellDataFeatures) {
                return resultObjectStringCellDataFeatures.getValue().getValue().getScreen_name();
            }
        });
        treeView.setShowRoot(false);
        treeView.getColumns().setAll(name, screen_name);
        treeView.getColumns().get(1).getStyleClass().add("idInList");
        treeView.getColumns().get(0).getStyleClass().add("nameINList");
        treeView.setFixedCellSize(25);
    }

    /**
     * Events
     **/
    @FXML
    public void discardJFXButtonPressed(ActionEvent event) {
        launchInfoDialog("Annulation", "La création du point d'intérêt a été annulée", "D'accord", false);
    }


    @FXML
    public void saveJFXButtonPressed(ActionEvent event) {
        interestPoint = new InterestPoint(nameJFXTextField.getText(), descriptionJFXTextArea.getText(), date, hashtagList, userList);

        piViewer.addInterestPointToDatabase(interestPoint);
        launchInfoDialog("Enregistrement réussi", "Votre point d'intérêt a été enregistré", "D'accord", true);
    }

    @FXML
    public void addHashtagJFXButtonPressed(ActionEvent event) {
        String hashtagInput = hashtagField.getText();
        hashtagInput = hashtagInput.replaceAll("\\s", "");

        //Remove thee # on the beginning before adding
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

                        treeView.setVisible(false);
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
    private void launchInfoDialog(String header, String text, String labelButton, boolean success) {
        Label headerLabel = new Label(header);
        Text bodyText = new Text(text);
        JFXButton button = new JFXButton(labelButton);

        BoxBlur blur = new BoxBlur(3, 3, 3);

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
        dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
            anchorPane.setEffect(null);
        });
        anchorPane.setEffect(blur);

        dialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                tabPane.getTabs().remove(tab);
                mainController.goToMyPisPane();
            }
        });
    }

    /**
     * This function has to be called just after initialisation of this controller
     */
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
        // if the PI already exists, we show it
        if (isNew) {
            // TODO fill the entries with the Interest Point attributes
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
                usersNamesAndScreenNames = searchViewer.getListPropositions(newValue);

                ObservableList<ListObjects.ResultObject> resultObjects = FXCollections.observableArrayList();
                for (Map.Entry<String, String> entry : usersNamesAndScreenNames.entrySet()) {
                    resultObjects.add(new ListObjects.ResultObject(entry.getKey(), entry.getValue()));
                }
                Platform.runLater(() -> {
                    final TreeItem<ListObjects.ResultObject> root = new RecursiveTreeItem<ListObjects.ResultObject>(resultObjects, RecursiveTreeObject::getChildren);
                    treeView.setRoot(root);

                    if (resultObjects.size() > 0) {
                        treeView.setVisible(true);
                        propositionVBox.setVisible(true);
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
     * @author Sergiy
     * A Cell element used as an entity shown in the Hashtag JFXListView
     */
    public final class HashtagCell extends ListCell<String> {
        GridPane cellGridPane;
        ColumnConstraints column1;
        ColumnConstraints column2;
        ColumnConstraints column3;

        Label hashtagIconLabel;
        Label hashtagLabel;
        JFXButton removeHashtagJFXButton;

        FontIcon hashtagIcon;
        FontIcon minusIcon;

        public HashtagCell() {
            super();

            cellGridPane = new GridPane();
            cellGridPane.setPrefSize(550, 50);
            cellGridPane.getStyleClass().add("userCellGridPane");

            column1 = new ColumnConstraints();
            column1.setPrefWidth(100);
            column2 = new ColumnConstraints();
            column2.setPrefWidth(400);
            column3 = new ColumnConstraints();
            column3.setPrefWidth(50);

            cellGridPane.getColumnConstraints().addAll(column1, column2, column3);

            hashtagIconLabel = new Label();

            hashtagIcon = new FontIcon("fas-hashtag");
            hashtagIcon.setIconSize(18);
            hashtagIconLabel.setGraphic(hashtagIcon);

            hashtagLabel = new Label();
            removeHashtagJFXButton = new JFXButton();

            minusIcon = new FontIcon("fas-minus");
            minusIcon.setIconSize(18);
            minusIcon.setIconColor(Paint.valueOf("#CB7C7AFF"));

            removeHashtagJFXButton.setGraphic(minusIcon);

            removeHashtagJFXButton.setPrefSize(50, 50);

            removeHashtagJFXButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Action: " + getItem());
                    String hashtagStringObject = getItem();
                    hashtagList = hashtagList.stream().filter(hashtag -> hashtag.getHashtag().equals(hashtagStringObject)).collect(Collectors.toList());
                    observableListHashtag.remove(hashtagStringObject);
                }
            });

            cellGridPane.add(hashtagIconLabel, 0, 0);
            cellGridPane.add(hashtagLabel, 1, 0);
            cellGridPane.add(removeHashtagJFXButton, 2, 0);
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
                setGraphic(cellGridPane);
            }
        }
    }

    /**
     * @author Sergiy
     * <p>
     * A Cell element used as an entity shown in the Users JFXListView
     */
    public final class UserCell extends ListCell<User> {
        GridPane cellGridPane;
        ColumnConstraints column1;
        ColumnConstraints column2;
        ColumnConstraints column3;
        ColumnConstraints column4;

        ImageView profileImageView;
        Label screenNameLabel;
        Label followersCountLabel;
        JFXButton removeUserJFXButton;

        Image profilePicture;
        FontIcon minusIcon;

        public UserCell() {
            super();

            cellGridPane = new GridPane();
            cellGridPane.setPrefSize(550, 50);
            cellGridPane.getStyleClass().add("userCellGridPane");

            column1 = new ColumnConstraints();
            column1.setPrefWidth(50);
            column2 = new ColumnConstraints();
            column2.setPrefWidth(150);
            column3 = new ColumnConstraints();
            column3.setPrefWidth(300);
            column4 = new ColumnConstraints();
            column4.setPrefWidth(50);

            cellGridPane.getColumnConstraints().addAll(column1, column2, column3, column4);

            profileImageView = new ImageView();

            screenNameLabel = new Label();
            followersCountLabel = new Label();

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

                                cellGridPane.setVisible(false);
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

            cellGridPane.add(profileImageView, 0, 0);
            cellGridPane.add(screenNameLabel, 1, 0);
            cellGridPane.add(followersCountLabel, 2, 0);
            cellGridPane.add(removeUserJFXButton, 3, 0);
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
                        followersCountLabel.setText("Followers: " + Long.toString(user.getFollowers_count()));

                        setText(null);
                        setGraphic(cellGridPane);
                    }
                });

            }
        }
    }
}
