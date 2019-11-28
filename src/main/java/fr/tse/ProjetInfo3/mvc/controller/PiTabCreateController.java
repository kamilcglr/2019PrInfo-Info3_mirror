package fr.tse.ProjetInfo3.mvc.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;

import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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

	/** HashtagCell.fxml FXML elements **/
	@FXML
	private Label hashtagLabel;

	@FXML
	private JFXButton removeHashtagJFXButton;

	@FXML
	private FontAwesomeSolid removeHashtagIcon;

	@FXML
	private GridPane gridPane;

	/** UserCell.fxml FXML elements **/

	@FXML
	private void initialize() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		date = new Date();

		hashtagList.setFocusTraversable(false);
		userList.setFocusTraversable(false);

		suivisGrid.setVisible(true);
		suivisGrid2.setVisible(true);
		creationDateJFXTextField.setEditable(true);
		creationDateJFXTextField.setText("Créé le " + simpleDateFormat.format(date));

		observableListHashtag = FXCollections.observableArrayList();
		observableListHashtag.addAll("dsadas", "dsadas", "dsadas", "dsadas", "dsadas", "dsadas");

		hashtagList.setItems(observableListHashtag);
		hashtagList.setCellFactory(hastagListView -> new HashtagCell());
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
		inputNewHashtag();
	}

	@FXML
	public void addUserJFXButtonPressed(ActionEvent event) {
		inputNewUser();
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

	private void inputNewUser() {
	}

	private void inputNewHashtag() {
		// String hashtagInput = hashtagField.getText();

		// if (hashtagInput.charAt(0) == '#') {
		// hashtagInput = hashtagInput.substring(0, hashtagInput.length());
		// }

		// hashtagList.getItems().add(hashtagInput);

	}

	class HashtagCell extends ListCell<String> {

		public HashtagCell() {
			super();
		}

		@Override
		protected void updateItem(String hashtagName, boolean empty) {
			super.updateItem(hashtagName, empty);

			if (empty || hashtagName == null) {

				setText(null);
				setGraphic(null);

			} else {
				FXMLLoader fxmlLoader = null;

				if (fxmlLoader == null) {
					fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/HashtagCell.fxml"));
					fxmlLoader.setController(this);

					try {
						fxmlLoader.load();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

				// hashtagLabel.setText("gdfgd");

				setText(null);
				setGraphic(gridPane);
			}
		}
	}
}
