package model;

import java.util.ArrayList;
import java.util.List;

import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;

public class Storage implements ISubject<Storage>{
	private static Storage _instance = null;
	private double _actualStorage = ConstantProject.InitialChargeStorage;
	private ArrayList<IObserver<Storage>> _observer = new ArrayList<>();
	private IStoragePolicy _policy = null;
	
	public static Storage GetInstance(){
		if(_instance == null) _instance = new Storage();
		return _instance;
	}

	public double getActualCharge(){
		return _actualStorage;
	}
	
	public void setStoragePolicy(IStoragePolicy policy){
		_policy = policy;
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
	
	public void chargeUpdate(){
		_actualStorage = _policy.modifyChargeStorage();
		notifyObserver();
	}
	
	public List<Triple> toTriple(String nameSpace,String timestamp){
		List<Triple> ris = new ArrayList<Triple>();			
		ris.add(new Triple(nameSpace,"Storage",Ontology.HasScreen,getTripleScreenSubject(timestamp),this.getClass().getName(),this.getClass().getName()));
		ris.add(new Triple(nameSpace,getTripleScreenSubject(timestamp),Ontology.Is,Double.toString(_actualStorage),this.getClass().getName(),Float.class.getName()));
		//ris.add(new Triple(nameSpace,"Storage",Ontology.Is,Float.toString(InitialChargeStorage)));
		return ris;
	}
	public String getTripleScreenSubject(String timestamp){
		return "Storage_"+timestamp;
	}
}
