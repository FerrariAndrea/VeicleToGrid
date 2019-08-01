package persistence;

import java.util.Iterator;
import java.util.List;
	
import model.Document;
import model.ParametersSimulation;
import persistence.file.TripleToFile;
import persistence.sib.KPConnector;
import persistence.sib.Triple;

public class VTDSibConnector {

	private static int  MAX_RATE =150;//triple alla volta 
	
	
	
	public static void saveSnap() throws Exception {
		KPConnector.GetInstance().join();

		List<Triple> triples = Document.GetInstance().toTriple();
		String location = ParametersSimulation.GetInstance().getInformationOfParameter("Location output").getValue().toString();
		if(location.equals("SIB"))
			KPConnector.GetInstance().insert(triples,MAX_RATE);
		else
			TripleToFile.GetInstance().insert(triples);
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
