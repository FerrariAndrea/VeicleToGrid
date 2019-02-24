package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import model.CurrentSession;
import model.Parking;
import model.RegisteredUser;
import model.Reserving;
import presentation.IObserver;
import presentation.ObserverFactory;
import presentation.TimeSpinner;

public class MyControllerRegisteredUserForm implements Initializable {
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@FXML
    private Label labelNickName;

	@FXML
    private TableView<Reserving> tableViewReserving;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<Integer> minCargeSpinner;

    @FXML
    private Button insertButton;
    
    @FXML
    private GridPane grid;
    
    private TimeSpinner startTimeSpinner, endTimeSpinner;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelNickName.setText("Form of " + CurrentSession.GetInstance().getRegisteredUser().getNickname());
		
		initializeTableView();
		
		minCargeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 80));
		
		startTimeSpinner = new TimeSpinner();
    	grid.add(startTimeSpinner, 2, 0);
    	
    	endTimeSpinner = new TimeSpinner();
    	grid.add(endTimeSpinner, 2, 1);
    	
    	
    	startDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    	
    	endDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    	
	}
	
	@SuppressWarnings("unchecked")
	private void initializeTableView(){
		tableViewReserving.getColumns().clear();
		
		TableColumn<Reserving, String> userCol = new TableColumn<Reserving, String>("User");
		
		TableColumn<Reserving, LocalDateTime> startTimeCol = new TableColumn<Reserving, LocalDateTime>("Start time");
		startTimeCol.setCellFactory(col -> new TableCell<Reserving, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {

		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});
		
		TableColumn<Reserving, LocalDateTime> endTimeCol = new TableColumn<Reserving, LocalDateTime>("End time");
		endTimeCol.setCellFactory(col -> new TableCell<Reserving, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {

		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});
		
		TableColumn<Reserving, Integer> minChargeCol = new TableColumn<Reserving, Integer>("Min charge request");
		TableColumn<Reserving, Integer> parkingCol = new TableColumn<Reserving, Integer>("ParkingSpace ID");
		tableViewReserving.getColumns().addAll(userCol, startTimeCol, endTimeCol, minChargeCol, parkingCol);
		
		userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
		startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTimeReserving"));
		endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTimeReserving"));
		minChargeCol.setCellValueFactory(new PropertyValueFactory<>("minCharge"));
		parkingCol.setCellValueFactory(new PropertyValueFactory<>("parkingSpace"));
		
		startTimeCol.setSortType(TableColumn.SortType.DESCENDING);
		
		IObserver<ArrayList<Reserving>> o = ObserverFactory.createObserverReservingTableView(tableViewReserving);
		Parking.GetInstance().attachObserver(o);
	}
	
	@FXML
    void clickInsertButton(MouseEvent event) {
		LocalTime startTime = startTimeSpinner.getValue();
		LocalTime endTime = endTimeSpinner.getValue();
		LocalDate startDate = startDatePicker.getValue();
		LocalDate endDate = endDatePicker.getValue();
		Integer minCharge = minCargeSpinner.getValue();
		
		if(startTime == null || endTime == null || startDate == null || endDate == null || minCharge == null){
			Alert alert = new Alert(Alert.AlertType.ERROR, "Insert all value request");
    		alert.setTitle("Error");
    		alert.setHeaderText("Error message");
    		alert.showAndWait();
    		return;
		}
		
		try{
			LocalDateTime startTimeReserving = LocalDateTime.of(startDate, startTime);
			LocalDateTime endTimeReserving = LocalDateTime.of(endDate, endTime);
			RegisteredUser r = new RegisteredUser(CurrentSession.GetInstance().getRegisteredUser().getEmail(), CurrentSession.GetInstance().getRegisteredUser().getPassword(), CurrentSession.GetInstance().getRegisteredUser().getNickname());
			if(!Parking.GetInstance().createNewReserving(r, startTimeReserving, endTimeReserving, minCharge)){
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "All parking space will be busy in the specified interval time");
	    		alert.setTitle("Error");
	    		alert.setHeaderText("Error message");
	    		alert.showAndWait();
			}else{
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reserving inserted");
	    		alert.setTitle("Success");
	    		alert.setHeaderText("Information message");
	    		alert.showAndWait();
	    		startDatePicker.setValue(null);
	    		endDatePicker.setValue(null);
	    		minCargeSpinner.getValueFactory().setValue(0);
			}
		}catch(IllegalArgumentException e){
			Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
    		alert.setTitle("Error");
    		alert.setHeaderText("Error message");
    		alert.showAndWait();
		}
    }

}
