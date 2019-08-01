package persistence.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.standard.DateTimeAtCompleted;

import model.SingleForecast.TimeSlot;
import persistence.sib.KPConnector;
import persistence.sib.Ontology;
import persistence.sib.Triple;

public class TripleToFile {
		//-----------------default setting
		private static String default_file_name = "VeicleToGridProgect";		
		private static String default_file_path = "";		
		private static String indent ="\t";
		
		//-----------------Istance of singleton
		private static TripleToFile _instance=null;	
		
		public static TripleToFile GetInstance(String fullFilePath){
			if(_instance == null) _instance = new TripleToFile(fullFilePath);
			return _instance;
		}
		public static TripleToFile GetInstance(){
			if(_instance == null) _instance = new TripleToFile(default_file_path+default_file_name);
			return _instance;
		}
		
		
		//-------------------------------------
		private String _fullpath;
		private boolean _newNameForNewInsert =true;
		
		private TripleToFile(String fullpath) {
			_fullpath=fullpath;
		}
		
		
		public String getFullpath() {
			return _fullpath;
		}
		public void setFullpath(String _fullpath) {
			this._fullpath = _fullpath;
		}
		public boolean isNewNameForNewInsert() {
			return _newNameForNewInsert;
		}
		public void setNewNameForNewInsert(boolean _newNameForNewInsert) {
			this._newNameForNewInsert = _newNameForNewInsert;
		}
		

		public void insert(List<Triple> t) throws Exception {
				String name = _fullpath ;
				HashMap<String,List<Triple>> sub = subdivideBySubject(t);

				if(_newNameForNewInsert) {
					name+="_"+System.currentTimeMillis()+".ttl";
				}else {
					name+=".ttl";
				}
			    BufferedWriter writer = new BufferedWriter(new FileWriter(name, true));
			   //prefixs
			    for(Iterator<String> keys = Ontology._prefixsRef.keySet().iterator();keys.hasNext();) {
			    	String prefix = keys.next();
			    	writer.append("@prefix " + prefix + ": <"+Ontology.resolvePrefix(prefix)+">\n");			    	
			    }
				writer.append("\n\n\n");	
			    //triples
				 for (Iterator<String> keys= sub.keySet().iterator(); keys.hasNext();) {
					 List<Triple> tempList= sub.get(keys.next());
					 for(int x=0;x<tempList.size();x++) {
						 if(x==0) {
							 writer.append(tempList.get(x).toStringForTurtle());
							 if(tempList.size()>1) {
								 writer.append(";\n");
							 }else {
								 writer.append(".\n");//no more triple for this subject
							 }
						 }else {//more triple for this subject
							 writer.append("\t"+tempList.get(x).toStringForTurtlePredAndObj()+".\n");								
						 }
					 }
				 }
				
			  
			    
			    writer.close();
			
		}
		
		
		
		private HashMap<String,List<Triple>> subdivideBySubject(List<Triple> t){
			HashMap<String, List<Triple>> ris = new HashMap<String, List<Triple>>();
			
			 for (Iterator<Triple> i = t.iterator(); i.hasNext();) {
				 Triple temp = i.next();
				 String s = temp.toStringForTurtleSubject();
				 if(ris.containsKey(s)) {
					 ris.get(s).add(temp);
				 }else {
					 List<Triple> tempList= new ArrayList<Triple>();
					 tempList.add(temp);
					 ris.put(s, tempList);
				 }
			  }
			    
			
			return ris;
		}
		
		
}
