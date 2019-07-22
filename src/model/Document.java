package model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import persistence.VTDSibConnector;
import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;
import utils.Utilities;

public class Document implements ISubject<LocalDateTime>{
	private ArrayList<RegisteredUser> _registeredUser;
	private static Document _instance = null;
	private LocalDateTime _currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);	//tempo corrente applicazione
	private boolean _exitApplication = false;
	private boolean _startApplication = false;
	private Step _step;
	private ArrayList<IObserver<LocalDateTime>> _observer = new ArrayList<>();
	private int _sleepTimeStep = 500;
	private String _nameSpace = "http://veicletogrid";
	private Document(){
		_registeredUser = new ArrayList<>();
		_step = new Step();
		_registeredUser.add(new RegisteredUser("ciao", "ciao", "pluto"));
	}
	
	public static Document GetInstance(){
		if(_instance == null) _instance = new Document();
		return _instance;
	}
	
	public ArrayList<RegisteredUser> getRegisteredUser(){
		return _registeredUser;
	}
	
	public LocalDateTime getTime(){
		return _currentTime;
	}
	
	public void updateTime(){
		_currentTime = _currentTime.plusMinutes(1);
		notifyObserver();
	}
	public String get_nameSpace() {
		return _nameSpace;
	}

	public void set_nameSpace(String _nameSpace) {
		this._nameSpace = _nameSpace;
	}

	public boolean isExit(){
		return _exitApplication;
	}
	
	public boolean isStart(){
		return _startApplication;
	}
	
	public int getSleepTimeStep(){
		return _sleepTimeStep;
	}
	
	public void setSleepTimeStep(int step){
		this._sleepTimeStep = step;
	}
	
	//metodo per avviare l'applicazione
    public void start(){
    	_startApplication = true;
    	IGeneratorEvent g = RandomGeneratorFactory.CreateGeneratorEntryEvent();
    	Thread entryCostumer = new Thread(g);
    	entryCostumer.start();
    	g = RandomGeneratorFactory.CreateGeneratorWheaterEvent();
    	Thread wheaterForecast = new Thread(g);
    	wheaterForecast.start();
    	Thread step = new Thread(_step);
		step.start();
	}
    
    //metodo per mettere in pausa l'applicazione
    public void pause(){
    	_step.requestPause();
    	saveOnSIB();
    }
    
    //metodo per riattivare l'applicazione dopo una pausa
    public void resumePause(){
    	_step.getSemaphore().release();
    }
	
    //metodo per terminare l'applicazione
    public void exit(){
    	if(_step.isInPause()) resumePause();
		_exitApplication = true;
		if(CurrentSession.GetInstance().getRegisteredUser() != null) CurrentSession.GetInstance().Logout();
	}

	@Override
	public void attachObserver(IObserver<LocalDateTime> observer) {
		observer.update(_currentTime);
		_observer.add(observer);
	}

	@Override
	public void detachObserver(IObserver<LocalDateTime> observer) {
		_observer.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for(IObserver<LocalDateTime> o : _observer)
			o.update(_currentTime);
	}
	
	public void saveOnSIB(){
		try {
			VTDSibConnector.saveSnap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadFromSIB(){
		/*
		try {
			VTDSibConnector.stampSnap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public List<Triple> toTriple(String app_ns){
		String s = Ontology.APP_NS+ "SimulationScreenInstance";
		List<Triple> ris = new ArrayList<Triple>();
		ris.add(new Triple(s,Ontology.rdf_type,Ontology.vtg_SimulationScreen));
		ris.add(new Triple(s,Ontology.vtg_chargingSpeedStorageByEnel,ConstantProject.chargingSpeedStorageByEnel+""));
		ris.add(new Triple(s,Ontology.vtg_chargingVehiclesSpeed,ConstantProject.chargingVehiclesSpeed+""));
		ris.add(new Triple(s,Ontology.vtg_initialChargteStorage,ConstantProject.InitialChargeStorage+""));
		ris.add(new Triple(s,Ontology.vtg_maxChargeStorageCapacity,ConstantProject.maxChargeStorageCapacity+""));
		ris.add(new Triple(s,Ontology.vtg_maxChargeVehicleStorage,ConstantProject.maxChargeVehicleStorage+""));
		
		ris.add(new Triple(s,Ontology.maxDurationCarPark,ConstantProject.maxDurationCarPark+""));
		ris.add(new Triple(s,Ontology.vtg_minDurationCarPark,ConstantProject.minDurationCarPark+""));
		ris.add(new Triple(s,Ontology.vtg_maxVehicleCapacityOnArrive,ConstantProject.maximumVehicleCapacityWhenArriveToParkingSpace+""));
		ris.add(new Triple(s,Ontology.vtg_minTimeToNowForReserving,ConstantProject.minTimeToNowForReserving+""));
		ris.add(new Triple(s,Ontology.vtg_numberParkingSpace,ConstantProject.numberNormalParkingSpace+""));
		ris.add(new Triple(s,Ontology.vtg_timeStamp, Utilities.getTimeStamp(getTime())));
		
		
		//public static String vtg_hasForecast ="vtg:hasForecast"; //---------da fare
		//public static String vtg_hasParking ="vtg:hasParking";//---------da fare
		//public static String vtg_hasStore ="vtg:hasStore";//---------da fare
		//public static String vtg_hasUser ="vtg:hasUser";//---------da fare

	
		
		return ris;
	}
	
	
}
