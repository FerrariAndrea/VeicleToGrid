package persistence.sib;

import java.util.HashMap;

public class Ontology {


	public static String Type = "type";

	
	private static Ontology _instance=null;
	
	public static Ontology getInstance() {
		if(_instance==null) {
			_instance= new Ontology();
		}
		return _instance;
	}
	
	private HashMap<String, String> _prefixMap;
	private HashMap<String, String> _prefixsRef;
	
	private Ontology() {
		_prefixMap= new HashMap<String, String> ();
		_prefixsRef= new HashMap<String, String> ();
		
		//prefix
		_prefixsRef.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns");
		_prefixsRef.put("vtg","http://www.semanticweb.org/tsg/ontologies/2019/itunibo/veicletogrid");
		
		//--------------------
		
		_prefixMap.put(Type, "rdf");
	
	}
	
	
	public String resolvePrefix(String prefix) {
		return _prefixsRef.get(prefix);
	}
	
	public String getPrefixOf(String attribute) {
		return _prefixMap.get(attribute);
	}
}


