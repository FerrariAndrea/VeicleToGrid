package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.CurrentSession;
import model.Document;

public class MyControllerRegistrationForm implements Initializable {

	@FXML
    private TextField emailField;

	@FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private TextField nickNameField;

    @FXML
    private Button createAccountButton;

    @FXML
    private Button cancelButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	@FXML
    void clickCancelButton(MouseEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
    }

    @FXML
    void clickCreateAccountButton(MouseEvent event) {
    	if(Document.GetInstance().isExit()){
    		Alert alert = new Alert(Alert.AlertType.ERROR, "The application is not running");
    		alert.setTitle("Error");
    		alert.setHeaderText("Error");
    		alert.showAndWait();
    		
    		Stage stage = (Stage) createAccountButton.getScene().getWindow();
    	    stage.close();
    	    return;
    	}
    	
    	boolean check = true;
    	String error = "";
    	if(emailField.getText().length() == 0 || passwordField.getText().length() == 0 || repeatPasswordField.getText().length() == 0
    			|| nickNameField.getText().length() == 0) check = false; 
    	if(!passwordField.getText().equals(repeatPasswordField.getText())) {check = false; error = "The first password does not coincide with the first one";}
    	
    	//qua eventualmente verificare altre cose ad esempio come deve essere fatta la password
    	
    	if(!check){
    		Alert alert = new Alert(Alert.AlertType.ERROR, error);
    		alert.setTitle("Error");
    		alert.setHeaderText("The data entered are not correct");
    		alert.showAndWait();
    		return;
    	}
    	
    	try{
    		CurrentSession.GetInstance().Registration(emailField.getText(), passwordField.getText(), nickNameField.getText());
    		
    		Stage stage = (Stage) createAccountButton.getScene().getWindow();
    	    stage.close();
    	    
    	}catch(IllegalArgumentException e){
    		Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage());
    		alert.setTitle("Error");
    		alert.setHeaderText("Error during registration");
    		alert.showAndWait();
    	}
    }

}
