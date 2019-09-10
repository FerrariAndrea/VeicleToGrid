package presentation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.*;
import model.SingleForecast.TimeSlot;

public class ObserverFactory {
	public static ObserverFactory _instance = new ObserverFactory();
	
	public static  IObserver<ParkingSpace> createObserverProgressBarParkingSpace(ProgressBar control) {
		return _instance.new MyProgressBarParkingSpaceObserver(control);
	}
	
	public static  IObserver<Storage> createObserverProgressBarStorage(ProgressBar control) {
		return _instance.new MyProgressBarStorageObserver(control);
	}
	
	public static IObserver<ParkingSpace> createObserverChartParking(BarChart<String, Number> control){
		return _instance.new MyChartParkingObserver(control);
	}
	
	public static IObserver<Log> createObserverLogTextArea(TextArea control){
		return _instance.new MyTextAreaObserver(control);
	}

	public static IObserver<LocalDateTime> createObserverCurrentTimeLabel(Label control){
		return _instance.new MyLabelTimeObserver(control);
	}
	
	public static IObserver<SingleForecast> createObserverForecastTabPane(TabPane pane){
		return _instance.new MyTabPaneObserver(pane);
	}
	
	public static IObserver<RegisteredUser> createObserverRegisteredUserButton(Button button){
		return _instance.new MyButtonObserver(button);
	}
	
	public static IObserver<RegisteredUser> createObserverRegisteredUserLabel(Label label){
		return _instance.new MyLabelNickNameObserver(label);
	}
	
	public static IObserver<RegisteredUser> createObserverRegisteredUserMenuItem(MenuItem menuItem){
		return _instance.new MyMenuItemObserver(menuItem);
	}
	
	public static IObserver<ArrayList<Reserving>> createObserverReservingTableView(TableView<Reserving> tableView){
		return _instance.new MyTableViewObserver(tableView);
	}
	
	public static IObserver<RegisteredUser> createObserverRegisteredUserStage(Stage stage){
		return _instance.new MyStageObserver(stage);
	}

	private abstract class MyProgressBarObserver extends ObserverControl<ProgressBar>{
		protected static final String RED_BAR    = "-fx-accent: red;";
		protected static final String YELLOW_BAR = "-fx-accent: yellow;";
		protected static final String GREY_BAR = "-fx-accent: grey;";
		protected static final String GREEN_BAR  = "-fx-accent: green;";
		protected final String[] barColorStyleClasses = { RED_BAR, GREY_BAR, YELLOW_BAR, GREEN_BAR };
		
		protected MyProgressBarObserver(ProgressBar progressBar){
			super(progressBar);
		}
		
		protected void setColour(){
			getControl().getStyleClass().removeAll(barColorStyleClasses);
			if(getControl().getProgress() <= 1 && getControl().getProgress() >= 0.7) getControl().setStyle(GREEN_BAR);
			else{
				if(getControl().getProgress() < 0.7 && getControl().getProgress() >= 0.3) getControl().setStyle(YELLOW_BAR);
				else getControl().setStyle(RED_BAR);
			}
		}
	}
	
	private class MyProgressBarParkingSpaceObserver extends MyProgressBarObserver implements IObserver<ParkingSpace>{

		public MyProgressBarParkingSpaceObserver(ProgressBar progressBar){
			super(progressBar);
			free();
		}
		
		private void free(){
			getControl().setProgress(1);
			getControl().setOpacity(0.40);
			getControl().getStyleClass().removeAll(barColorStyleClasses);
			getControl().setStyle(GREY_BAR);
		}
		
		private void busy(ParkingSpace p){
			getControl().setProgress(((double)p.getActualVehicleStorage()) / Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("maxChargeVehicleStorage").getValue()));
			getControl().setOpacity(1);
			setColour();
		}
		
		@Override
		public void update(ParkingSpace parking) {
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					if(parking.isFree()) free();
					else busy(parking);
				}
			});
		}
	}
	
	private class MyProgressBarStorageObserver extends MyProgressBarObserver implements IObserver<Storage>{
		
		public MyProgressBarStorageObserver(ProgressBar progressBar){
			super(progressBar);
		}

		@Override
		public void update(Storage storage) { 
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					getControl().setProgress(storage.getActualCharge() / Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("InitialChargeStorage").getValue()));
					getControl().getStyleClass().removeAll(barColorStyleClasses);
					setColour();
				}
			});
		}
	}
	
	private class MyChartParkingObserver extends ObserverControl<BarChart<String, Number>> implements IObserver<ParkingSpace>{
		
		public MyChartParkingObserver(BarChart<String, Number> barChart){
			super(barChart);
		}
		
		@Override
		public void update(ParkingSpace parking) {
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					if(parking.isFree()) {
						getControl().getData().get(0).getData().get(0).setYValue(getControl().getData().get(0).getData().get(0).getYValue().intValue() + 1);
						getControl().getData().get(1).getData().get(0).setYValue(getControl().getData().get(1).getData().get(0).getYValue().intValue() - 1);
					}else{
						getControl().getData().get(0).getData().get(0).setYValue(getControl().getData().get(0).getData().get(0).getYValue().intValue() - 1);
						getControl().getData().get(1).getData().get(0).setYValue(getControl().getData().get(1).getData().get(0).getYValue().intValue() + 1);
					}
				}
			});
		}
	}
	
	private class MyTextAreaObserver extends ObserverControl<TextArea> implements IObserver<Log>{

		public MyTextAreaObserver(TextArea control) {
			super(control);
		}

		@Override
		public void update(Log log) {
			
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					getControl().appendText(log.getLog() + "\n");
				}
			});
		}
	}
	
	private abstract class MyLabelObserver extends ObserverControl<Label>{

		protected MyLabelObserver(Label control) {
			super(control);
		}
	}
	
	private class MyLabelTimeObserver extends MyLabelObserver implements IObserver<LocalDateTime>{
		
		public MyLabelTimeObserver(Label control) {
			super(control);
		}

		@Override
		public void update(LocalDateTime currentTime) {
			
			
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					ZonedDateTime zonedDateTime = ZonedDateTime.of(currentTime.toLocalDate(), currentTime.toLocalTime(), ZoneId.of("Europe/Rome"));
					getControl().setText(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(zonedDateTime));
				}
			});
		}	
	}
	
	private class MyLabelNickNameObserver extends MyLabelObserver implements IObserver<RegisteredUser>{

		public MyLabelNickNameObserver(Label control) {
			super(control);
		}

		@Override
		public void update(RegisteredUser user) {
			if(user != null) getControl().setText("Ciao " + user.getNickname() + ", ");
			else getControl().setText("");
		}
		
	}
	
	private class MyTabPaneObserver extends ObserverControl<TabPane> implements IObserver<SingleForecast>{
		private BlockingQueue<IObserver<LocalDateTime>> _queue = new ArrayBlockingQueue<>(WheaterForecast.GetInstance().getForecast().size());
		
		public MyTabPaneObserver(TabPane control) {
			super(control);
		}

		@Override
		public void update(SingleForecast forecast) {
			ArrayList<Tab> list = new ArrayList<>(getControl().getTabs());
			if(getControl().getTabs().size() == WheaterForecast.GetInstance().getForecast().size()){
				Document.GetInstance().detachObserver(_queue.poll());
				list.remove(0);
				try {

					Document.GetInstance().attachObserver(_queue.peek());
				}catch(Exception ex){
					//
				}
			}
			
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					if(getControl().getTabs().size() == WheaterForecast.GetInstance().getForecast().size()){
						getControl().getTabs().clear();
						getControl().getTabs().addAll(list);
					}
					
					//inserisco la nuova previsione
					try {
						creatingNewTab(forecast);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}

		private void creatingNewTab(SingleForecast forecast) throws IOException {
			
			Tab tab = new Tab();
			IObserver<LocalDateTime> o = new MyTabObserver(tab, forecast);
			if(_queue.size() == 0) Document.GetInstance().attachObserver(o);
			
			_queue.add(o);
			getControl().getTabs().add(tab);
		}
		
		private class MyTabObserver extends ObserverControl<Tab> implements IObserver<LocalDateTime>{
			private ListView<MyHBox> _listView = new ListView<MyHBox>();
			private LocalDate _dateTab;

			public MyTabObserver(Tab control, SingleForecast forecast) throws IOException {
				super(control);
				
				_dateTab = forecast.getDate();
				//set Title of tab
				Instant instant = forecast.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
				Date res = Date.from(instant);
				getControl().setText(new SimpleDateFormat("EEEE, dd/MM/yyyy").format(res));
				
				//create list view
				ArrayList<MyHBox> list = new ArrayList<>();
				for(TimeSlot t : forecast.getForecast().keySet())
					list.add(new MyHBox(t, forecast.getForecast().get(t)));
				
		        ObservableList<MyHBox> myObservableList = FXCollections.observableList(list);
		        _listView.setItems(myObservableList);
				Collections.sort(myObservableList, new Comparator<MyHBox>(){

					@Override
					public int compare(MyHBox o1, MyHBox o2) {
						return o1.getTimeSlot().getStartTime().compareTo(o2.getTimeSlot().getStartTime());
					}
					
				});
				
				getControl().setContent(_listView);
			}

			@Override
			public void update(LocalDateTime dateTime) {
				if(dateTime.toLocalDate().isEqual(_dateTab)){
					Platform.runLater(new Runnable(){
	
						@Override
						public void run() {
							LocalTime t = dateTime.toLocalTime();
							
								for(MyHBox h : _listView.getItems()){
									h.setStyle("-fx-border-color: white;");
								}
								
								for(int i = 0; i < _listView.getItems().size(); i++){
									MyHBox h = _listView.getItems().get(i);
									if(h.getTimeSlot().contain(t)){ h.setStyle("-fx-border-color: red;"); _listView.scrollTo(i);}
								}
						}
					});
				}
			}
			
			private class MyHBox extends HBox{
				private TimeSlot _t;
				private Label _label = new Label();
				private ImageView _imageView = new ImageView();
				
				public MyHBox(TimeSlot t, Wheater w) throws IOException {
					super();
					this._t = t;
					_label.setText(t.toString());
					_label.setAlignment(Pos.CENTER);
					_label.setMaxWidth(Double.MAX_VALUE);
					HBox.setHgrow(_label, Priority.ALWAYS);
					String path =System.getProperty("user.dir");
					//System.out.println("---->"+path);
					File file = null;
					if(isNight() && w.compareTo(Wheater.Sunny) == 0) file = new File(path+"\\img\\moon.jpeg");// new File("../immagini/moon.jpeg");
					else file = new File(path+"\\img\\"+w.toString() + ".jpeg");
			        Image image = new Image(file.toURI().toString(), 100, 100, false, false);
			        _imageView.setImage(image);
			        //this.setStyle("-fx-border-color: red;");
			        
			        this.getChildren().addAll(_label, _imageView);
				}
				
				private boolean isNight(){
					if(_t.getStartTime().isAfter(LocalTime.of(19, 59)) || _t.getEndTime().isBefore(LocalTime.of(6, 0))) return true;
					return false;
				}
				
				public TimeSlot getTimeSlot(){
					return _t;
				}
			}
		}
	}

	
	private class MyButtonObserver extends ObserverControl<Button> implements IObserver<RegisteredUser>{

		protected MyButtonObserver(Button control) {
			super(control);
		}
		
		@Override
		public void update(RegisteredUser user) {
			if(user != null) getControl().setText("Logout");
			else getControl().setText("Login");
		}
	}
	
	private class MyMenuItemObserver extends ObserverControl<MenuItem> implements IObserver<RegisteredUser>{

		protected MyMenuItemObserver(MenuItem control) {
			super(control);
		}

		@Override
		public void update(RegisteredUser user) {
			if(user != null) getControl().setDisable(false);
			else getControl().setDisable(true);
		}
	}
	
	private class MyTableViewObserver extends ObserverControl<TableView<Reserving>> implements IObserver<ArrayList<Reserving>>{

		protected MyTableViewObserver(TableView<Reserving> control) {
			super(control);
		}

		@Override
		public void update(ArrayList<Reserving> reserving) {
			getControl().getItems().clear();
			for(Reserving r : reserving)
				getControl().getItems().add(r);
		}
	}
	
	private class MyStageObserver extends ObserverControl<Stage> implements IObserver<RegisteredUser>{

		protected MyStageObserver(Stage control) {
			super(control);
		}

		@Override
		public void update(RegisteredUser object) {
			getControl().close();
		}
		
	}
}
