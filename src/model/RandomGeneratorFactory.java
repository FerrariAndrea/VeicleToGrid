package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGeneratorFactory {
	public static RandomGeneratorFactory _instance = new RandomGeneratorFactory();

	public static IGeneratorEvent CreateGeneratorEntryEvent() {
		return _instance.new RandomCostumerEntryGenerator();
	}
	
	public static IGeneratorEvent CreateGeneratorExitEvent(ParkingSpace p){
		return _instance.new RandomCostumerExitGenerator(p);
	}
	
	public static IGeneratorEvent CreateGeneratorWheaterEvent(){
		return _instance.new RandomWheaterGenerator();
	}

	private class RandomCostumerEntryGenerator implements IGeneratorEvent{
		private Random _random;
		private Map<TimeSlot, ProbabilityCostumerEntry> _map;
		private final ProbabilityCostumerEntry[] _probability = {ProbabilityCostumerEntry.night, ProbabilityCostumerEntry.night, ProbabilityCostumerEntry.night, 
				ProbabilityCostumerEntry.morning, ProbabilityCostumerEntry.morning, ProbabilityCostumerEntry.morning, ProbabilityCostumerEntry.early_afternoon, 
				ProbabilityCostumerEntry.early_afternoon, ProbabilityCostumerEntry.late_afternoon, ProbabilityCostumerEntry.evening, ProbabilityCostumerEntry.evening, ProbabilityCostumerEntry.night};
		
		public RandomCostumerEntryGenerator(){
			_map = new HashMap<>();
			_random = new Random();
			for(int i = 0; i < 12; i++){
				LocalTime start = LocalTime.of(2*i, 0);
				LocalTime end = null;
				if(i != 11) end = LocalTime.of((i+1)*2, 0).minusMinutes(1);
				else end = LocalTime.of(23, 59);
				_map.put(new TimeSlot(start, end),  _probability[i]);
			}
		}
		
		@Override
		public void run() {
			while(!Document.GetInstance().isExit()){				
				try {
					//ottengo l'attuale probabilità che si presenti un cliente in base al tempo attuale
					double probability = 0;
					for(TimeSlot t : _map.keySet())
						if(t.contain(Document.GetInstance().getTime().toLocalTime())) probability = _map.get(t).getValue();
					
					if(_random.nextDouble() <= probability){
						//l'evento si verificherà, lo creo
						ParkingCostumerEntryEvent event = new ParkingCostumerEntryEvent(_random.nextInt(59) + 1);
						
						//aggiungo l'evento nel ContainerEvent per poter poi essere schedulato
						ContainerEvent.GetInstance().addEvent(event);
					}
					
					//attendo un tempo variabili prima di generare un nuovo evento, sospendendomi su un semaforo privato
					int timeToSleep = _random.nextInt(9) + 1;
					MySemaphore sem = new MySemaphore(0, Document.GetInstance().getTime().plusMinutes(timeToSleep));
					ContainerEvent.GetInstance().addSemaphore(sem);
					sem.acquire();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private class TimeSlot{
			private LocalTime _start;
			private LocalTime _end;
			
			public TimeSlot(LocalTime start, LocalTime end){
				this._start = start;
				this._end = end;
			}
			
			public boolean contain(LocalTime time){
				if(_start.isBefore(time) && _end.isAfter(time) ) return true;
				return false;
			}
		}
	}
	
	private class ParkingCostumerEntryEvent implements IEvent{
		private int _carStorage;
		
		public ParkingCostumerEntryEvent(int carStorage){
			this._carStorage = carStorage;
		}
		
		@Override
		public Log operation() {
			Log log = null;
			
			//dovrà verificare se ci sono posti disponibili
			if(Parking.GetInstance().numberOfFreeParkingSpace() > 0){ 
				
				//ci sono posti disponibili quindi provo ad occuparne uno (deve avere almeno un ora libera)
				ParkingSpace p = Parking.GetInstance().occupyParkingSpace(_carStorage); 
				if(p != null){
					//è disponibile un parcheggio
					log = new Log("A new costumer without reserving has entered in the parking. His car have " + _carStorage);
					
					//devo creare il thread che si occuperà di liberare il parcheggio dopo x tempo arbitrario
					IGeneratorEvent randomExit = RandomGeneratorFactory.CreateGeneratorExitEvent(p);
					
					//lo metto in esecuzione
					Thread t = new Thread(randomExit);
					t.start();
				}else log = new Log("There aren't free parking space now, the costumer cannot entry");
				
			}
			else log = new Log("There aren't free parking space now, the costumer cannot entry");
			
			return log;
		}
		
	}
	
	private class RandomWheaterGenerator implements IGeneratorEvent{
		private Random _random;
		
		public RandomWheaterGenerator(){
			_random = new Random();
		}
		
		@Override
		public void run() {
			while(!Document.GetInstance().isExit()){
				try {
					//mi sospendo su un semaforo privato per aspettare che passi una giornata
					MySemaphore sem = new MySemaphore(0, Document.GetInstance().getTime().truncatedTo(ChronoUnit.DAYS).plusDays(1).plusMinutes(1));
					ContainerEvent.GetInstance().addSemaphore(sem);
					sem.acquire();
					
					if(!Document.GetInstance().isExit()){
					
						//genero la previsione
						Wheater[] wheater = new Wheater[12];
						for(int i = 0; i < 12; i++){
							wheater[i] = Wheater.valueOf(_random.nextInt(3));
						}
						
						//creo l'evento
						WheaterEvent event = new WheaterEvent(wheater, Document.GetInstance().getTime().toLocalDate().plusDays(WheaterForecast.GetInstance().getForecast().size() - 1));
						
						//aggiungo l'evento nel ContainerEvent per poter poi essere schedulato
						ContainerEvent.GetInstance().addEvent(event);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class WheaterEvent implements IEvent{
		private Wheater[] _forecast;
		private LocalDate _date;

		public WheaterEvent(Wheater[] forecast, LocalDate date){
			this._forecast = forecast;
			this._date = date;
		}
		
		@Override
		public Log operation() {
			WheaterForecast.GetInstance().addNewForecast(_forecast, _date);
			return new Log("Add new Forecast!");
		}
	}
	
	private class RandomCostumerExitGenerator implements IGeneratorEvent{
		private ParkingSpace _p;
		
		public RandomCostumerExitGenerator(ParkingSpace parkingSpace){
			this._p = parkingSpace;
		}
		
		@Override
		public void run() {
			try {
				//calcolo il tempo arbitrario dopo il quale dovrà generare l'evento di uscita del cliente
				long maxTime = Parking._maxDurationCarPark;
				if(_p.getReserving().size() > 0) maxTime = Duration.between(Document.GetInstance().getTime(), _p.getReserving().first().getStartTimeReserving()).toMinutes();
				if(maxTime > Parking._maxDurationCarPark) maxTime = Parking._maxDurationCarPark;
				long time = ThreadLocalRandom.current().nextLong(Parking._minDurationCarPark -1 , maxTime) + 1;
				
				//mi sospendo su un semaforo privato
				MySemaphore sem = new MySemaphore(0, Document.GetInstance().getTime().plusMinutes(time));
				ContainerEvent.GetInstance().addSemaphore(sem);
				sem.acquire();
				
				if(!Document.GetInstance().isExit()){
				
					//creo l'evento
					ParkingCostumerExitEvent event = new ParkingCostumerExitEvent(_p);
					
					//aggiungo l'evento nel ContainerEvent per poter poi essere schedulato
					ContainerEvent.GetInstance().addEvent(event);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ParkingCostumerExitEvent implements IEvent{
		private ParkingSpace _parkingSpace;
		
		public ParkingCostumerExitEvent(ParkingSpace parking){
			this._parkingSpace = parking;
		}
		
		@Override
		public Log operation() {
			//ottengo la carica finale del veicolo e libero il posteggio
			int storage = _parkingSpace.getActualVehicleStorage();
			_parkingSpace.makeFree();
			
			//fornisco il log in uscita
			Log log = new Log("A costumer without reserving  has just left his parking space. His car had " + storage + " of charge");
			return log;
		}
	}
}
