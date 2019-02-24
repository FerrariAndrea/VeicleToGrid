package controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Document;

public class MyControllerPanelControlForm implements Initializable{
	
	@FXML
    private ToggleButton pauseButton;

    @FXML
    private ToggleGroup manageTime;

    @FXML
    private ToggleButton stopButton;

    @FXML
    private ToggleGroup radioButtonPolitics;
    
    @FXML
    private Slider speedTimeSlider;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		speedTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
            	Document.GetInstance().setSleepTimeStep(new_val.intValue());
            }
		});
		
		radioButtonPolitics.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			@Override
	        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				
				if (radioButtonPolitics.getSelectedToggle() != null) {
					//parte l'applicazione
					if(!Document.GetInstance().isStart()) Document.GetInstance().start();
					
					RadioButton button = (RadioButton)radioButtonPolitics.getSelectedToggle();
		            System.out.println(button.getText());
		             
		           //disabilito i radio button
					radioButtonPolitics.getToggles().forEach(toggle -> {
					    Node node = (Node) toggle ;
					    node.setDisable(true);
					});
					
					//abilito i pulsanti di pause e di stop
					pauseButton.setDisable(false);
					stopButton.setDisable(false);
					speedTimeSlider.setDisable(false);
		         }
			}
		});
		
		pauseButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if(pauseButton.selectedProperty().get() == false) {
		    		//è stato rilasciato, riprende il simulatore
		    		pauseButton.setStyle("-fx-base: white;");
		    		Document.GetInstance().resumePause();
		    		
		    		//disabilito i radio button della politica
		    		radioButtonPolitics.getToggles().forEach(toggle -> {
					    Node node = (Node) toggle ;
					    node.setDisable(true);
					});
		    		
		    	}else{
		    		//è stato premuto, siamo in pausa
		    		pauseButton.setStyle("-fx-base: gray;");
		    		Document.GetInstance().pause();
		    		//riattivo i pulsanti di selezione della politica
		    		radioButtonPolitics.getToggles().forEach(toggle -> {
					    Node node = (Node) toggle ;
					    node.setDisable(false);
					});
		    	}
		    }
		});
		
		stopButton.setOnAction(new EventHandler<ActionEvent> (){
			@Override 
		    public void handle(ActionEvent e) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to stop the simulation?");
				alert.setHeaderText("Ask confirmation");
				alert.setTitle("Warning"); 
				Optional<ButtonType> result = alert.showAndWait();
			    if (result.isPresent() && result.get() == ButtonType.OK) {
			    	//stop confermato
			    	Document.GetInstance().exit();
			    	
			    	//chiusura finestra
			    	Stage s = (Stage)stopButton.getScene().getWindow();
			    	s.close();
			    }else if (result.isPresent() && result.get() == ButtonType.CANCEL) stopButton.setStyle("-fx-base: white;");
			}
		});
	}
}
