package persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Document;
import model.Parking;
import model.Storage;
import model.WheaterForecast;
import utils.Utilities;

public class VTDSibConnector {

	private static int  MAX_RATE =150;
	public static void saveSnap() throws Exception {
		KPConnector.GetInstance().join();
		String nameSpace = "http://veicletogrid/#";
		String timestamp = Utilities.getTimeStamp(Document.GetInstance().getTime());
		List<Triple> triples = new ArrayList<Triple>();
		triples.addAll(Parking.GetInstance().toTriple(nameSpace,timestamp));
		triples.addAll(WheaterForecast.GetInstance().toTriple(nameSpace,timestamp));
		triples.addAll(Storage.GetInstance().toTriple(nameSpace,timestamp));
		KPConnector.GetInstance().insert(triples,MAX_RATE);
	}
	
	public static List<Triple> getSnap() throws Exception{
			KPConnector.GetInstance().join();
		return KPConnector.GetInstance().query(null, null, null, null, null);
	}
	public static void stampSnap() throws Exception{
		KPConnector.GetInstance().join();
		for (Iterator<Triple> i = 	KPConnector.GetInstance().query(null, null, null, null, null).iterator(); i.hasNext();) {	
			System.out.println(i.next().toString());
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
