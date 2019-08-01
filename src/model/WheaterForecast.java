package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import persistence.sib.Ontology;
import persistence.sib.Triple;
import presentation.IObserver;

public class WheaterForecast implements ISubject<SingleForecast>{
	private static WheaterForecast _instance = null;
	private BlockingQueue<SingleForecast> _queue;
	private ArrayList<IObserver<SingleForecast>> _observer;
	
	private WheaterForecast(){
		_queue = new ArrayBlockingQueue<>(3);
		_observer = new ArrayList<>();
		New();
	}
	
	private void New(){
		Random random = new Random();
		for(int i = 0; i < 3; i++){
			Wheater[] wheater = new Wheater[12];
			for(int j = 0; j < 12; j++){
				wheater[j] = Wheater.valueOf(random.nextInt(3));
			}
			addNewForecast(wheater, Document.GetInstance().getTime().toLocalDate().plusDays(i));
		}
	}
	
	public static WheaterForecast GetInstance(){
		if(_instance == null) _instance = new WheaterForecast();
		return _instance;
	}
	
	//insert the new Forecast at the end of the queue (tail), before remove the old forecast
	public void addNewForecast(Wheater[] forecast, LocalDate date){
		if(_queue.remainingCapacity() == 0 ) _queue.poll();
		_queue.add(new SingleForecast(forecast, date));
		notifyObserver();
	}
	
	public BlockingQueue<SingleForecast> getForecast(){
		return _queue;
	}
	
	public Wheater getWheater(LocalTime time){
		return _queue.peek().get(time);
	}

	@Override
	public void attachObserver(IObserver<SingleForecast> observer) {
		for(SingleForecast f : _queue)
			observer.update(f);
		_observer.add(observer);
	}

	@Override
	public void detachObserver(IObserver<SingleForecast> observer) {
		_observer.remove(observer);
	}

	@Override
	public void notifyObserver() {
		SingleForecast[] arr = _queue.toArray(new SingleForecast[0]);
		for(IObserver<SingleForecast> o : _observer)
			o.update(arr[_queue.size() - 1]);
	}
	
	public void reset() {
		New();
	}
	
	public List<Triple> toTriple(String parent,String predicateOfParent){
	
		
		List<Triple> ris = new ArrayList<Triple>();
		int counter = 0;
		for (Iterator<SingleForecast> i = this._queue.iterator(); i.hasNext();) {	
			String s = Ontology.APP_NS+ "SF_"+counter;
			counter++;
			ris.add(new Triple(parent,predicateOfParent,s));
			ris.addAll(i.next().toTriple(s));
		}		
		return ris;
	}
}
