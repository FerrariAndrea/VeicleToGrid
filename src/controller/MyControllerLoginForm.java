package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CurrentSession;
import model.Document;

public class MyControllerLoginForm implements Initializable{
	
	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField emailField;

	@FXML
	private Button cancelButton;

    @FXML
    private Button signInAccount;

    @FXML
    private Button createAccountButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	@FXML
    void clickCancelButton(MouseEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
    }

    @FXML
    void clickCreateAccountButton(MouseEvent event) throws IOException {
    	if(Document.GetInstance().isExit()){
    		Alert alert = new Alert(Alert.AlertType.ERROR, "The application is not running");
    		alert.setTitle("Error");
    		alert.setHeaderText("Error");
    		alert.showAndWait();
    		
    		Stage stage = (Stage) createAccountButton.getScene().getWindow();
    	    stage.close();
    	    return;
    	}
    	
    	Stage registrationStage = new Stage();
    	registrationStage.resizableProperty().set(Boolean.FALSE);
		AnchorPane createAccountPanel = (AnchorPane) FXMLLoader.load(getClass().getResource("/presentation/registrationForm.fxml"));
		Scene scene = new Scene(createAccountPanel, 600, 400);
		registrationStage.setScene(scene);
		registrationStage.show();
		
		Stage stage = (Stage) createAccountButton.getScene().getWindow();
	    stage.close();
    }

    @FXML
    void clickSignInButton(MouseEvent event) {
    	if(Document.GetInstance().isExit()){
    		Alert alert = new Alert(Alert.AlertType.ERROR, "The application is not running");
    		alert.setTitle("Error");
    		alert.setHeaderText("Error");
    		alert.showAndWait();
    		
    		Stage stage = (Stage) signInAccount.getScene().getWindow();
    	    stage.close();
    	    return;
    	}
    	CurrentSession.GetInstance().Login(emailField.getText(), passwordField.getText());
    	if(CurrentSession.GetInstance().getRegisteredUser() == null){
    		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Email or password are not correct");
    		alert.setTitle("Warning");
    		alert.setHeaderText("Message");
    		alert.showAndWait();
    	}else{
    		Stage stage = (Stage) signInAccount.getScene().getWindow();
    	    stage.close();
    	}
    }

}
