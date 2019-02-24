package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import model.SingleForecast;
import model.WheaterForecast;
import presentation.IObserver;
import presentation.ObserverFactory;

public class MyControllerWheaterForm implements Initializable {

	@FXML
    private TabPane tabPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		IObserver<SingleForecast> o = ObserverFactory.createObserverForecastTabPane(tabPane);
		WheaterForecast.GetInstance().attachObserver(o);
	}
	
}
