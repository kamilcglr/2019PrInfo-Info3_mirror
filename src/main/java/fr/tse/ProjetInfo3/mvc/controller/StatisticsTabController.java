package fr.tse.ProjetInfo3.mvc.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * @author Sergiy
 *         {@code This class acts as a Controller for the Tab which is used to display Interest Point statistics}
 */
public class StatisticsTabController {

	/**
	 * StatisticsTab.fxml FXML elements
	 **/
	@FXML
	private AnchorPane statisticsPane;

	@FXML
	private StackPane dialogStackPane;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private GridPane gridPane;

	public StatisticsTabController() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FXML
	private void initialize() {
		final NumberAxis xAxis = new NumberAxis(1, 31, 1);
		final NumberAxis yAxis = new NumberAxis();
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Temperature Monitoring (in Degrees C)");

		XYChart.Series seriesApril = new XYChart.Series();
		seriesApril.setName("April");
		seriesApril.getData().add(new XYChart.Data(1, 4));
		seriesApril.getData().add(new XYChart.Data(3, 10));
		seriesApril.getData().add(new XYChart.Data(6, 15));
		seriesApril.getData().add(new XYChart.Data(9, 8));
		seriesApril.getData().add(new XYChart.Data(12, 5));
		seriesApril.getData().add(new XYChart.Data(15, 18));
		seriesApril.getData().add(new XYChart.Data(18, 15));
		seriesApril.getData().add(new XYChart.Data(21, 13));
		seriesApril.getData().add(new XYChart.Data(24, 19));
		seriesApril.getData().add(new XYChart.Data(27, 21));
		seriesApril.getData().add(new XYChart.Data(30, 21));

		XYChart.Series seriesMay = new XYChart.Series();
		seriesMay.setName("May");
		seriesMay.getData().add(new XYChart.Data(1, 20));
		seriesMay.getData().add(new XYChart.Data(3, 15));
		seriesMay.getData().add(new XYChart.Data(6, 13));
		seriesMay.getData().add(new XYChart.Data(9, 12));
		seriesMay.getData().add(new XYChart.Data(12, 14));
		seriesMay.getData().add(new XYChart.Data(15, 18));
		seriesMay.getData().add(new XYChart.Data(18, 25));
		seriesMay.getData().add(new XYChart.Data(21, 25));
		seriesMay.getData().add(new XYChart.Data(24, 23));
		seriesMay.getData().add(new XYChart.Data(27, 26));
		seriesMay.getData().add(new XYChart.Data(31, 26));

		ac.getData().addAll(seriesApril, seriesMay);

		Pane pane = new Pane();
		pane.getChildren().add(ac);

		gridPane.add(pane, 0, 0);

		// pane1.getChildren().add(ac);
	}

}
