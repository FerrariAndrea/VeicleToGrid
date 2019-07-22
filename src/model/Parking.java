package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;

public class Parking implements ISubject<ArrayList<Reserving>>{
	private static Parking _instance;
	private ArrayList<ParkingSpace> _parkingSpace;
	private ArrayList<IObserver<ArrayList<Reserving>>> _observer;
	
	private Parking(){
		_parkingSpace = new ArrayList<>();
		_observer = new ArrayList<>();
		NormalParkingSpaceFactory factory = new NormalParkingSpaceFactory();
		
		for(int i = 0; i < ConstantProject.numberNormalParkingSpace; i++){
			_parkingSpace.add(factory.CreateParkingSpace(i));
		}
		//this.createNewReserving(new RegisteredUser("ciao", "ciao", "pluto"), LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 41)), LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 41)), 30);
	}
	
	public static Parking GetInstance(){
		if(_instance == null) _instance = new Parking();
		return _instance;
	}
	
	public ArrayList<ParkingSpace> getParkingSpace(){
		return _parkingSpace;
	}
	
	public int numberOfFreeParkingSpace(){
		int result = 0;
		
		for(ParkingSpace parkingSpace : _parkingSpace){
			if(parkingSpace.isFree()) result ++;
		}
		
		return result;
	}
	
	public ArrayList<Reserving> getAllReservingOfRegisteredUser(RegisteredUser user){
		ArrayList<Reserving> result = new ArrayList<>();
		
		for(ParkingSpace p : _parkingSpace){
			for(Reserving r : p.getReserving()){
				if(r.getUser().equals(user)) result.add(r);
			}
		}
		
		return result;
	}
	
	public boolean createNewReserving(RegisteredUser user, LocalDateTime startTimeReserving, LocalDateTime endTimeReserving, int minCharge){
		if(startTimeReserving.isBefore(Document.GetInstance().getTime())) throw new IllegalArgumentException("The start time of reserving is before the actual time");
		if(startTimeReserving.isAfter(endTimeReserving)) throw new IllegalArgumentException("The end time of reserving is before the start time");
		Duration duration = Duration.between(startTimeReserving, endTimeReserving);
		if(duration.toMinutes() < ConstantProject.minDurationCarPark || duration.toMinutes() > ConstantProject.maxDurationCarPark) throw new IllegalArgumentException("The reserving must be between one and eight hours");
		
		if(Duration.between(Document.GetInstance().getTime(), startTimeReserving).toMinutes() < ConstantProject.minTimeToNowForReserving) throw new IllegalArgumentException("There must be at least half an hour between the reservation and the current time");
		
		for(ParkingSpace p : _parkingSpace){
			if(p.insertReserving(user, startTimeReserving, endTimeReserving, minCharge)) { notifyObserver(); return true; }
		}
		
		return false;
	}
	
	public void updateParkingSpaceDueToReserving(){
		
		for(ParkingSpace p : _parkingSpace)
			p.verififyIsToUpdate();
	}
	
	public ParkingSpace occupyParkingSpace(int actualVehicleStorage){
		
		ParkingSpace p = findParkingSpaceWithMostTimeFree();
		if(p != null) p.makeBusy(actualVehicleStorage);
		
		return p;
	}
	
	private ParkingSpace findParkingSpaceWithMostTimeFree(){
		long best = ConstantProject.minDurationCarPark;
		
		for(ParkingSpace p : _parkingSpace){
			if(p.isFree())
				if(p.getReserving().size() == 0) return p;
				else{
					Duration d = Duration.between(Document.GetInstance().getTime(), p.getReserving().first().getStartTimeReserving());
					if(d.toMinutes() > best) best = d.toMinutes();
				}
		}
		
		for(ParkingSpace p : _parkingSpace){
			if(p.isFree() && (Duration.between(Document.GetInstance().getTime(), p.getReserving().first().getStartTimeReserving()).toMinutes() == best))
				return p;
		}
		
		//tutti i parcheggi sono occupati o comunque non hanno nemmeno un ora libera
		return null;
	}
	
	public void removeOldRserving(){
		for(ParkingSpace p : _parkingSpace)
			if(p.removeOldReserving()) notifyObserver();
	}

	@Override
	public void attachObserver(IObserver<ArrayList<Reserving>> observer) {
		_observer.add(observer);
		notifyObserver();
	}

	@Override
	public void detachObserver(IObserver<ArrayList<Reserving>> observer) {
		_observer.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for(IObserver<ArrayList<Reserving>> o : _observer)
			o.update(getAllReservingOfRegisteredUser(CurrentSession.GetInstance().getRegisteredUser()));
	}
	

	public List<Triple> toTriple(String parent,String predicateOfParent){
		String s = Ontology.APP_NS+"Parking1";
		List<Triple> ris = new ArrayList<Triple>();
		ris.add(new Triple(parent,predicateOfParent,s));
		
		for (Iterator<ParkingSpace> i = _parkingSpace.iterator(); i.hasNext();) {			
			ris.addAll(i.next().toTriple(s,Ontology.vtg_isComposed));
		}
		return ris;
	}
}
