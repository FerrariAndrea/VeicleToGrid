package model;

public class Log {
	private String _message;
	
	protected Log(String message){
		this._message = message;
	}
	
	public String getLog(){
		return _message;
	}
}
