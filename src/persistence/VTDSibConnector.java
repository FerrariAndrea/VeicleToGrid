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
	private static boolean joinDone=false;
	
	
	public static void saveSnap() throws Exception {
		

		List<Triple> triples = Document.GetInstance().toTriple();
		String location = ParametersSimulation.GetInstance().getInformationOfParameter("Location output").getValue().toString();
		if(location.equals("SIB")) {
			if(!joinDone) {KPConnector.GetInstance().join();joinDone=true;}			
			KPConnector.GetInstance().insert(triples,MAX_RATE);
		}else if(location.equals("File.txt")) {
			TripleToFile.GetInstance().insert(triples);
		}else if(location.equals("Both")) {
			if(!joinDone) {KPConnector.GetInstance().join();joinDone=true;}
			KPConnector.GetInstance().insert(triples,MAX_RATE);
			TripleToFile.GetInstance().insert(triples);
		}//else nothing
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
