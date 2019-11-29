package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.viewer.HastagViewer;
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
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

	/** Controller variables **/

	boolean isNew; // if true, it is teh creation of a PI, else false (edition of existing PI)

	private Date date;

	private TabPane tabPane;
	private Tab tab;
	private InterestPoint interestPoint;

	private ObservableList<String> observableListHashtag;
	private ObservableList<User> observableListUser;

	/** PiTabCreate.fxml FXML elements **/

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
	private JFXTextField creationDateJFXTextField;

	@FXML
	private JFXTextField hashtagField;

	@FXML
	private JFXTextField userField;

	@FXML
	private GridPane suivisGrid;

	@FXML
	private GridPane suivisGrid2;

	@FXML
	private JFXListView<String> hashtagList;

	@FXML
	private JFXListView<User> userList;

	/** Proposition Box FXML elements **/
	@FXML
	private VBox propositionVBox;

	@FXML
	private JFXListView<String> propositionList;

	/** Initialization method **/
	@FXML
	private void initialize() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		date = new Date();

		hashtagList.setFocusTraversable(false);
		userList.setFocusTraversable(false);

		suivisGrid.setVisible(true);
		suivisGrid2.setVisible(true);
		propositionVBox.setVisible(false);
		creationDateJFXTextField.setEditable(true);
		creationDateJFXTextField.setText("Créé le " + simpleDateFormat.format(date));

		/**
		 * Proposition mechanism initialization - Taken from SearchTabController.java
		 **/
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		userField.textProperty().addListener((observable, old_value, new_value) -> {
			propositionList.getItems().clear();
			propositionList.setVisible(false);
			if (new_value.contains(" ")) {
				userField.setText(old_value);
			}

			if (userField.getText().isEmpty() || !userField.getText(0, 1).equals("@")) {
				userField.setText("@" + new_value);
			}

			if (new_value.length() > 2) {
				pause.setOnFinished(event -> {
					showPropositionList(new_value.substring(1));
				});
				pause.playFromStart();
			}

		});

		observableListHashtag = FXCollections.observableArrayList();
		observableListUser = FXCollections.observableArrayList();

		hashtagList.setItems(observableListHashtag);
		hashtagList.setCellFactory(hastagListView -> new HashtagCell());

		userList.setItems(observableListUser);
		userList.setCellFactory(userListView -> new UserCell());
	}

	@FXML
	private void propositionListClicked(MouseEvent event) {
		userField.setText(propositionList.getSelectionModel().getSelectedItem());
		propositionList.setVisible(false);
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
		interestPoint = new InterestPoint(nameJFXTextField.getText(), descriptionJFXTextArea.getText(), date);
		piViewer.addInterestPointToDatabase(interestPoint);
		launchInfoDialog("Enregistrement réussi", "Votre point d'intérêt a été enregistré", "D'accord", true);
	}

	@FXML
	public void addHashtagJFXButtonPressed(ActionEvent event) {
		String hashtagInput = hashtagField.getText();

		if (hashtagInput.charAt(0) == '#') {
			hashtagInput = hashtagInput.substring(0, hashtagInput.length());
		}

		hashtagList.getItems().add(hashtagInput);
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
						userList.getItems().add(userViewer.getUser());
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
				ObservableList<String> items = propositionList.getItems();
				SearchViewer searchViewer = new SearchViewer();
				// Here we remove the @ to make our research of propositions
				List<String> users = searchViewer.getListPropositions(newValue);
				// We go through the proposition list
				Platform.runLater(() -> {
					items.addAll(users);
					if (items.size() > 0) {
						propositionVBox.setVisible(true);
						propositionList.setVisible(true);
					}
				});
				return null;
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 
	 * @author Sergiy
	 * 
	 *         A Cell element used as an entity shown in the Hashtag JFXListView
	 *
	 */
	public final class HashtagCell extends ListCell<String> {
		GridPane cellGridPane;
		ColumnConstraints column1;
		ColumnConstraints column2;

		Label hashtagLabel;
		JFXButton removeHashtagJFXButton;

		public HashtagCell() {
			super();

			cellGridPane = new GridPane();
			cellGridPane.setPrefSize(550, 50);

			column1 = new ColumnConstraints();
			column1.setPrefWidth(500);
			column2 = new ColumnConstraints();
			column2.setPrefWidth(50);

			cellGridPane.getColumnConstraints().addAll(column1, column2);

			hashtagLabel = new Label();
			removeHashtagJFXButton = new JFXButton();
			// removeHashtagJFXButton.setStyle("-fx-background-color: #F39C12;
			// -fx-text-fill: white;");
			removeHashtagJFXButton.setGraphic(new FontIcon("fas-minus"));

			removeHashtagJFXButton.setPrefSize(50, 50);

			removeHashtagJFXButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println("Action: " + getItem());
					String hashtagStringObject = getItem();
					hashtagList.getItems().remove(hashtagStringObject);
				}
			});

			cellGridPane.add(hashtagLabel, 0, 0);
			cellGridPane.add(removeHashtagJFXButton, 1, 0);
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
	 * 
	 * @author Sergiy
	 * 
	 *         A Cell element used as an entity shown in the Users JFXListView
	 *
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

		public UserCell() {
			super();

			cellGridPane = new GridPane();
			cellGridPane.setPrefSize(550, 50);

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
			removeUserJFXButton.setGraphic(new FontIcon("fas-minus"));

			removeUserJFXButton.setPrefSize(50, 50);

			removeUserJFXButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println("Action: " + getItem());
					User userObject = getItem();
					userList.getItems().remove(userObject);
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
				setGraphic(cellGridPane);
			}
		}
	}
}
