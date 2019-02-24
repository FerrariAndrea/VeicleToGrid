package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import persistence.Ontology;
import persistence.Triple;
import utils.Utilities;

public class Reserving implements Comparable<Reserving>{
	private RegisteredUser user;
	private LocalDateTime startTimeReserving;
	private LocalDateTime endTimeReserving;
	private int minCharge;
	private ParkingSpace parkingSpace;
	
	public Reserving(RegisteredUser user, LocalDateTime startTimeReserving, LocalDateTime endTimeReserving, int minCharge, ParkingSpace p){
		this.user = user;
		this.startTimeReserving = startTimeReserving;
		this.endTimeReserving = endTimeReserving;
		this.minCharge = minCharge;
		this.parkingSpace = p;
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
	
	public List<Triple> toTriple(String nameSpace){
		List<Triple> ris = new ArrayList<Triple>();
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.HasUser,this.user.getNickname()));
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.StartTime,Utilities.getTimeStamp(this.startTimeReserving)));
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.EndTime,Utilities.getTimeStamp(this.endTimeReserving)));
		ris.add(new Triple(nameSpace,getTripleSubject(),Ontology.MinCharge,Integer.toString(minCharge)));
		//ris.addAll(this.parkingSpace.toTriple(nameSpace)); RIDONDANTE
		return ris;
	}
	public String getTripleSubject(){
		return "Reserving_" + this.user+"_"+Utilities.getTimeStamp(this.startTimeReserving);
	}
}
