package persistence.sib;

import java.util.Vector;

public class Triple {

	//public static String SEPARATOR = "#";
	
	//----------------------------------Triple
	//private String _nameSpace; -----------deprecato
	private String _subject;
	private String _predicate;
	private String _object;
	private String _subjectType;
	private String _objectType;
	private String _subjectNS;
	private String _predicateNS;
	private String _objectNS;
	

	

	public Triple(String _subjectPrefix,String _subject,String _predicatePrefix, String _predicate, String _objectPrefix, String _object, String _subjectType, String _objectType
			 ) {
	
	
		this._subject = _subject;
		this._predicate = _predicate;
		this._object = _object;
		this._subjectType = _subjectType;
		this._objectType = _objectType;
		this._subjectNS = _subjectPrefix;
		this._predicateNS = _predicatePrefix;
		this._objectNS = _objectPrefix;
	}
	//FAST TRIPLE
	public Triple(String _subject,String _predicate,String _object,String _objectType) {
		
		if(_subject.contains(":") && Ontology.isValidPrefix(_subject.split(":")[0])) {
			this._subject = _subject.split(":")[1];
			this._subjectType = Ontology.RESOURCE;	//this._subjectType = Ontology.resolveType(_subject.split(":")[0],this._subject);
			this._subjectNS = _subject.split(":")[0];//as Prefix 
		}else {
			this._subject = _subject;
			this._subjectType = Ontology.RESOURCE;		
			this._subjectNS ="";
		}
		if(_predicate.contains(":") && Ontology.isValidPrefix(_predicate.split(":")[0])) {
			this._predicate = _predicate.split(":")[1];
			this._predicateNS = _predicate.split(":")[0];//as Prefix 
		}else {
			this._predicate = _predicate;
			this._predicateNS = "";			
		}
		if(_object.contains(":") && Ontology.isValidPrefix(_object.split(":")[0])) {
			this._object = _object.split(":")[1];
			this._objectType =_objectType;
			//this._objectType = Ontology.resolveType(_predicate);			
			this._objectNS = _object.split(":")[0];//as Prefix 
		}else {
			this._object = _object;
			this._objectType =_objectType;// Ontology.LITERAL;		
			this._objectNS ="";
		}
	
	}
public Triple(String _subject,String _predicate,String _object) {
		
		if(_subject.contains(":") && Ontology.isValidPrefix(_subject.split(":")[0])) {
			this._subject = _subject.split(":")[1];
			this._subjectType = Ontology.RESOURCE;	// this._subjectType = Ontology.resolveType(_subject.split(":")[0],this._subject);
			this._subjectNS = _subject.split(":")[0];//as Prefix 
		}else {
			this._subject = _subject;
			this._subjectType = Ontology.RESOURCE;		
			this._subjectNS ="";
		}
		if(_predicate.contains(":") && Ontology.isValidPrefix(_predicate.split(":")[0])) {
			this._predicate = _predicate.split(":")[1];
			this._predicateNS = _predicate.split(":")[0];//as Prefix 
		}else {
			this._predicate = _predicate;
			this._predicateNS = "";			
		}
		if(_object.contains(":") && Ontology.isValidPrefix(_object.split(":")[0])) {
			this._object = _object.split(":")[1];
			this._objectType = Ontology.resolveObjectType(_predicate);	
			this._objectNS = _object.split(":")[0];//as Prefix 
		}else {
			this._object = _object;
			this._objectType = Ontology.resolveObjectType(_predicate);// Ontology.LITERAL;		
			this._objectNS ="";
		}
	
	}
	public Triple(String _subjectPrefix,String _subject,String _predicatePrefix, String _predicate, String _objectPrefix, String _object) {
	
	
		this._subject = _subject;
		this._predicate = _predicate;
		this._object = _object;
		this._subjectType = Ontology.RESOURCE;
		this._objectType = Ontology.resolveObjectType(_predicate);
		this._subjectNS = _subjectPrefix;
		this._predicateNS = _predicatePrefix;
		this._objectNS = _objectPrefix;
	}
	public String get_subject() {
		return _subject;
	}
	public void set_subject(String _subject) {
		this._subject = _subject;
	}
	public String get_predicate() {
		return _predicate;
	}
	public void set_predicate(String _predicate) {
		this._predicate = _predicate;
	}
	public String get_object() {
		return _object;
	}
	public void set_object(String _object) {
		this._object = _object;
	}
	public String get_subjectType() {
		return _subjectType;
	}
	public void set_subjectType(String _subjectType) {
		this._subjectType = _subjectType;
	}
	public String get_objectType() {
		return _objectType;
	}
	public void set_objectType(String _objectType) {
		this._objectType = _objectType;
	}
	public String get_subjectNS() {
		return _subjectNS;
	}
	public void set_subjectNS(String _subjectNS) {
		this._subjectNS = _subjectNS;
	}
	public String get_predicateNS() {
		return _predicateNS;
	}
	public void set_predicateNS(String _predicateNS) {
		this._predicateNS = _predicateNS;
	}
	public String get_objectNS() {
		return _objectNS;
	}
	public void set_objectNS(String _objectNS) {
		this._objectNS = _objectNS;
	}
	public Vector<String> toVector() {
		Vector<String> triple = new Vector<String>();
		if(Ontology.isValidPrefix(_subjectNS)) {
			triple.add( Ontology.resolvePrefix(_subjectNS) +Ontology.SEPARATOR_URI +_subject);
		}else {
			triple.add( _subjectNS +Ontology.SEPARATOR_URI +_subject);
		}
		if(Ontology.isValidPrefix(_predicateNS)) {
			triple.add(Ontology.resolvePrefix( _predicateNS) +Ontology.SEPARATOR_URI +_predicate);
		}else {
			triple.add( _predicateNS +Ontology.SEPARATOR_URI +_predicate);
		}
		
	if(_objectNS!="") {
		if(Ontology.isValidPrefix(_objectNS)) {
			triple.add(Ontology.resolvePrefix( _objectNS) +Ontology.SEPARATOR_URI +_object);
		}else {
			triple.add( _objectNS +Ontology.SEPARATOR_URI +_object);
		}
	}else {
		triple.add( _object);
	}
	
		
		triple.add(_subjectType);
		triple.add(_objectType);
		return triple;
	}
	/*
	public Vector<String> toVectorForTurtle(){
		Vector<String> triple = new Vector<String>();
		if(Ontology.isValidPrefix(_subjectNS)) {
			triple.add( _subjectNS +Ontology.SEPARATOR_PREFIX +_subject);
		}else {
			triple.add( _subjectNS +Ontology.SEPARATOR_URI +_subject);
		}
		if(Ontology.isValidPrefix(_predicateNS)) {
			triple.add( _predicateNS +Ontology.SEPARATOR_PREFIX +_predicate);
		}else {
			triple.add( _predicateNS +Ontology.SEPARATOR_URI +_predicate);
		}
		if(Ontology.isValidPrefix(_objectNS)) {
			triple.add( _objectNS +Ontology.SEPARATOR_PREFIX +_object);
		}else {
			triple.add( _objectNS +Ontology.SEPARATOR_URI +_object);
		}
		return triple;
	}
	*/
	public String toStringForTurtle(){

		String ris ="";
				
		if(Ontology.LITERAL == _subjectType) {//inutile un soggetto non può essere un literale
			ris+="'"+ _subject+"' ";		
		}else if(Ontology.RESOURCE ==_subjectType){
			if(_subjectNS!="") {
				if(Ontology.isValidPrefix(_predicateNS)) {
					ris+=_subjectNS +Ontology.SEPARATOR_PREFIX +_subject+ " ";
				}else {
					ris+="<"+ _subjectNS +Ontology.SEPARATOR_URI +_subject+"> ";
				}		
			}else {
				ris+="<"+_subject+ "> ";			
			}			
		}else {
			ris+="'"+ _subject+"'"+_subjectType+" ";
		}
	
	
		if(Ontology.isValidPrefix(_predicateNS)) {
			ris+=_predicateNS +Ontology.SEPARATOR_PREFIX +_predicate+ " ";
		}else {
			ris+="<"+ _predicateNS +Ontology.SEPARATOR_URI +_predicate+"> ";
		}
		
		if(Ontology.LITERAL == _objectType) {
			ris+="'"+ _object+"' ";			
		}else if(Ontology.RESOURCE ==_objectType){
			if(_objectNS!="") {
				if(Ontology.isValidPrefix(_predicateNS)) {
					ris+=_objectNS +Ontology.SEPARATOR_PREFIX +_object+ " ";
				}else {
					ris+="<"+ _objectNS +Ontology.SEPARATOR_URI +_object+"> ";
				}		
			}else {
				ris+="<"+_object+ "> ";			
			}			
		}else {
			ris+="'"+ _object+"'"+_objectType+" ";
		}
		return ris;
		
	}
	public String toStringForTurtleSubject(){
		String ris ="";
				
		if(Ontology.LITERAL == _subjectType) {//inutile un soggetto non può essere un literale
			ris+="'"+ _subject+"' ";		
		}else if(Ontology.RESOURCE ==_subjectType){
			if(_subjectNS!="") {
				if(Ontology.isValidPrefix(_predicateNS)) {
					ris+=_subjectNS +Ontology.SEPARATOR_PREFIX +_subject+ " ";
				}else {
					ris+="<"+ _subjectNS +Ontology.SEPARATOR_URI +_subject+"> ";
				}		
			}else {
				ris+="<"+_subject+ "> ";			
			}			
		}else {
			ris+="'"+ _subject+"'"+_subjectType+" ";
		}
		return ris;
		
	}
	
	public String toStringForTurtlePredAndObj(){
		String ris ="";
				

		
		if(Ontology.isValidPrefix(_predicateNS)) {
			ris+=_predicateNS +Ontology.SEPARATOR_PREFIX +_predicate+ " ";
		}else {
			ris+="<"+ _predicateNS +Ontology.SEPARATOR_URI +_predicate+"> ";
		}
		
		if(Ontology.LITERAL == _objectType) {
			ris+="'"+ _object+"' ";			
		}else if(Ontology.RESOURCE ==_objectType){
			if(_objectNS!="") {
				if(Ontology.isValidPrefix(_predicateNS)) {
					ris+=_objectNS +Ontology.SEPARATOR_PREFIX +_object+ " ";
				}else {
					ris+="<"+ _objectNS +Ontology.SEPARATOR_URI +_object+"> ";
				}		
			}else {
				ris+="<"+_object+ "> ";			
			}			
		}else {
			ris+="'"+ _object+"'"+_objectType+" ";
		}
		return ris;
		
	}
	/*
	@Override
	public String toString() {
		return "<"+_nameSpace+_subject+":"+_subjectType+"><"+_nameSpace+_predicate+"><"+_nameSpace+_object+":"+_objectType+">";		
		
	}
	*/
	@Override
	public String toString() {
		return "<"+_subject+"><"+_predicate+"><"+_object+">";		
		
	}
	
	
}
