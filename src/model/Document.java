package model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import persistence.VTDSibConnector;
import presentation.IObserver;

public class Document implements ISubject<LocalDateTime>{
	private ArrayList<RegisteredUser> _registeredUser;
	private static Document _instance = null;
	private LocalDateTime _currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);	//tempo corrente applicazione
	private boolean _exitApplication = false;
	private boolean _startApplication = false;
	private Step _step;
	private ArrayList<IObserver<LocalDateTime>> _observer = new ArrayList<>();
	private int _sleepTimeStep = 500;
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
	
	
}
