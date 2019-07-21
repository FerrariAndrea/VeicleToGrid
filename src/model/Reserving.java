package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import persistence.sib.Ontology;
import persistence.sib.Triple;
import utils.BrokerId;
import utils.Utilities;

public class Reserving implements Comparable<Reserving>{
	private RegisteredUser user;
	private LocalDateTime startTimeReserving;
	private LocalDateTime endTimeReserving;
	private int minCharge;//deprecato
	private ParkingSpace parkingSpace;
	private int id;
	

	public Reserving(RegisteredUser user, LocalDateTime startTimeReserving, LocalDateTime endTimeReserving, int minCharge, ParkingSpace p){
		this.user = user;
		this.startTimeReserving = startTimeReserving;
		this.endTimeReserving = endTimeReserving;
		this.minCharge = minCharge;
		this.parkingSpace = p;
		this.id = BrokerId.getInstance().getNextId();
	}
	
	public int getId() {
		return id;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public LocalDateTime getStartTimeReserving() {
		return startTimeReserving;
	}

	public LocalDateTime getEndTimeReserving() {
		return endTimeReserving;
	}

	public int getMinCharge() {
		return minCharge;
	}
	
	public ParkingSpace getParkingSpace(){
		return parkingSpace;
	}

	@Override
	public int compareTo(Reserving o) {
		return startTimeReserving.compareTo(o.getStartTimeReserving());
	}
	
	public List<Triple> toTriple(String nameSpace,String timestamp){
		String screen = this.getTripleScreenSubject(timestamp);
		List<Triple> ris = new ArrayList<Triple>();
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.HasScreen,screen,this.getClass().getName(),this.getClass().getName()));
		ris.add(new Triple(nameSpace,screen,Ontology.HasUser,this.user.getNickname(),this.getClass().getName(),String.class.getName()));
		ris.add(new Triple(nameSpace,screen,Ontology.StartTime,Utilities.getTimeStamp(this.startTimeReserving),this.getClass().getName(),this.startTimeReserving.getClass().getName()));
		ris.add(new Triple(nameSpace,screen,Ontology.EndTime,Utilities.getTimeStamp(this.endTimeReserving),this.getClass().getName(),this.endTimeReserving.getClass().getName()));
		ris.add(new Triple(nameSpace,screen,Ontology.MinCharge,Integer.toString(minCharge),this.getClass().getName(),Integer.class.getName()));
		//ris.addAll(this.parkingSpace.toTriple(nameSpace)); RIDONDANTE
		return ris;
	}
	public String getTripleSubject(){
		return "Reserving_" + this.id;
	}
	public String getTripleScreenSubject(String timestamp){
		return getTripleSubject()+"_"+timestamp;
	}
}
