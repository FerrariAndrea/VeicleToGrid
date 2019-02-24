package model;

import presentation.IObserver;

public interface ISubject<T> {

	public void attachObserver(IObserver<T> observer);
	public void detachObserver(IObserver<T> observer);
	public void notifyObserver();
}
