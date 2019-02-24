package presentation;

import java.util.Optional;

import controller.MyControllerMainForm;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Document;

//mettere a posto il semaforo del wheater generator!!!!!!!!!!!!!!!!!!!!!!!!!!

public class MainForm extends Application {
	
	private static MyControllerMainForm myController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//carico tutti i dati dal SIB
		Document.GetInstance().loadFromSIB();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("mainForm.fxml"));
		BorderPane root = (BorderPane) loader.load();
		myController = (MyControllerMainForm) loader.getController();
		Scene scene = new Scene(root, 1000, 700);
		primaryStage.setScene(scene);
		primaryStage.resizableProperty().setValue(Boolean.FALSE);
		primaryStage.show();
		myController.setStage(primaryStage);
		
		primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEventMainForm);
	}

	public static void main(String[] args){
		launch(args);
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

