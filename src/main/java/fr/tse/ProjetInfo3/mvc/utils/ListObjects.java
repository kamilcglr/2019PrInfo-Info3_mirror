package fr.tse.ProjetInfo3.mvc.utils;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

public class ListObjects {
	private static final Paint GREEN = Paint.valueOf("#48AC98FF");
	private static final Paint RED = Paint.valueOf("#CB7C7AFF");
	
	private static int currentPiId;

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
	public static class HashtagCellPI extends ListCell<ResultHashtag> {
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

		PIViewer piViewer;

		public HashtagCellPI(InterestPoint interestPoint) {
			super();

			this.interestPoint = interestPoint;
			piViewer = new PIViewer();

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

			addDeleteIcon = new FontIcon("fas-plus");
			addDeleteIcon.setIconSize(18);
			addDeleteIcon.setIconColor(GREEN);
			addDeleteHashtag.setGraphic(addDeleteIcon);

			addDeleteHashtag.setPrefSize(20, 20);

			cellGridPane.add(classementLabel, 0, 0);
			cellGridPane.add(hashtagLabel, 1, 0);
			cellGridPane.add(nbTweetLabel, 2, 0);
			cellGridPane.add(addDeleteHashtag, 3, 0);
		}

		public void updateItem(ResultHashtag resultHashtag, boolean empty) {
			super.updateItem(resultHashtag, empty);

			if (resultHashtag != null && !empty) {
				classementLabel.setText(resultHashtag.getClassementIndex());
				hashtagLabel.setText(resultHashtag.getHashtagName());
				nbTweetLabel.setText(resultHashtag.getNbTweets() + " tweets");
				setGraphic(cellGridPane);
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

	/**
	 * This class is used to print result inside the trreeTable
	 */
	public static class ResultObject extends RecursiveTreeObject<ResultObject> {
		private StringProperty name;
		private StringProperty screen_name;

		public ResultObject(String name, String sreen_name) {
			this.name = new SimpleStringProperty(name);
			this.screen_name = new SimpleStringProperty("@" + sreen_name);
		}

		public StringProperty getName() {
			return name;
		}

		public void setName(StringProperty name) {
			this.name = name;
		}

		public StringProperty getScreen_name() {
			return screen_name;
		}

		public void setScreen_name(StringProperty screen_name) {
			this.screen_name = screen_name;
		}
	}

	/*
	 * Used in topUser inside PITab
	 */
	public static class TopUserCell extends ListCell<User> {
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
		PIViewer piViewer;
		User userObject;
		

		public TopUserCell(InterestPoint interestPointParam) {
			super();

			interestPoint = interestPointParam;
			piViewer = new PIViewer();
			userObject = getItem();
			
			currentPiId = interestPoint.getId();

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

			addDeleteUser.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (interestPoint.containsUser(getItem())) {
						User currentUser = getItem();

						piViewer.deleteInterestPointFromDatabaseById((int)currentPiId);
						interestPoint.getUsers().remove(currentUser);
						piViewer.addInterestPointToDatabase(interestPoint);

						addDeleteIcon = new FontIcon("fas-plus");
						addDeleteIcon.setIconColor(GREEN);
						addDeleteUser.setGraphic(addDeleteIcon);
					} else {
						User currentUser = getItem();
						
						System.out.println("ID of PI on suppression " + currentPiId);
						piViewer.deleteInterestPointFromDatabaseById(currentPiId);
						
						interestPoint = new InterestPoint(interestPoint);
						interestPoint.addToInterestPoint(currentUser);
						currentPiId = (int)piViewer.addInterestPointToDatabase(interestPoint);
						System.out.println(currentPiId);
						
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

				followersCountLabel.setText("Followers : " + Long.toString(user.getFollowers_count()));
				followersCountLabel.getStyleClass().add("followersCountLabel");

				setText(null);
				setGraphic(cellGridPane);
			}
		}
	}
}
