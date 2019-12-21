package fr.tse.ProjetInfo3.mvc.controller;

import javafx.animation.RotateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

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

		Pane pane1 = new Pane();

		gridPane.add(pane1, 0, 0);
		
		makeChartAppear(pane1, ac);
	}

	void rotateNode(Node node) {
		RotateTransition rotate = new RotateTransition();

		// Setting Axis of rotation
		rotate.setAxis(Rotate.Y_AXIS);

		// setting the angle of rotation
		rotate.setByAngle(90);

		// setting cycle count of the rotation
		// rotate.setCycleCount(500);

		// Setting duration of the transition
		rotate.setDuration(Duration.millis(1000));

		// the transition will be auto reversed by setting this to true
		// rotate.setAutoReverse(true);

		// setting Rectangle as the node onto which the
		// transition will be applied
		rotate.setNode(node);

		// playing the transition
		rotate.play();
	}

	void makeChartAppear(Pane pane, AreaChart<Number, Number> ac) {
		RotateTransition rotate2 = new RotateTransition();
		rotate2.setAxis(Rotate.Y_AXIS);
		rotate2.setByAngle(90);
		rotate2.setDuration(Duration.millis(1000));
		rotate2.setAutoReverse(true);  
		rotate2.setNode(pane);

		RotateTransition rotate1 = new RotateTransition();
		rotate1.setAxis(Rotate.Y_AXIS);
		rotate1.setByAngle(90);
		rotate1.setDuration(Duration.millis(1000));
		rotate1.setAutoReverse(false);  
		rotate1.setNode(pane);
		
		rotate1.setOnFinished(e -> {
			rotate2.play();
			pane.getChildren().add(ac);
		});
		
		RotateTransition rotateInitial = new RotateTransition();

		rotateInitial.setAxis(Rotate.Y_AXIS);
		rotateInitial.setByAngle(180);
		rotateInitial.setDuration(Duration.millis(1));
		rotateInitial.setAutoReverse(false);  
		rotateInitial.setNode(pane);
		
		rotateInitial.setOnFinished(e -> {
			rotate1.play();
		});
		
		rotateInitial.play();
	}

	void dynamicallyCompleteCharts() {
		
	}

}
