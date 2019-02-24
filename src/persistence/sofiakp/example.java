package persistence.sofiakp;

import java.util.Vector;

public class example {
	public static void main(String[] args)
	{
		/*
		KPICore kp = new KPICore("localhost",10111,"Prova");
		kp.enable_debug_message();
		kp.enable_error_message();
		//kp.openConnect();
		kp.join();
		System.out.println(kp.querySPARQL("INSERT INTO <#book4> <http://purl.org/dc/elements/1.1/title> 'SPARQL 1.0 Tutorial'"));

		System.out.println(kp.querySPARQL("SELECT ?titolo ?autore ?anno"));
		 */

		KPICore kp;  //direct interface with the SIB
		SIBResponse resp; // The class representing SIB response
		String SIB_Host = "localhost";
		int SIB_Port = 10111;
		String SIB_Name = "X";

		kp = new KPICore(SIB_Host, SIB_Port, SIB_Name);
		System.out.println("Connected!");
		//Remove debug and error print
		//kp.disable_debug_message();
		//kp.disable_error_message();
		//enable debug
		//kp.enable_debug_message();
		//kp.enable_error_message();

		//resp represents the SIB response
		resp = kp.join();
		if(!resp.isConfirmed()) {
			System.err.println ("Error joining the SIB");
		}
		else {

			System.out.println ("SIB joined correctly");
		}

		Vector<String> triple; 
		// that are lists of triple
		Vector<Vector<String>> triples = new Vector<Vector<String>>();

		//create 5 example triples that has http://examplens# as name space 
		String ns = "http://examplens#";
		for(int i = 0; i < 5; i++){
			triple = new Vector<String>();
			triple.add(ns+ "subject_" + i);
			triple.add(ns+ "predicate_" + i);
			triple.add(ns+ "object_" + i);
			triple.add("uri");
			triple.add("uri");
			triples.add(triple);
		}
/*
		resp = kp.insert(triples);
		if(!resp.isConfirmed()) {

			System.err.println("Error inserting into the SIB");
		}else {

			System.out.println("triples_ins inserted into the SIB");
		}
*/
		resp=kp.queryRDF (ns+ "subject_1", SSAP_XMLTools.ANYURI, null, "uri","uri");		
		if(!resp.isConfirmed()) {
			System.err.println ("Error during RDF-M3 query");
		}else{
			System.out.println("\nPrinting RDF-M3 query results:\n");
		}
		triples = resp.query_results;
		if (triples != null){
			for(int i=0; i<triples.size() ; i++ ){
				Vector<String> t=triples.get(i);
				System.out.println(" S:["+t.get(0)
						+"] P:["+t.get(1)
						+"] O:["+t.get(2)
						+"] Otype:["+t.get(3)+"]");

			}
		}
	}
}
