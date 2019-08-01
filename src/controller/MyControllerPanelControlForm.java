package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Document;
import model.ParameterInformation;
import model.ParametersSimulation;
import model.Storage;
import model.StoragePolicyFactory;
import model.ParameterInformation.ParameterInformationListValue;
import model.ParameterInformation.ParameterInformationSingleValue;

public class MyControllerPanelControlForm implements Initializable{
	
	@FXML
    private ToggleButton pauseButton;

    @FXML
    private ToggleGroup manageTime;

    @FXML
    private ToggleButton stopButton;
    
    @FXML
    private ListView<HBoxCell> parameterList;
    
    @FXML
    private Slider speedTimeSlider;
    
    @FXML
    private ToggleButton playButton;
    
    private TextArea logTextAreaMain;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		speedTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
            	Document.GetInstance().setSleepTimeStep(new_val.intValue());
            }
		});
		
		populateListView();
		
		playButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				//se arrivamo in pausa a causa di un reset rifacciamo partire
				Document.GetInstance().resumePause();
				
				//setto tutti i parametri
				if( !takeValueOfParameters()) { event.consume(); return;}
				
				//parte l'applicazione
				if(!Document.GetInstance().isStart()) Document.GetInstance().start();
				 
				//disabilito la list view con i parametri
				parameterList.setDisable(true);
				 
				playButton.setDisable(true);
				 
				//abilito i pulsanti di pause e di stop
				pauseButton.setDisable(false);
				stopButton.setDisable(false);
				speedTimeSlider.setDisable(false);
			}
			
		});
		
		pauseButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if(pauseButton.selectedProperty().get() == false) {
		    		//è stato rilasciato, riprende il simulatore
		    		pauseButton.setStyle("-fx-base: white;");
		    		
		    		//setto i parametri
		    		takeValueOfParameters();
		    		
		    		Document.GetInstance().resumePause();
		    		
		    		//disabilito la listView dei parametri
		    		parameterList.setDisable(true);
		    	}else{
		    		//è stato premuto, siamo in pausa
		    		pauseButton.setStyle("-fx-base: gray;");
		    		Document.GetInstance().pause();
		    		//reiabilito la listView dei parametri
		    		parameterList.setDisable(false);
		    	}
		    }
		});
		
		stopButton.setOnAction(new EventHandler<ActionEvent> (){
			@Override 
		    public void handle(ActionEvent e) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to restart the simulation?");
				alert.setHeaderText("Ask confirmation");
				alert.setTitle("Warning"); 
				Optional<ButtonType> result = alert.showAndWait();
			    if (result.isPresent() && result.get() == ButtonType.OK) {
			    	
			    	//restart confermato, siamo in pausa
			    	Document.GetInstance().reset();
			    	logTextAreaMain.setText("");
			    	
			    	//disattivo/attivo pulsanti e lista
			    	pauseButton.setDisable(true);
			    	playButton.setDisable(false);
			    	parameterList.setDisable(false);
			    	
			    	//refresh della list view con i parametri iniziali
			    	for(HBoxCell cell : parameterList.getItems()) {
			    		cell.refresh();
			    	}
			    	
			    }else if (result.isPresent() && result.get() == ButtonType.CANCEL) stopButton.setStyle("-fx-base: white;");
			}
		});
	}

	private void populateListView() {
		
        List<HBoxCell> list = new ArrayList<>();
        for (String s : ParametersSimulation.GetInstance().getAllParameterName()) {
             if(!ParametersSimulation.GetInstance().getInformationOfParameter(s).isInitial()) list.add(new HBoxCell(s));
        }

        
        ObservableList<HBoxCell> myObservableList = FXCollections.observableList(list);
        parameterList.setItems(myObservableList);
		
	}
	
	private class HBoxCell extends HBox {
		private String _parameterName;
		private Object _value = null;
		private Control _control;

        public HBoxCell(String parameterName) {
            super();
            
            _parameterName = parameterName;
            Label label = new Label("");
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            ParameterInformation<?> pInfo = ParametersSimulation.GetInstance().getInformationOfParameter(parameterName);
            if(pInfo.getClass().equals(ParameterInformationSingleValue.class)) _control = manageSingleValue(ParameterInformationSingleValue.class.cast(pInfo));
            else _control = manageMultipleValue(ParameterInformationListValue.class.cast(pInfo));
            label.setText(pInfo.get_graficInterfaceName());
            this.getChildren().addAll(label, _control);
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
						if(!Document.GetInstance().isStart()) playButton.setDisable(false);
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
		        	if(!Document.GetInstance().isStart()) playButton.setDisable(false);
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
						if(!Document.GetInstance().isStart()) playButton.setDisable(false);
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
		
		public void refresh() {
			ParameterInformation<?> pInfo = ParametersSimulation.GetInstance().getInformationOfParameter(getParameterName());
			if(pInfo.getClass().equals(ParameterInformationSingleValue.class)) {
				if(pInfo.getType().equals(Integer.class)) {
					@SuppressWarnings("unchecked")
					Spinner<Integer> s = Spinner.class.cast(_control);
					SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, (Integer)pInfo.getValue());
			        s.setValueFactory(valueFactory);
				}else {
					Label label = Label.class.cast(_control);
					label.setText(pInfo.getValue().toString());
				}
			}else {
				SplitMenuButton splitMenu = SplitMenuButton.class.cast(_control);
				splitMenu.setText("");
			}
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
			if(!pInfo.isModified() && !Document.GetInstance().isStart()) allModified = false;
			if(cell.getParameterName().equals("policy")) {
				if(cell.getValue().toString().equals("Cheap"))Storage.GetInstance().setPolicy(StoragePolicyFactory.CreateCheapStoragePolicy());
				else Storage.GetInstance().setPolicy(StoragePolicyFactory.CreateExpensiveStoragePolicy());
			}
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
	
	public void setMain(TextArea logTextArea) {
		logTextAreaMain = logTextArea;
	}
}
