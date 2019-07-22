package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.scene.paint.Color;
import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;

public abstract class ParkingSpace implements ISubject<ParkingSpace>{
	private int _ID;
	private NavigableSet<Reserving> _reserving;
	private StateParkingSpace _state = new Free();
	private Lock _lockReserving = new ReentrantLock();
	private ArrayList<IObserver<ParkingSpace>> _observer;
	
	protected ParkingSpace(int ID){
		this._ID = ID;
		_reserving = new TreeSet<>();
		_observer = new ArrayList<>();
	}
	
	//usato allo start dell'applicazione per caricare tutte le prenotazioni presenti nel SIB
	public void insertAllReserving(ArrayList<Reserving> reserving){
		for(Reserving r : reserving){
			if(r.getStartTimeReserving().isAfter(Document.GetInstance().getTime())) _reserving.add(r);
		}
	}
	
	//ti dice se il posteggio è libero in questo momento
	public boolean isFree(){
		if(! _state.isFree()) return false;
		return true;
	}
	
	public abstract Color getColor();
	
	public void makeFree(){
		_state = _state.makeFree();
		notifyObserver();
	}
	
	public void makeBusy(int actualVehicleStorage){
		_state = _state.makeBusy(actualVehicleStorage);
		notifyObserver();
	} 
	
	public double getActualVehicleStorage(){
		return _state.getActualVehicleStorage();
	}
	
	public void updateVehicleStorage(){
		_state.updateVehicleStorage();
		notifyObserver();
	}
	
	public SortedSet<Reserving> getReserving(){
		return _reserving;
	}
	
	public int getID(){
		return _ID;
	}
	
	@Override
	public String toString(){
		return "" +this._ID;
	}
	
	@Override
	public void attachObserver(IObserver<ParkingSpace> observer){
		_observer.add(observer);
		notifyObserver();
	}
	
	@Override
	public void detachObserver(IObserver<ParkingSpace> observer){
		_observer.remove(observer);
	}
	
	@Override
	public void notifyObserver(){
		for(IObserver<ParkingSpace> o : _observer){
			if(Thread.currentThread().getStackTrace()[2].getMethodName().equals("attachObserver") && o.getClass().getName().contains("MyChartParkingObserver")) continue;
			if((Thread.currentThread().getStackTrace()[2].getMethodName().equals("updateVehicleStorage") && 
					o.getClass().getName().contains("MyProgressBarParkingSpaceObserver")) || 
					!Thread.currentThread().getStackTrace()[2].getMethodName().equals("updateVehicleStorage")) o.update(this);
		}
		
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null) return false;
		if(obj == this) return false;
		if(! (obj instanceof ParkingSpace)) return false;
		
		ParkingSpace other = (ParkingSpace)obj;
		if(other.getID() == this._ID) return true;
		return false;
	}
	
	public boolean insertReserving(RegisteredUser user, LocalDateTime startTimeReserving, LocalDateTime endTimeReserving, int minCharge ){	
		_lockReserving.lock();
		
		//controllo se il posteggio è attualmente occupato da un utente randomico, se la prenotazione è entro le otto ore 
		//(tempo massimo di posteggio) da quanto il posteggio è stato occupato non posso accettarla
		if(!isFree() && (_reserving.size() == 0 || ( _reserving.size() > 0 && _reserving.first().getStartTimeReserving().isAfter(Document.GetInstance().getTime()) ) )){
			Duration d = Duration.between(_state.getStartOccupation(), startTimeReserving);
			boolean entroLeOttoOre = d.toMinutes() < 480 ? true : false;
			if(entroLeOttoOre){ _lockReserving.unlock(); return false;}
		}
		
		if(_reserving.size() == 0) {_reserving.add(new Reserving(user, startTimeReserving, endTimeReserving, minCharge, this)); _lockReserving.unlock(); return true;}
		
		//controllo se la nuova prenotazione è precedente a tutte quelle inserite, se è così la inserisco
		if(_reserving.first().getStartTimeReserving().isAfter(endTimeReserving)) { _reserving.add(new Reserving(user, startTimeReserving, endTimeReserving, minCharge, this)); _lockReserving.unlock(); return true;}
		
		Iterator<Reserving> it = _reserving.tailSet(_reserving.first(), false).iterator();
		while(it.hasNext()){
			Reserving rNext = it.next();
			if(_reserving.lower(rNext).getEndTimeReserving().isBefore(startTimeReserving) && rNext.getStartTimeReserving().isAfter(endTimeReserving)){
				_reserving.add(new Reserving(user, startTimeReserving, endTimeReserving, minCharge, this));
				_lockReserving.unlock();
				return true;
			}
		}
		
		//controllo se la nuova prenotazione è successiva a tutte quelle inserite, se è così la inserisco
		if(_reserving.last().getEndTimeReserving().isBefore(startTimeReserving)) {_reserving.add(new Reserving(user, startTimeReserving, endTimeReserving, minCharge, this)); _lockReserving.unlock(); return true;}
		
		_lockReserving.unlock();
		return false;
	}
	
	//verifica se lo stato è da aggiornare a causa dell'inizio di una prenotazione
	public void verififyIsToUpdate(){
		if(_reserving.size() > 0 && _reserving.first().getStartTimeReserving().isBefore(Document.GetInstance().getTime()) && _reserving.first().getEndTimeReserving().isAfter(Document.GetInstance().getTime()) && isFree()){
			Random random = new Random();
			makeBusy(random.nextInt(59) +1);
			ReservingStartEvent event = new ReservingStartEvent(_reserving.first().getUser(), _reserving.first().getParkingSpace());
			ContainerEvent.GetInstance().addEvent(event);
		}
	}
	
	public boolean removeOldReserving(){
		if(_reserving.size() > 0 && _reserving.first().getEndTimeReserving().isBefore(Document.GetInstance().getTime())){
			_lockReserving.lock();
			makeFree();
			ReservingEndEvent event = new ReservingEndEvent(_reserving.first().getUser(), _reserving.first().getParkingSpace());
			_reserving.pollFirst();
			ContainerEvent.GetInstance().addEvent(event);
			_lockReserving.unlock();
			return true;
		}
		return false;
	}
	
	private class ReservingEndEvent implements IEvent{
		private RegisteredUser _user;
		private ParkingSpace _p;
		
		public ReservingEndEvent(RegisteredUser user, ParkingSpace parking){
			this._user = user;
			this._p = parking;
		}
		
		@Override
		public Log operation() {
			Log log = new Log("The registered user " + _user + "(" + _user.getNickname() + ") has left the parking space " + _p);
			return log;
		}
		
	}
	
	private class ReservingStartEvent implements IEvent{
		private RegisteredUser _user;
		private ParkingSpace _p;
		
		
		public ReservingStartEvent(RegisteredUser user, ParkingSpace parking){
			this._user = user;
			this._p = parking;
		}
		
		@Override
		public Log operation() {
			Log log = new Log("The registered user " + _user + "(" + _user.getNickname() + ") has entered thanks to his reservation in the parking space " + _p);
			return log;
		}
		
	}
	
	private abstract class StateParkingSpace{
		public abstract boolean isFree();
		public abstract StateParkingSpace makeBusy(int actualVehicleStorage);
		public abstract StateParkingSpace makeFree();
		public abstract double getActualVehicleStorage();
		public abstract void updateVehicleStorage();
		public abstract LocalDateTime getStartOccupation();
	}
	
	private class Free extends StateParkingSpace{
		
		@Override
		public boolean isFree() {
			return true;
		}

		@Override
		public StateParkingSpace makeBusy(int actualVehicleStorage) {
			return new Busy(actualVehicleStorage);
		}

		@Override
		public StateParkingSpace makeFree() {
			throw new UnsupportedOperationException("The parking space is already free");
		}

		@Override
		public double getActualVehicleStorage() {
			throw new UnsupportedOperationException("The parking space is free");
		}

		@Override
		public void updateVehicleStorage() {
			throw new UnsupportedOperationException("The parking space is free");
		}

		@Override
		public LocalDateTime getStartOccupation() {
			throw new UnsupportedOperationException("The parking space is free");
		}
	}
	
	private class Busy extends StateParkingSpace{
		private double _actualVehicleStorage;
		private LocalDateTime _startDateTime;
		
		public Busy(double actualVehicleStorage){
			this._actualVehicleStorage = actualVehicleStorage;
			_startDateTime = Document.GetInstance().getTime();
		}
		
		@Override
		public boolean isFree() {
			return false;
		}

		@Override
		public StateParkingSpace makeBusy(int actualStorageVehicle) {
			throw new UnsupportedOperationException("The parking space is already busy");
		}

		@Override
		public StateParkingSpace makeFree() {
			return new Free();
		}
		
		@Override
		public double getActualVehicleStorage(){
			return _actualVehicleStorage;
		}

		@Override
		public void updateVehicleStorage() {
			double newVehicleStorage = _actualVehicleStorage + (ConstantProject.chargingVehiclesSpeed/60.0);		//KW to Wmin
			if(newVehicleStorage > ConstantProject.maxChargeVehicleStorage) _actualVehicleStorage = ConstantProject.maxChargeVehicleStorage;
			else _actualVehicleStorage = newVehicleStorage;
		}

		@Override
		public LocalDateTime getStartOccupation() {
			return _startDateTime;
		}
		
	
	}
	public List<Triple> toTriple(String parent,String predicateOfParent){
		List<Triple> ris = new ArrayList<Triple>();
		String s = Ontology.APP_NS+ getTripleSubject();		
		ris.add(new Triple(parent,predicateOfParent,s));
		
		
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.HasScreen,screen,this.getClass().getName(),this.getClass().getName()));
		ris.add(new Triple(nameSpace,screen,Ontology.Is,Boolean.toString(this.isFree()),this.getClass().getName(),Boolean.class.getName()));
		for (Iterator<Reserving> i = this._reserving.iterator(); i.hasNext();) {	
			Reserving temp = i.next();
			ris.addAll(temp.toTriple(nameSpace,timestamp));
			ris.add(new Triple(nameSpace,screen,Ontology.HasReserving,temp.getTripleSubject(),this.getClass().getName(),temp.getClass().getName()));			
		}	
		try{
			ris.add(new Triple(nameSpace,screen,Ontology.HasVeicleStorage,Double.toString(this.getActualVehicleStorage()),this.getClass().getName(),Integer.class.getName()));
		}catch(Exception e) {
			//e
		}
		return ris;
	}
	
	public String getTripleSubject(){
		return "ParkingSpace_"+this._ID;
	}
}
