package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import persistence.Ontology;
import persistence.Triple;
import utils.BrokerId;
import utils.Utilities;

public class SingleForecast {
	private Map<TimeSlot, Wheater> _map;
	private LocalDate _date;
	private int _id;
	
	
	public SingleForecast(Wheater[] forecasts, LocalDate date){
		if(forecasts.length != 12) throw new IllegalArgumentException("The argument must have 12 elements");
		_map = new HashMap<>();
		for(int i = 0; i < forecasts.length; i++){
			LocalTime start = LocalTime.of(2*i, 0);
			LocalTime end = null;
			if(i != 11) end = LocalTime.of((i+1)*2, 0).minusMinutes(1);
			else end = LocalTime.of(23, 59);
			_map.put(new TimeSlot(start, end), forecasts[i]);
		}
		this._date = date;
		this._id = BrokerId.getInstance().getNextId();
	}
	
	public int get_id() {
		return _id;
	}

	public Wheater get(LocalTime time){
		for(TimeSlot f : _map.keySet()){
			if(f.contain(time)) return _map.get(f);
		}
		throw new IllegalArgumentException("time is not correct");
	}
	
	public LocalDate getDate(){
		return _date;
	}
	
	public Map<TimeSlot, Wheater> getForecast(){
		return _map;
	}
	
	public class TimeSlot{
		private LocalTime _start;
		private LocalTime _end;
		private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		
		public TimeSlot(LocalTime start, LocalTime end){
			this._start = start;
			this._end = end;
		}
		
		public boolean contain(LocalTime time){
			if((_start.isBefore(time) || _start.equals(time)) && (_end.isAfter(time) || _end.equals(time)) ) return true;
			return false;
		}
		
		public LocalTime getStartTime(){
			return _start;
		}
		
		public LocalTime getEndTime(){
			return _end;
		}
		
		@Override
		public String toString(){
			return dtf.format(_start) + " - " + dtf.format(_end);
		}
		
		
		
	}
	
	public List<Triple> toTriple(String nameSpace,String timestamp){
		List<Triple> ris = new ArrayList<Triple>();
		String screen = getTripleScreenSubject(timestamp);
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.HasScreen,screen,this.getClass().getName(),this.getClass().getName()));
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.HasDate,Utilities.getTimeStamp(this._date),this.getClass().getName(),this._date.getClass().getName()));
		for (Iterator<TimeSlot> i = _map.keySet().iterator(); i.hasNext();) {	
			TimeSlot temp =i.next();
			ris.add(new Triple(nameSpace,screen,Ontology.HasWheater,getTripleWheaterSubject(temp,timestamp),this.getClass().getName(),temp.getClass().getName()));
			ris.add(new Triple(nameSpace,getTripleWheaterSubject(temp,timestamp),Ontology.At,_map.get(temp).toString(),temp.getClass().getName(),temp.getClass().getName()));
			ris.add(new Triple(nameSpace,getTripleWheaterSubject(temp,timestamp),Ontology.Is,_map.get(temp).toString(),temp.getClass().getName(),_map.get(temp).getClass().getName()));
		}		
		return ris;
	}
	public String getTripleSubject(){
		return "Forecast_" + this._id;
	}
	public String getTripleScreenSubject(String timestamp){
		return getTripleSubject()+"_"+timestamp;
	}
	public String getTripleWheaterSubject(TimeSlot ts ,String timestamp) {
		return getTripleScreenSubject(timestamp)+"_"+Utilities.getTimeStamp(ts.getStartTime());
	}
}
