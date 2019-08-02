package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import presentation.IObserver;

public class ContainerEvent implements ISubject<Log>{
	private static ContainerEvent _instance = null;
	private ArrayList<IEvent> _randomEvent;
	private Lock _lockEvent, _lockSem;
	private ArrayList<MySemaphore> _sem;
	private ArrayList<IObserver<Log>> _observer = new ArrayList<>();
	private ArrayList<Log> _logs = new ArrayList<>();
	
	private ContainerEvent(){
		_randomEvent = new ArrayList<>();
		_lockEvent = new ReentrantLock();
		_lockSem = new ReentrantLock();
		_sem = new ArrayList<>();
	}
	
	public static ContainerEvent GetInstance(){
		if(_instance == null) _instance = new ContainerEvent();
		return _instance;
	}
	
	public void addEvent(IEvent event){
		_lockEvent.lock();
		_randomEvent.add(event);
		_lockEvent.unlock();
	}
	
	public void addSemaphore(MySemaphore sem){
		_lockSem.lock();
		_sem.add(sem);
		_lockSem.unlock();
	}
	
	public void attivateSemaphore(){
		_lockSem.lock();
		
		for(MySemaphore s : _sem)
			if(s.isReady()){
				s.release();
		}
		
		for(Iterator<MySemaphore> iter = _sem.listIterator(); iter.hasNext(); ){
			MySemaphore s = iter.next();
			if(s.isReady()){ 
				iter.remove();
			}
		}
		
		_lockSem.unlock();
	}
	
	public void managementEvent(){
		_lockEvent.lock();
		
		_logs.clear();
		for(IEvent e : _randomEvent){
			Log l = e.operation();
			_logs.add(l);
		}
		
		notifyObserver();
		
		_randomEvent.clear();
		
		_lockEvent.unlock();
	}

	@Override
	public void attachObserver(IObserver<Log> observer) {
		_observer.add(observer);
	}

	@Override
	public void detachObserver(IObserver<Log> observer) {
		_observer.remove(observer);
	}

	@Override
	public void notifyObserver() {
		for(IObserver<Log> o : _observer){
			for(Log l : _logs)
				o.update(l);
		}
	}
	
	public void exit() {
		_lockSem.lock();
		
		for(MySemaphore s : _sem)
			s.release();
		_sem.clear();
		
		_lockSem.unlock();
	}
	
	public void reset() {
		_lockEvent.lock();
		//reset eventi futuri
		_randomEvent.clear();
		
		//reset Semafori
		exit();
		
		//reset log
		_logs.clear();
		
		_lockEvent.unlock();
	}
	
}
