package model;

import java.util.ArrayList;
import java.util.List;

import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;

public class Storage implements ISubject<Storage>{
	private static Storage _instance = null;
	private double _actualStorage;
	private ArrayList<IObserver<Storage>> _observer = new ArrayList<>();
	private IStoragePolicy _policy = null;
	
	private Storage() {
		_actualStorage = Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("InitialChargeStorage").getValue());
	}
	
	public static Storage GetInstance(){
		if(_instance == null) _instance = new Storage();
		return _instance;
	}

	public double getActualCharge(){
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
	
	public void chargeUpdate(){
		//qui bisogna che venga in qualche modo presa la politica da ParametersSimulation
		_actualStorage = _policy.modifyChargeStorage();
		notifyObserver();
	}
	
	public void setPolicy(IStoragePolicy policy) {
		_policy = policy;
	}
	
	public void reset() {
		_actualStorage = Integer.class.cast(ParametersSimulation.GetInstance().getInformationOfParameter("InitialChargeStorage").getValue());
		notifyObserver();
	}
	
	public List<Triple> toTriple(String parent,String predicateOfParent){
		List<Triple> ris = new ArrayList<Triple>();			
		String s = Ontology.APP_NS+ "BuildingBattery";
		ris.add(new Triple(parent,predicateOfParent,s));
		ris.add(new Triple(s,Ontology.vtg_actualCharge,Double.toString(_actualStorage)));
	return ris;
	}
	
	public String getTripleScreenSubject(String timestamp){
		return "Storage_"+timestamp;
	}
}
