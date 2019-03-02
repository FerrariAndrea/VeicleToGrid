package persistence;

import java.util.Vector;

public class Triple {

	//----------------------------------Triple
	private String _nameSpace;
	private String _subject;
	private String _predicate;
	private String _object;
	private String _subjectType;
	private String _objectType;
	
	public Triple(String _nameSpace, String _subject, String _predicate, String _object, String _subjectType,String _objectType) {
		this._nameSpace = _nameSpace.replaceAll("#", "")+"#";
		this._subject = _subject.replaceAll("#", "");
		this._predicate = _predicate.replaceAll("#", "");
		this._object = _object.replaceAll("#", "");
		this._subjectType = _subjectType;
		this._objectType = _objectType;
	}

	public String get_nameSpace() {
		return _nameSpace;
	}
	public void set_nameSpace(String _nameSpace) {
		this._nameSpace = _nameSpace;
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

	public Vector<String> toVector() {
		Vector<String> triple = new Vector<String>();
		triple.add(_nameSpace+ _subject);
		triple.add(_nameSpace+ _predicate);
		triple.add(_nameSpace+ _object);
		triple.add(_subjectType);
		triple.add(_objectType);
		return triple;
	}
	/*
	@Override
	public String toString() {
		return "<"+_nameSpace+_subject+":"+_subjectType+"><"+_nameSpace+_predicate+"><"+_nameSpace+_object+":"+_objectType+">";		
		
	}
	*/
	@Override
	public String toString() {
		return "<"+_nameSpace+_subject+"><"+_nameSpace+_predicate+"><"+_nameSpace+_object+">";		
		
	}
	
}
