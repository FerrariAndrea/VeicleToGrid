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
	
	public List<Triple> toTriple(String parent,String predicateOfParent){
		List<Triple> ris = new ArrayList<Triple>();
		String s = Ontology.APP_NS+ getTripleSubject();
		ris.add(new Triple(parent,predicateOfParent,s));
		
		ris.add(new Triple(s,Ontology.vtg_startTimeReserving,Utilities.getTimeStamp(this.startTimeReserving)));
		ris.add(new Triple(s,Ontology.vtg_endTimeReserving,Utilities.getTimeStamp(this.endTimeReserving)));
		ris.add(new Triple(s,Ontology.vtg_owner,user.getTripleSubject()));
		ris.add(new Triple(s,Ontology.vtg_isReferedTo,parkingSpace.getTripleSubject()));
		
		
		return ris;
	}
	public String getTripleSubject(){
		return "Reserving_" + this.id;
	}
}
