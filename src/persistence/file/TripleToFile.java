package persistence.file;

import java.util.List;

import persistence.sib.KPConnector;
import persistence.sib.Triple;

public class TripleToFile {
		//-----------------default setting
		private static String default_file_name = "VeicleToGridProgect";		
		private static String default_file_path = "";		
		
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
		private TripleToFile(String fullpath) {
			_fullpath=fullpath;
		}
		
		public void insert(List<Triple> t) throws Exception {
		
		}
		
}
