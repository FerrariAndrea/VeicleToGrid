package persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Document;
import model.Parking;
import model.Storage;
import model.WheaterForecast;

public class VTDSibConnector {

	private static int  MAX_RATE =150;
	public static void saveSnap() throws Exception {
		KPConnector.GetInstance().join();
		String nameSpace = "http://veicletogrid/"+Document.GetInstance().getTime().toString()+"#";
		List<Triple> triples = new ArrayList<Triple>();
		triples.addAll(Parking.GetInstance().toTriple(nameSpace));
		triples.addAll(WheaterForecast.GetInstance().toTriple(nameSpace));
		triples.addAll(Storage.GetInstance().toTriple(nameSpace));
		KPConnector.GetInstance().insert(triples,MAX_RATE);
		//ParkingSpace	
		//Parking  				*
		//Reserving
		//SingleForecast
		//WheaterForecast   	*
		//Storage 				*
	}
	
	public static List<Triple> getSnap() throws Exception{
			KPConnector.GetInstance().join();
		return KPConnector.GetInstance().query(null, null, null, null, null);
	}
	public static void stampSnap() throws Exception{
		KPConnector.GetInstance().join();
		for (Iterator<Triple> i = 	KPConnector.GetInstance().query(null, null, null, null, null).iterator(); i.hasNext();) {	
			System.out.print(i.next().toString());
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Output SIB:");
		try {
			stampSnap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
