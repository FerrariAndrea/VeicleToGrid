package model;

import java.util.ArrayList;
import java.util.List;

import persistence.Ontology;
import persistence.Triple;
import presentation.IObserver;

public class Storage implements ISubject<Storage>{
	private static Storage _instance = null;
	public static final float InitialChargeStorage = Parking.GetInstance().getParkingSpace().size() * 4;
	private float _actualStorage = InitialChargeStorage;
	private ArrayList<IObserver<Storage>> _observer = new ArrayList<>();
	
	public static Storage GetInstance(){
		if(_instance == null) _instance = new Storage();
		return _instance;
	}

	public float getActualCharge(){
		return _actualStorage;
	}
	
	@Override
	public void attachObserver(IObserver<Storage> observer) {
		observer.update(this);
		_observer.add(observer);
	}

	@Override
	public void detachObserver(IObserver<Storage> observer) {
		_observer.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for(IObserver<Storage> o : _observer)
			o.update(this);
	}
	
	public List<Triple> toTriple(String nameSpace){
		List<Triple> ris = new ArrayList<Triple>();			
		ris.add(new Triple(nameSpace,"Storage",Ontology.Is,Float.toString(_actualStorage)));
		//ris.add(new Triple(nameSpace,"Storage",Ontology.Is,Float.toString(InitialChargeStorage)));
		return ris;
	}
}
