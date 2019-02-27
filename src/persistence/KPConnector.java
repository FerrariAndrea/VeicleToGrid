package persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import persistence.sofiakp.*;


public class KPConnector {
	
	//-----------------default setting
	private static String default_sib_name = "VeicleToGridProgect";
	private static String default_host = "localhost";
	private static int default_port =10111;
	private static String defaultSubjectType = "uri";

	
	//-----------------Istance of singleton
	private static KPConnector _instance=null;	
	public static KPConnector GetInstance(String host,int port,String sib_name){
		if(_instance == null) _instance = new KPConnector(host,port,sib_name);
		return _instance;
	}
	public static KPConnector GetInstance(){
		if(_instance == null) _instance = new KPConnector(default_host,default_port,default_sib_name);
		return _instance;
	}
	
	//-----------------KPConnector
	private KPICore _kp; 
	private boolean _debug;
	private boolean _joined=false;
	private KPConnector(String host,int port,String sib_name){
		_joined=false;
		_kp = new KPICore(host, port, sib_name);
		enableDebug(false);
		
	}
	
	
	
	public void enableDebug(boolean enable) {
		_debug=enable;
		if(enable) {
			_kp.enable_debug_message();
			_kp.enable_error_message();
		}else {
			_kp.disable_debug_message();
			_kp.disable_error_message();
		}
	}
	public boolean isDebugEnable() {return _debug;}

	public void join() throws Exception {
		if(!_joined) {
			forceJoin();
			_joined=true;
		}
	}
	
	public void forceJoin() throws Exception {
		SIBResponse resp= _kp.join();
		if(!resp.isConfirmed()) {
			throw new Exception("Error joining the SIB:" + resp.Message);
			}
		else {
			if(_debug) {
				System.out.println ("SIB joined correctly");
			}
		}
	}
	
	
	public void insert(Vector<Vector<String>> triples) throws Exception {
		SIBResponse resp = _kp.insert(triples);
		if(!resp.isConfirmed()) {
			throw new Exception("Error inserting into the SIB:" + resp.Message);
		}else {
			if(_debug) {
				System.out.println("triples_ins inserted into the SIB");
			}
		}
	}
	
	public void insert(List<Triple> t,int maxStock) throws Exception {
		Vector<Vector<String>> triples = new Vector<Vector<String>>();
		for(int i = 0;i<t.size();i++) {		
			triples.add(t.get(i).toVector());
			if(maxStock>0 && triples.size()>=maxStock) {
				insert(triples);
				triples = new Vector<Vector<String>>();
			}
		}
		if(triples.size()>0) {
			insert(triples);
		}
	}
	
	public void insert(List<Triple> t) throws Exception {
		insert(t,0);
	}
	
	public List<Triple> query(String subject, String predicate, String object, String typeSubject, String typeObject){
		SIBResponse resp=_kp.queryRDF(subject, predicate, object, typeSubject, typeObject);		
		if(!resp.isConfirmed()) {
			if(_debug) {
				System.err.println ("Error during RDF-M3 query");
			}
		}else{
			if(_debug) {
				System.out.println("RDF-M3 query success");
			}
			return getFrom(resp.query_results);
		}	
		return new ArrayList<Triple>();
	}
	
	public List<Triple> querySelect_S_P_O_Where(String query){
		if(query.toLowerCase().startsWith("query")) {
			//	throw new Exception("Not valid query, the query already start as 'query ?s ?p ?o where', you need just add the where definition.");
		}
		List<Triple> ris = new ArrayList<Triple>();
		SIBResponse resp=_kp.querySPARQL(query);		
		if(!resp.isConfirmed()) {
			if(_debug) {
				System.err.println ("Error during querySPARQL");
			}
		}else{
			if(_debug) {
				System.out.println("querySPARQL success");
			}
	
		for(int i =0;i<resp.sparqlquery_results.getResults().size();i++) {
			
				String s_type = resp.sparqlquery_results.getRow(i).get(0)[1];
				String o_type = resp.sparqlquery_results.getRow(i).get(2)[1];
				String s = resp.sparqlquery_results.getRow(i).get(0)[2];
				String o = resp.sparqlquery_results.getRow(i).get(2)[2];
				String p = resp.sparqlquery_results.getRow(i).get(1)[2];				
				String nameSpace ="";
				String temp[] = s.split("#");
				if(temp.length>1) {
					nameSpace = temp[0];
					s = temp[1];
					p = p.split("#")[1];
					o = o.split("#")[1];
				}
				ris.add(new Triple(nameSpace,s,p,o,s_type,o_type));
			}
			
		}
		return ris;
	}
	
	
	
	private List<Triple> getFrom(Vector<Vector<String>> triples){
		List<Triple> ris = new ArrayList<Triple>();
		if (triples != null){
			for(int i=0; i<triples.size() ; i++ ){
				Vector<String> t=triples.get(i);
				String temp[] = t.get(0).split("#");
				String nameSpace ="";
				String s = "";
				String o = "";
				String p = "";
				if(temp.length>1) {
					nameSpace = temp[0];
					s = temp[1];
				}else {
					s = temp[0];
				}
				if(nameSpace=="") {
					p = t.get(1);
					o = t.get(2);
				}else {
					p = t.get(1).split("#")[1];
					o = t.get(2).split("#")[1];
				}				
				//la kp non consente di ottenere SubjectType
				ris.add(new Triple(nameSpace,s,p,o,defaultSubjectType,t.get(3)));
			}
		}
		return ris;
	}
}
