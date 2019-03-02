package utils;

import java.util.Iterator;

import model.Document;
import persistence.KPConnector;
import persistence.Ontology;
import persistence.Triple;

public class BrokerId {


	private static BrokerId _instance = null;
	public static BrokerId getInstance() {
		if(_instance==null) {_instance= new BrokerId(); }
		return _instance;
	}

	private int actualID=0;

	public BrokerId() {
		actualID = getLastFromSib();
	}
	public int getActualID() {
		return actualID;
	}


	public int getLastFromSib(){
		try {
			KPConnector.GetInstance().join();
			String nameSpace=Document.GetInstance().get_nameSpace();
			for (Iterator<Triple> i = 	KPConnector.GetInstance().query(nameSpace+"#BrokerID", nameSpace+"#"+Ontology.ActualID, null, null, null).iterator(); i.hasNext();) {	
				return Integer.parseInt(i.next().get_object());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	public void updateToSib(){
		try {
			KPConnector.GetInstance().join();
			String nameSpace=Document.GetInstance().get_nameSpace();
			KPConnector.GetInstance().insert(new Triple(nameSpace,"BrokerID", Ontology.ActualID,Integer.toString(actualID),"BrokerID",Integer.class.getName()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getNextId() {
		actualID+=1;
		return actualID;
	}
}
