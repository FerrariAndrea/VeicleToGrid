package presentation;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//mettere a posto il semaforo del wheater generator!!!!!!!!!!!!!!!!!!!!!!!!!!

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("initialForm.fxml"));
		BorderPane root = (BorderPane) loader.load();
		Scene scene = new Scene(root, 700, 300);
		primaryStage.setScene(scene);
		primaryStage.resizableProperty().setValue(Boolean.FALSE);
		primaryStage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
}

