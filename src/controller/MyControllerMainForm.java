package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.charts.Legend;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.*;
import presentation.*;


public class MyControllerMainForm implements Initializable {

	private Stage controlStage = null, wheaterStage = null, loginStage = null;
	
    @FXML
    private Button LoginButton;

    @FXML
    private Label LabelTime;

    @FXML
    private AnchorPane panelParking;
    
    @FXML
    private TextArea logTextArea;
    
    @FXML
    private BarChart<String, Number> chartParking;
   
    @FXML
    private ImageView imageParking;
    
    private GridPane gridParking = new GridPane();
    
    @FXML
    private ProgressBar progressBarStorage;
    
    @FXML
    private MenuItem fileClose;

    @FXML
    private MenuItem fileViewPanelControl;

    @FXML
    private MenuItem fileViewUserPanel;
    
    @FXML
    private MenuItem fileViewWheaterForecast;
    
    @FXML
    private Label labelNickName;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) { 
		
		//inizialize labelTime
		IObserver<LocalDateTime> old = ObserverFactory.createObserverCurrentTimeLabel(LabelTime);
		Document.GetInstance().attachObserver(old);
		
		//inizialize grid
		initializeGrid();
		
		//inizialize progressBarStorage
		IObserver<Storage> os = ObserverFactory.createObserverProgressBarStorage(progressBarStorage);
		Storage.GetInstance().attachObserver(os);
		
		//inizialize chartParking
		initializeChartBar();
		
		//initialize TextArea
		IObserver<Log> ol = ObserverFactory.createObserverLogTextArea(logTextArea);
		ContainerEvent.GetInstance().attachObserver(ol);
		logTextArea.setEditable(false);
		
		//initialize loginButton
		IObserver<RegisteredUser> oRU = ObserverFactory.createObserverRegisteredUserButton(LoginButton);
		CurrentSession.GetInstance().attachObserver(oRU);
		
		//initialize labelNickName
		oRU = ObserverFactory.createObserverRegisteredUserLabel(labelNickName);
		CurrentSession.GetInstance().attachObserver(oRU);
		
		//initilize fileViewUserPanel
		oRU = ObserverFactory.createObserverRegisteredUserMenuItem(fileViewUserPanel);
		CurrentSession.GetInstance().attachObserver(oRU);
	}
	
	private void initializeChartBar(){
		XYChart.Series<String, Number> series1 = new XYChart.Series<>();
		series1.setName("free");
		series1.getData().add(new XYChart.Data<String, Number>("State", 0));
		XYChart.Series<String, Number> series2 = new XYChart.Series<>();
		series2.setName("busy");
		series2.getData().add(new XYChart.Data<String, Number>("State", 0));
		chartParking.getData().add(series1);
		chartParking.getData().add(series2);
		chartParking.lookup(".default-color0.chart-bar").setStyle("-fx-bar-fill: green");
		chartParking.lookup(".default-color1.chart-bar").setStyle("-fx-bar-fill: red");
		for(Node n : chartParking.getChildrenUnmodifiable()){
			   if(n instanceof Legend){
				   ((Legend)n).getItems().get(0).getSymbol().setStyle("-fx-background-color: #0000ff, green;");
				   ((Legend)n).getItems().get(1).getSymbol().setStyle("-fx-background-color: #0000ff, red;");
			   }
			}
		IObserver<ParkingSpace> o = ObserverFactory.createObserverChartParking(chartParking);
		for(ParkingSpace p : Parking.GetInstance().getParkingSpace())
			p.attachObserver(o);
		
		chartParking.getData().get(0).getData().get(0).setYValue(Parking.GetInstance().numberOfFreeParkingSpace());
		chartParking.getData().get(1).getData().get(0).setYValue(Parking.GetInstance().getParkingSpace().size() - Parking.GetInstance().numberOfFreeParkingSpace());
		
	}
	
	private void initializeGrid(){
		boolean stop = false;
		int index = 0;
		
		ArrayList<ParkingSpace> parking = Parking.GetInstance().getParkingSpace();
		gridParking.setAlignment(Pos.CENTER);
		gridParking.setHgap(10);
		gridParking.setVgap(10);
		gridParking.setPadding(new Insets(10,10,10,10));
		ScrollPane sp = new ScrollPane();
		sp.setContent(gridParking);
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);
	    AnchorPane.setBottomAnchor(sp, 0.0);
		AnchorPane.setLeftAnchor(sp, 0.0);
		AnchorPane.setRightAnchor(sp, 0.0);
		AnchorPane.setTopAnchor(sp , -(panelParking.getHeight() - imageParking.getFitHeight()));
		sp.setStyle("-fx-background: rgb(120, 222, 198); -fx-border-color: rgb(120, 222, 198); -fx-background-color: rgb(120, 222, 198);");
		
		ColumnConstraints cc = new ColumnConstraints();
		cc.setFillWidth(true);
        cc.setHgrow(Priority.ALWAYS);
        for (int i = 0; i < 6; i++) {
            gridParking.getColumnConstraints().add(cc);
        }

        RowConstraints rc = new RowConstraints();
        rc.setFillHeight(true);
        rc.setVgrow(Priority.ALWAYS);

        int rowCount = parking.size() / 6;
        if(parking.size() % 6 != 0) rowCount ++;
        for (int i = 0; i < rowCount; i++) {
            gridParking.getRowConstraints().add(rc);
        }
        
        for (int i = 0; stop == false; i++) {
            for (int j = 0; j < 6 && stop == false; j++) {
            	if(index < parking.size()){
	            	HBox box = new HBox();
	            	box.setBackground(new Background(new BackgroundFill(parking.get(index).getColor(), null, null)));
	            	box.setMaxHeight(30);
	            	box.setAlignment(Pos.CENTER);
	            	ProgressBar bar = new ProgressBar();
	            	IObserver<ParkingSpace> o = ObserverFactory.createObserverProgressBarParkingSpace(bar);
	            	parking.get(index).attachObserver(o);
	            	index ++;
	            	box.getChildren().add(bar);
	            	
	            	gridParking.add(box, j, i);
            	}else stop = true;
            }
        }
        panelParking.getChildren().add(sp);
	}
	
	public static void closeMain(Event event) {
		//evento chiusura della main windows
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close the application?");
		alert.setHeaderText("Ask confirmation");
		alert.setTitle("Warning");
		Optional<ButtonType> result = alert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	    	//chiusura confermata
	    	
	    	if(Document.GetInstance().isStart() && !Document.GetInstance().isExit()) Document.GetInstance().exit();
	    	
	    	//chiudo tutte le finestre aperte
	    	Platform.exit();
	    } else event.consume();	//fermo la chiusura dell'applicazione
	}
	
	@FXML
    void clickViewClose(ActionEvent event) {
	    closeMain(event);
    }

    @FXML
    void clickViewPanelControl(ActionEvent event) throws IOException {
    	if(controlStage == null){
    		controlStage = new Stage();
			controlStage.resizableProperty().setValue(Boolean.FALSE);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/presentation/panelControlForm.fxml"));
			AnchorPane controlPanel = (AnchorPane) loader.load();
			MyControllerPanelControlForm controller = (MyControllerPanelControlForm) loader.getController();
			controller.setMain(logTextArea);
			Scene scene = new Scene(controlPanel, 700, 300);
			controlStage.setScene(scene);
			controlStage.show();
			
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			controlStage.setX((primScreenBounds.getWidth() - ((Stage) labelNickName.getScene().getWindow()).getWidth() - controlStage.getWidth()) /4);
			controlStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		        		  fileViewPanelControl.setDisable(false);
		          }
		     }); 
    	}else controlStage.show();
		fileViewPanelControl.setDisable(true);
    }

    @FXML
    void clickViewUserPanel(ActionEvent event) throws IOException {
    	Stage userPanelStage = new Stage();
    	userPanelStage.resizableProperty().setValue(Boolean.FALSE);
    	BorderPane userPanel = (BorderPane) FXMLLoader.load(getClass().getResource("/presentation/registeredUserForm.fxml"));
		Scene scene = new Scene(userPanel, 600, 400);
		userPanelStage.setScene(scene);
		IObserver<RegisteredUser> o = ObserverFactory.createObserverRegisteredUserStage(userPanelStage);
		CurrentSession.GetInstance().attachObserver(o);
		userPanelStage.showAndWait();
		CurrentSession.GetInstance().detachObserver(o);
		
    }
    
    @FXML
    void clickViewWheaterForecast(ActionEvent event) throws IOException {
    	if(wheaterStage == null){
    		wheaterStage = new Stage();
    		wheaterStage.resizableProperty().set(Boolean.FALSE);
    		AnchorPane wheaterPanel = (AnchorPane) FXMLLoader.load(getClass().getResource("/presentation/wheaterForecastForm.fxml"));
    		Scene scene = new Scene(wheaterPanel, 700, 400);
    		wheaterStage.setScene(scene);
    		wheaterStage.show();
    		
    		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
    		wheaterStage.setX((primScreenBounds.getWidth() - wheaterStage.getWidth() - wheaterStage.getWidth()) /4);
    		wheaterStage.setY(primScreenBounds.getHeight() - wheaterStage.getHeight() - 20);
    		wheaterStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		        	  fileViewWheaterForecast.setDisable(false);
		          }
		     }); 
    	}else wheaterStage.show();
    	fileViewWheaterForecast.setDisable(true);
    }
    
    @FXML
    void clickLoginButton(MouseEvent event) throws IOException {
    	if(!Document.GetInstance().isStart() || Document.GetInstance().isExit()){
    		Alert alert = new Alert(Alert.AlertType.ERROR, "Application is not running");
    		alert.setTitle("Information");
    		alert.setHeaderText("Message");
    		alert.showAndWait();
    		return;
    	}
		if(CurrentSession.GetInstance().getRegisteredUser() == null){
    		if(loginStage == null){
    			loginStage = new Stage();
    			loginStage.resizableProperty().set(Boolean.FALSE);
        		AnchorPane wheaterPanel = (AnchorPane) FXMLLoader.load(getClass().getResource("/presentation/loginForm.fxml"));
        		Scene scene = new Scene(wheaterPanel, 400, 600);
        		loginStage.setScene(scene);
        		loginStage.showAndWait();
        		
    		}else loginStage.showAndWait();;
		}else{
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to exit?");
			alert.setTitle("Confirmation");
			alert.setHeaderText("Are you sure?");
			Optional<ButtonType> result = alert.showAndWait();
		    if (result.isPresent() && result.get() == ButtonType.OK) {
		    	CurrentSession.GetInstance().Logout();
		    }
		}
    }
}
