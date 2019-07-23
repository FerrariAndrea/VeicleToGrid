package persistence.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.standard.DateTimeAtCompleted;

import model.SingleForecast.TimeSlot;
import persistence.sib.KPConnector;
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
				if(_newNameForNewInsert) {
					name+="_"+System.currentTimeMillis()+".txt";
				}else {
					name+=".txt";
				}
			    BufferedWriter writer = new BufferedWriter(new FileWriter(name, true));
			    for (Iterator<Triple> i = t.iterator(); i.hasNext();) {	
			    	writer.append(i.next().toStringForTurtle()+"\n");
			    }
			    
			    writer.close();
			
		}
		
		
		
}
