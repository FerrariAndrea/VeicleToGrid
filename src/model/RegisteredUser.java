package model;

import java.util.ArrayList;
import java.util.List;

import persistence.sib.Ontology;
import persistence.sib.Triple;
import utils.Utilities;

public class RegisteredUser {
	private String _email;
	private String _password;
	private String _nickname;
	
	
	public RegisteredUser(String email, String password, String nickname){
		this._email = email;
		this._password = password;
		this._nickname = nickname;
	}
	
	public String getEmail() {
		return _email;
	}


	public String getPassword() {
		return _password;
	}


	public String getNickname() {
		return _nickname;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null) return false;
		if(obj == this) return false;
		if(! (obj instanceof RegisteredUser)) return false;
		
		RegisteredUser other = (RegisteredUser)obj;
		if(other.getEmail().equals(this._email)) return true;
		return false;
	}
	
	@Override
	public String toString(){
		return this._nickname;			
	}
	public List<Triple> toTriple(String parent,String predicateOfParent){
		List<Triple> ris = new ArrayList<Triple>();
		String s = Ontology.APP_NS+ getTripleSubject();
		ris.add(new Triple(parent,predicateOfParent,s));
		
		ris.add(new Triple(s,Ontology.vtg_Nickname,this._nickname));
		ris.add(new Triple(s,Ontology.vtg_Password,this._password));
		ris.add(new Triple(s,Ontology.vtg_hasEmail,this._email));
		
		
		return ris;
	}
	public String getTripleSubject(){
		return "User_"+this._nickname;
	}
}
