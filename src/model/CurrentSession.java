package model;

import java.util.ArrayList;

import presentation.IObserver;

public class CurrentSession implements ISubject<RegisteredUser>{
	private static CurrentSession _instance = null;
	private State _stato = new NotRegistered();
	private ArrayList<IObserver<RegisteredUser>> _observer = new ArrayList<>();
	
	public static CurrentSession GetInstance(){
		if (_instance == null) _instance = new CurrentSession();
		return _instance;
	}
	
	public void Login(String email, String password){
		_stato = _stato.Login(email, password);
		if(_stato.getRegisteredUser() != null) notifyObserver();
	}
	
	public void Logout(){
		_stato = _stato.Logout();
		notifyObserver();
	}
	
	public void Registration(String email, String password, String nickname){
		_stato = _stato.Registration(email, password, nickname);
		if(_stato.getRegisteredUser() != null) notifyObserver();
	}
	
	public RegisteredUser getRegisteredUser(){
		return _stato.getRegisteredUser();
	}
	
	@Override
	public void attachObserver(IObserver<RegisteredUser> observer) {
		_observer.add(observer);
	}

	@Override
	public void detachObserver(IObserver<RegisteredUser> observer) {
		_observer.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for(IObserver<RegisteredUser> o : _observer)
			o.update(_stato.getRegisteredUser());
	}
	
	private abstract class State{
		public abstract State Login(String email, String password);
		public abstract State Logout();
		public abstract State Registration(String email, String password, String nickname);
		public abstract RegisteredUser getRegisteredUser();
	}
	
	private class Registered extends State{
		private RegisteredUser _user;

		public Registered(RegisteredUser user){
			this._user = user;
		}
		
		@Override
		public State Login(String email, String password) {
			throw new UnsupportedOperationException("");
		}

		@Override
		public State Logout() {
			return new NotRegistered();
		}

		@Override
		public State Registration(String email, String password, String nickname) {
			throw new UnsupportedOperationException("");
		}

		@Override
		public RegisteredUser getRegisteredUser() {
			return _user;
		}
		
	}
	
	private class NotRegistered extends State{

		@Override
		public State Login(String email, String password) {
			State state = this;
			
			for(RegisteredUser user : Document.GetInstance().getRegisteredUser()){
				if (user.getEmail().equals(email) && user.getPassword().equals(password)){ 
					state = new Registered(user);
					break;
				}
			}
			
			return state;
		}

		@Override
		public State Logout() {
			throw new UnsupportedOperationException("");
		}

		@Override
		public State Registration(String email, String password, String nickname) {
			
			for(RegisteredUser user : Document.GetInstance().getRegisteredUser()){
				if(user.getEmail().equals(email)) throw new IllegalArgumentException("There is another registered user with the same email");
			}
			
			RegisteredUser user = new RegisteredUser(email, password, nickname);
			Document.GetInstance().getRegisteredUser().add(user);
			return new Registered(user);
			
		}

		@Override
		public RegisteredUser getRegisteredUser() {
			return null;
		}
		
	}
}
