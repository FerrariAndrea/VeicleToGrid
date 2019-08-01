package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Document;
import model.ParameterInformation;
import model.ParametersSimulation;
import model.ParameterInformation.ParameterInformationListValue;
import model.ParameterInformation.ParameterInformationSingleValue;

public class MyControllerInitialForm implements Initializable{

	@FXML
	private Button closeButton;
	
	@FXML
	private Button confirmButton;
	
	@FXML
	private ListView<HBoxCell> parameterList;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		populateListView();
		
	}

	private void populateListView() {
		List<HBoxCell> list = new ArrayList<>();
        for (String s : ParametersSimulation.GetInstance().getAllParameterName()) {
             if(ParametersSimulation.GetInstance().getInformationOfParameter(s).isInitial()) list.add(new HBoxCell(s));
        }

        Collections.reverse(list);
        ObservableList<HBoxCell> myObservableList = FXCollections.observableList(list);
        parameterList.setItems(myObservableList);
	}

	@FXML
    void clickCloseButton(ActionEvent event) throws IOException {
		//evento chiusura
		Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close the application?").showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) Platform.exit();
	    else event.consume();	//fermo la chiusura dell'applicazione
	}
	
	@FXML
    void clickConfirmButton(ActionEvent event) throws IOException {
		//recupero dei parametri settati
		if( !takeValueOfParameters()) return;
		
		Stage mainStage = new Stage();
		mainStage.resizableProperty().setValue(Boolean.FALSE);
    	BorderPane mainPanel = (BorderPane) FXMLLoader.load(getClass().getResource("/presentation/mainForm.fxml"));
		Scene scene = new Scene(mainPanel, 1000, 700);
		mainStage.setScene(scene);
		mainStage.resizableProperty().setValue(Boolean.FALSE);
		mainStage.show();
		mainStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEventMainForm);
		
		
		Stage stage = (Stage) confirmButton.getScene().getWindow();
	    stage.close();
	}
	
	private class HBoxCell extends HBox {
		private String _parameterName; 
		private Object _value = null;
		
		public HBoxCell(String parameterName) {
	            super();
	            _parameterName = parameterName;
	            Label label = new Label("");
	            label.setMaxWidth(Double.MAX_VALUE);
	            HBox.setHgrow(label, Priority.ALWAYS);
	            ParameterInformation<?> pInfo = ParametersSimulation.GetInstance().getInformationOfParameter(parameterName);
	            Control control;
	            if(pInfo.getClass().equals(ParameterInformationSingleValue.class)) control = manageSingleValue(ParameterInformationSingleValue.class.cast(pInfo));
	            else control = manageMultipleValue(ParameterInformationListValue.class.cast(pInfo));
	            label.setText(pInfo.get_graficInterfaceName());
	            this.getChildren().addAll(label, control);
		 }

		private Control manageMultipleValue(ParameterInformationListValue<?> pInfo) {
			ArrayList<MenuItem> m = new ArrayList<>();
			SplitMenuButton splitMenu = new SplitMenuButton();
			for(Object o : pInfo.getAllValueOfParameter()) {
				MenuItem choice = new MenuItem(o.toString());
				choice.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						splitMenu.setText(o.toString());
						_value = o.toString();
						for(HBoxCell cell : parameterList.getItems())
							if(cell.getValue() == null) return;
						confirmButton.setDisable(false);
					}
					
				});
				m.add(choice);
			}
			splitMenu.getItems().addAll(m);
			
			return splitMenu;
		}

		private Control manageSingleValue(ParameterInformationSingleValue<?> pInfo) {
			_value = pInfo.getValue();
			if(pInfo.getType().equals(Integer.class)) {
				Spinner<Integer> spinner = new Spinner<Integer>();
				SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, (Integer)pInfo.getValue());
		        spinner.setValueFactory(valueFactory);
		        valueFactory.valueProperty().addListener((obs, oldValue, newValue) -> {
		        	_value = newValue;
		        	for(HBoxCell cell : parameterList.getItems())
						if(cell.getValue() == null) return;
		        	confirmButton.setDisable(false);
		        });
		        return spinner;
			}else {
				Label label = new Label("");
				label.setText(pInfo.getValue().toString());
				label.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
						_value = newValue;
						for(HBoxCell cell : parameterList.getItems())
							if(cell.getValue() == null) return;
						confirmButton.setDisable(false);
					}
				});
				return label;
			}
		}
		
		public String getParameterName() {
			return _parameterName;
		}
		
		public Object getValue() {
			return _value;
		}
	}
	
	private boolean takeValueOfParameters() {
		boolean allModified = true;
		for(HBoxCell cell : parameterList.getItems()) {
			ParameterInformation<?> pInfo = ParametersSimulation.GetInstance().getInformationOfParameter(cell.getParameterName());
			if(cell.getValue() != null) pInfo.setValueOfParameter(cell.getValue());
			else if(pInfo.getValue() == null) {
				//il parametro singolo non è stato specificato e non aveva un valore di default
				Alert alert = new Alert(Alert.AlertType.ERROR, "Specified all parameters");
	    		alert.setTitle("Error");
	    		alert.setHeaderText("Message");
	    		alert.showAndWait();
	    		return false;
			}
			if(!pInfo.isModified()) allModified = false;
		}
		int max = Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("maxChargeStorageCapacity").getValue());
		int initial = Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("InitialChargeStorage").getValue());
		if(max < initial) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Max charge of storage is less than initial");
			alert.setHeaderText("Error");
			alert.setTitle("Error");
			alert.showAndWait();
			return false;
		}
		if(!allModified) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There are some parameters that aren't modified (They have the default value). Do you want to continue?");
			alert.setHeaderText("Ask confirmation");
			alert.setTitle("Warning");
			Optional<ButtonType> result = alert.showAndWait();
		    if (result.isPresent() && result.get() == ButtonType.CANCEL) {
		    	return false;
		    }
		}
		return true;
	}
	
	private void closeWindowEventMainForm(WindowEvent event) {
		//evento chiusura della main windows
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close the application?");
		alert.setHeaderText("Ask confirmation");
		alert.setTitle("Warning");
		Optional<ButtonType> result = alert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	    	//chiusura confermata
	    	
	    	if(Document.GetInstance().isStart() && !Document.GetInstance().isExit()) Document.GetInstance().exit();
	    	if(Document.GetInstance().isStart()){
	    		//salvo sul database tutti gli oggetti
	    		Document.GetInstance().saveOnSIB();
	    	}
	    	
	    	//chiudo tutte le finestre aperte
	    	Platform.exit();
	    } else event.consume();	//fermo la chiusura dell'applicazione
	}
}
