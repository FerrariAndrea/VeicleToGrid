package model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import persistence.VTDSibConnector;
import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;
import utils.Utilities;

public class Document implements ISubject<LocalDateTime>{
	private ArrayList<RegisteredUser> _registeredUser;
	private static Document _instance = null;
	private LocalDateTime _initialTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
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
    	if(_step.isInPause())_step.getSemaphore().release();
    }
	
    public boolean isInPause() {
    	return _step.isInPause();
    }
    
    //metodo per terminare l'applicazione
    public void exit(){
    	_exitApplication = true;
    	if(_step.isInPause()) resumePause();
    	if(Document.GetInstance().isStart() && !isInPause()) saveOnSIB();
		ContainerEvent.GetInstance().exit();
	}
    
    //metodo per resettare il simulatore
    public void reset() {
    	//fermo il tempo e metto in pausa
    	pause();
    	
    	//resetto il tempo
    	_currentTime = _initialTime;
    	notifyObserver();
    	
    	//resetto le previsioni del tempo
    	WheaterForecast.GetInstance().reset();
    	
    	//reset dello storage
    	Storage.GetInstance().reset();
    	
    	//reset dei parcheggi e delle prenotazioni
    	Parking.GetInstance().reset();
    	
    	//reset semafori ed eventi
    	ContainerEvent.GetInstance().reset();
    	
    	//faccio ripartire i nuovi generatori
    	IGeneratorEvent g = RandomGeneratorFactory.CreateGeneratorEntryEvent();
    	Thread entryCostumer = new Thread(g);
    	entryCostumer.start();
    	g = RandomGeneratorFactory.CreateGeneratorWheaterEvent();
    	Thread wheaterForecast = new Thread(g);
    	wheaterForecast.start();
    	
    	//esco da un eventuale sessione corrente
    	CurrentSession.GetInstance().reset();
    	
    	//reset dei parametri
    	ParametersSimulation.GetInstance().resetAllParameters();
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
	
	public List<Triple> toTriple(){
		String s = Ontology.APP_NS+ "SimulationScreenInstance";
		List<Triple> ris = new ArrayList<Triple>();
		ris.add(new Triple(s,Ontology.rdf_type,Ontology.vtg_SimulationScreen));
		ris.add(new Triple(s,Ontology.vtg_chargingSpeedStorageByEnel,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("chargingSpeedStorageByEnel").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_chargingVehiclesSpeed,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("chargingVehiclesSpeed").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_initialChargteStorage,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("InitialChargeStorage").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_maxChargeStorageCapacity,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("maxChargeStorageCapacity").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_maxChargeVehicleStorage,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("maxChargeVehicleStorage").getValue())+""));		
		ris.add(new Triple(s,Ontology.vtg_maxDurationCarPark,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("maxDurationCarPark").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_minDurationCarPark,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("minDurationCarPark").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_maxVehicleCapacityOnArrive,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("maximumVehicleCapacityWhenArriveToParkingSpace").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_minTimeToNowForReserving,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("minTimeToNowForReserving").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_numberParkingSpace,Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("numberNormalParkingSpace").getValue())+""));
		ris.add(new Triple(s,Ontology.vtg_timeStamp, Utilities.getTimeStamp(getTime())));
		
		ris.addAll(WheaterForecast.GetInstance().toTriple(s, Ontology.vtg_hasForecast));
		
		ris.addAll(Parking.GetInstance().toTriple(s, Ontology.vtg_hasParking));
		
		ris.addAll(Storage.GetInstance().toTriple(s, Ontology.vtg_hasStore));
		
		
		for (Iterator<RegisteredUser> i = _registeredUser.iterator(); i.hasNext();) {	
			
			ris.addAll(i.next().toTriple(s, Ontology.vtg_hasUser));
			
		}

	
		
		return ris;
	}
	
	
}
