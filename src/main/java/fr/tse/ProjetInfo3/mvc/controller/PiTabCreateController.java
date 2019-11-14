package fr.tse.ProjetInfo3.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * 
 * @author Sergiy
 *
 */
public class PiTabCreateController {
	private MainController mainController;

	/* Controller can acces to this Tab */
	public void injectMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@FXML
	private JFXButton addHashtagJFXButton;

	@FXML
	private JFXButton addUserJFXButton;

	@FXML
	private JFXButton discardJFXButton;

	@FXML
	private JFXButton saveJFXButton;

	@FXML
	private JFXTextField descriptionJFXTextJield;

	@FXML
	private JFXTextField creationDateJFXTextJield;

	@FXML
	private void initialize() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
		Date date = new Date();

		creationDateJFXTextJield.setEditable(false);
		creationDateJFXTextJield.setText("Créé le " + simpleDateFormat.format(date));
	}

	/** Events **/
	@FXML
	public void discardJFXButtonPressed(ActionEvent event) {

	}

	@FXML
	public void saveJFXButtonPressed(ActionEvent event) {

	}

	@FXML
	public void addHashtagJFXButtonPressed(ActionEvent event) {

	}

	@FXML
	public void addUserJFXButtonPressed(ActionEvent event) {

	}
}
