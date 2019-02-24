package persistence;

import java.util.ArrayList;
import java.util.List;

import model.Document;
import model.Parking;
import model.Storage;
import model.WheaterForecast;

public class VTDSibConnector {

	public static void saveSnap() throws Exception {
		KPConnector.GetInstance().join();
		String nameSpace = "http://veicletogrid/"+Document.GetInstance().getTime().toString()+"#";
		List<Triple> triples = new ArrayList<Triple>();
		triples.addAll(Parking.GetInstance().toTriple(nameSpace));
		triples.addAll(WheaterForecast.GetInstance().toTriple(nameSpace));
		triples.addAll(Storage.GetInstance().toTriple(nameSpace));
		KPConnector.GetInstance().insert(triples);
		//ParkingSpace	
		//Parking  				*
		//Reserving
		//SingleForecast
		//WheaterForecast   	*
		//Storage 				*
	}
	
	public static List<Triple> getSnap(){
			return null;
	}
	
}
